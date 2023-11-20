package de.slimecloud.hardsmp.commands;

import de.cyklon.spigotutils.adventure.Formatter;
import de.slimecloud.hardsmp.HardSMP;
import kotlin.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ClaimCommand implements CommandExecutor, TabCompleter {

    //private final List<Map.Entry<UUID, Pair<Location, Location>>> claimList;
    private Component msg;

    public ClaimCommand() {
        Map<UUID, Pair<Location, Location>> pointMap = new HashMap<>();
        ConfigurationSection section = HardSMP.getInstance().getConfig().getConfigurationSection("claims");
        if (section == null) {
            this.msg = Component.text("Es ist ein fehler aufgetreten.\nBitte melde dieses Problem dem support", NamedTextColor.RED);
        } else {
            for (String key : section.getKeys(false)) {

            }
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (args.length == 1) {
            if (args[0].equals("start")) {

            } else if (args[0].equals("finish")) {

            } else {
                commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("Benutzung: /claim [start/finish]!", NamedTextColor.RED)));
            }
        } else {
            commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("Benutzung: /claim [start/finish]!", NamedTextColor.RED)));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            list.add("start");
            list.add("finish");
        }
        list.removeIf(s -> !s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()));
        return list;
    }
}
