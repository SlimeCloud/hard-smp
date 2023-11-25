package de.slimecloud.hardsmp.info;

import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.commands.EmptyTabCompleter;
import de.slimecloud.hardsmp.verify.Verification;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class MinecraftInfoCommand implements CommandExecutor, EmptyTabCompleter {
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if(args.length == 0) return false;

		UserSnowflake user = null;

		try {
			user = UserSnowflake.fromId(args[0]);
		} catch(NumberFormatException ignored) {}

		try {
			if(user == null) user = HardSMP.getInstance().getDiscordBot().jdaInstance.getGuildById(0).getMembersByEffectiveName(args[0], true).get(0);
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

		//TODO send message

		return true;
	}
}
