package de.slimecloud.hardsmp.subevent.commands;

import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.subevent.SubEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StartEventCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length>=1) {
            SubEvent event = HardSMP.getInstance().getSubEvents().getEvents().get(args[0]);
            if (event==null) {
                sender.sendMessage(Component.text("SubEvent '" + args[0] + "' wurde nicht gefunden!", NamedTextColor.RED));
                return true;
            }
            event.start(Bukkit.getOnlinePlayers().stream().map(p -> (Player) p).toList());
            sender.sendMessage(Component.text("SubEvent '" + args[0] + "' erfolgreich gestartet!", NamedTextColor.GREEN));
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length==1) list.addAll(HardSMP.getInstance().getSubEvents().getEvents().keySet());
        list.removeIf(s -> !s.toLowerCase().startsWith(args[args.length-1].toLowerCase()));
        return list;
    }
}
