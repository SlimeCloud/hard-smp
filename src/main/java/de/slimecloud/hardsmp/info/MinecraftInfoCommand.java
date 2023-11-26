package de.slimecloud.hardsmp.info;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.cyklon.spigotutils.adventure.Formatter;
import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.player.PlayerController;
import de.slimecloud.hardsmp.verify.Verification;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MinecraftInfoCommand implements CommandExecutor, TabCompleter {
	private final Cache<String, List<String>> cache = CacheBuilder.newBuilder()
			.expireAfterWrite(Duration.ofMinutes(10))
			.build();

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if(args.length == 0) return false;

		JDA jda = HardSMP.getInstance().getDiscordBot().jdaInstance;
		User user = null;

		try {
			user = jda.getUserById(args[0]);
		} catch(NumberFormatException ignored) {}

		try {
			if(user == null) user = jda.getGuildById(HardSMP.getInstance().getConfig().getLong("discord.guild")).getMembersByEffectiveName(args[0], true).get(0).getUser();
		} catch(NullPointerException | IndexOutOfBoundsException ignored) {}

		if(user == null) {
			sender.sendMessage(Component.text("Nutzer nicht gefunden!").color(NamedTextColor.RED));
			return true;
		}

		Verification verification = Verification.load(user);

		if(!verification.isVerified()) {
			sender.sendMessage(Component.text("Nutzer nicht gefunden!").color(NamedTextColor.RED));
			return true;
		}

		OfflinePlayer player = Bukkit.getOfflinePlayer(verification.getMinecraftID());

		sender.sendMessage(
				Formatter.parseText("§äInformationen zu §l§ö" + user.getEffectiveName() + "§r")
						.append(Formatter.parseText("§äMinecraft Name: §ö" + player.getName()))
						.append(Formatter.parseText("§äDiscord Name: §ö" + user.getEffectiveName()))
						.append(Formatter.parseText("§äPunkte: §ö: " + PlayerController.getPlayer(player).getActualPoints()))
		);

		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if(args.length != 1) return Collections.emptyList();

		try {
			return cache.get(args[0], () -> HardSMP.getInstance().getDiscordBot().jdaInstance.getGuildById(HardSMP.getInstance().getConfig().getLong("discord.guild")).getMembers().stream()
					.map(Member::getEffectiveName)
					.filter(u -> u.startsWith(args[0]))
					.toList()
			);
		} catch(ExecutionException e) {
			return Collections.emptyList();
		}
	}
}
