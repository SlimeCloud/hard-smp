package de.slimecloud.hardsmp.info;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.player.PlayerController;
import de.slimecloud.hardsmp.verify.Verification;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MinecraftInfoCommand implements CommandExecutor, TabCompleter {
	private final Cache<String, List<String>> cache = CacheBuilder.newBuilder()
			.expireAfterWrite(Duration.ofMinutes(10))
			.build();

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if(args.length != 2) return false;

		JDA jda = HardSMP.getInstance().getDiscordBot().jdaInstance;

		User user = null;
		OfflinePlayer player = null;

		switch(args[0]) {
			case "discord" -> {
				try {
					user = jda.getUserById(args[1]);
				} catch(NumberFormatException ignored) {
				}

				try {
					if(user == null) user = jda.getGuildById(HardSMP.getInstance().getConfig().getLong("discord.guild")).getMembersByEffectiveName(args[0], true).get(1).getUser();
				} catch(NullPointerException | IndexOutOfBoundsException ignored) {
				}

				if(user == null) {
					sender.sendMessage(Component.text("Nutzer nicht gefunden!").color(NamedTextColor.RED));
					return true;
				}

				Verification verification = Verification.load(user);

				if(!verification.isVerified()) {
					sender.sendMessage(Component.text("Nutzer nicht gefunden!").color(NamedTextColor.RED));
					return true;
				}

				player = Bukkit.getOfflinePlayer(verification.getMinecraftID());
			}

			case "minecraft" -> {
				try {
					player = Bukkit.getOfflinePlayer(UUID.fromString(args[1]));
				} catch(IllegalArgumentException ignored) {
				}

				if(player == null) player = Bukkit.getOfflinePlayer(args[1]);

				Verification verification = Verification.load(player.getUniqueId().toString());

				if(!verification.isVerified()) {
					sender.sendMessage(Component.text("Spieler nicht gefunden!").color(NamedTextColor.RED));
					return true;
				}

				user = jda.retrieveUserById(verification.getDiscordID()).complete();
			}

			default -> {
				return false;
			}
		}

		if(player.getName() == null) {
			sender.sendMessage(Component.text("Spieler nicht gefunden!").color(NamedTextColor.RED));
			return true;
		}

		sender.sendMessage(Component.text("Informationen zu ").color(TextColor.color(0x88D657)).append(Component.text(user.getEffectiveName()).color(TextColor.color(0xF6ED82))).appendNewline()
				.append(Component.text("Minecraft Name: ").color(TextColor.color(0x88D657)).append(Component.text(player.getName()).color(TextColor.color(0xF6ED82)))).appendNewline()
				.append(Component.text("Discord Name: ").color(TextColor.color(0x88D657)).append(Component.text(user.getEffectiveName()).color(TextColor.color(0xF6ED82)))).appendNewline()
				.append(Component.text("Punkte: ").color(TextColor.color(0x88D657)).append(Component.text(PlayerController.getPlayer(player).getActualPoints()).color(TextColor.color(0xF6ED82)))).appendNewline()
		);

		return true;
	}

	@Override
	@SneakyThrows
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if(args.length == 1) {
			return List.of("discord", "minecraft").stream()
					.filter(s -> s.startsWith(args[0]))
					.toList();
		}

		else if(args.length == 2) {
			switch(args[0]) {
				case "discord" -> {
					return cache.get(args[0], () -> HardSMP.getInstance().getDiscordBot().jdaInstance.getGuildById(HardSMP.getInstance().getConfig().getLong("discord.guild")).getMembers().stream()
							.map(Member::getEffectiveName)
							.filter(u -> u.startsWith(args[1]))
							.toList()
					);
				}

				case "minecraft" -> {
					return Bukkit.getOnlinePlayers().stream()
							.map(Player::getName)
							.filter(p -> p.startsWith(args[1]))
							.toList();
				}
			}
		}

		return Collections.emptyList();
	}
}
