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

public class EventCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length < 2) return false;
        SubEvent event = HardSMP.getInstance().getSubEvents().getEvents().get(args[1]);
        switch (args[0]) {
            case "start" -> {
                sender.sendMessage(HardSMP.getPrefix().append(Component.text("Starte Event!", HardSMP.getInstance().getGreenColor())));
                event.start();

            }
            case "setup" -> {
                sender.sendMessage(HardSMP.getPrefix().append(Component.text("Lade Event!", HardSMP.getInstance().getGreenColor())));
                if (event == null) {
                    sender.sendMessage(HardSMP.getPrefix().append(Component.text("SubEvent '" + args[1] + "' wurde nicht gefunden!", NamedTextColor.RED)));
                    return true;
                }
                event.setup(Bukkit.getOnlinePlayers().stream().map(p -> (Player) p).toList());
                sender.sendMessage(HardSMP.getPrefix().append(Component.text("SubEvent '" + args[1] + "' erfolgreich gestartet!", HardSMP.getInstance().getGreenColor())));
                return true;
            }
            case "stop" -> {
                if (event == null) {
                    sender.sendMessage(HardSMP.getPrefix().append(Component.text("SubEvent '" + args[1] + "' wurde nicht gefunden!", NamedTextColor.RED)));
                    return true;
                }
                event.stop();
                sender.sendMessage(HardSMP.getPrefix().append(Component.text("SubEvent '" + args[1] + "' erfolgreich beendet!", HardSMP.getInstance().getGreenColor())));
                return true;
            }
            case "join" -> {
                event.join(Bukkit.getPlayer(args[2]));
                return true;
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        switch (args.length) {
            case 1 -> {
                list.add("start");
                list.add("setup");
                list.add("stop");
                list.add("join");
            }
            case 2 -> list.addAll(HardSMP.getInstance().getSubEvents().getEvents().keySet());
            case 3 -> {
                if (args[1].equals("join")) list = Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .filter(p -> p.startsWith(args[0]))
                            .toList();
            }
        }
        list.removeIf(s -> !s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()));
        return list;
    }
}
