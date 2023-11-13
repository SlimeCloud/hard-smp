package de.slimecloud.hardsmp.subevent.replika.commands;

import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.build.Build;
import de.slimecloud.hardsmp.subevent.replika.Replika;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BuildSchematicCommand implements CommandExecutor, TabCompleter {
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (sender instanceof Player player && args.length>=1) {
			Replika replika = HardSMP.getInstance().getSubEvents().getReplika();
			File file = replika.getFile(args[0]);
			if (!file.exists()) {
				sender.sendMessage(Component.text("Schematic '" + args[0] + "' wurde nicht gefunden", NamedTextColor.RED));
				return true;
			}
			Build build;
			try {
				build = Build.load(file);
				build.build(player.getLocation());
			} catch (IOException e) {
				e.printStackTrace();
				sender.sendMessage(Component.text("Es ist ein fehler aufgetreten:\n" + e.getMessage(), NamedTextColor.RED));
				return true;
			}
			sender.sendMessage(Component.text(build.getBlocks().size(), NamedTextColor.LIGHT_PURPLE)
					.appendSpace()
					.append(Component.text("Blöcke platziert", NamedTextColor.GREEN))
					.appendNewline()
					.append(Component.text("'" + args[0] + "' erfolgreich gebaut.", NamedTextColor.GREEN)));
			return true;
		}
		return false;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		List<String> list = new ArrayList<>();

		if (args.length==1) {
			Replika replika = HardSMP.getInstance().getSubEvents().getReplika();
			File[] files = replika.getDirectory().listFiles();
			if (files!=null) for (File file : files) {
				list.add(file.getName().replaceFirst("[.][^.]+$", ""));
			}
		}

		list.removeIf(s -> !s.toLowerCase().startsWith(args[args.length-1].toLowerCase()));
		return list;
	}
}
