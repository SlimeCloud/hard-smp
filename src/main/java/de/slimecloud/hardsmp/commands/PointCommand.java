package de.slimecloud.hardsmp.commands;

import de.slimecloud.hardsmp.player.EventPlayer;
import de.slimecloud.hardsmp.player.PlayerController;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
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

public class PointCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2) return false;
        EventPlayer target = PlayerController.getPlayer(Bukkit.getOfflinePlayer(args[0]));
        if (args[1].equals("get")) {
            sender.sendMessage(Component.text(args[0] + ":")
                    .appendNewline()
                    .append(Component.text("points: ")
                            .append(Component.text(target.getPoints())
                                    .color(TextColor.color(50, 180, 200)))
                    )
                    .appendNewline()
                    .append(Component.text("actual points: ")
                            .append(Component.text(target.getActualPoints())
                                    .color(TextColor.color(50, 180, 200))
                            )
                    ));
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(Component.text(command.getUsage().replace("/get", "") + " <value>"));
            return true;
        }
        double value;
        try {
            value = Double.parseDouble(args[2].replace(",", "."));
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("§cFehler: value muss eine ganzzahl oder eine gleitkomma zahl sein."));
            return true;
        }
        switch (args[1]) {
            case "add" -> {
                target.addPoints(value);
                sender.sendMessage(Component.text(args[0] + " wurden erfolgreich ")
                        .color(TextColor.color(50, 200, 50))
                        .append(Component.text(value)
                                .color(TextColor.color(50, 180, 200)))
                        .append(Component.text(" punkte hinzugefügt")
                                .color(TextColor.color(50, 200, 50)))
                );
                return true;
            }
            case "set" -> {
                target.setPoints(value);
                sender.sendMessage(Component.text("Die punkte von " + args[1] + " wurden erfolgreich auf ")
                        .color(TextColor.color(50, 200, 50))
                        .append(Component.text(value)
                                .color(TextColor.color(50, 180, 200)))
                        .append(Component.text(" punkte gesetzt")
                                .color(TextColor.color(50, 200, 50)))
                );
                return true;
            }
            case "remove" -> {
                target.removePoints(value);
                sender.sendMessage(Component.text(args[0] + " wurden erfolgreich ")
                        .color(TextColor.color(50, 200, 50))
                        .append(Component.text(value)
                                .color(TextColor.color(50, 180, 200)))
                        .append(Component.text(" punkte entfernt")
                                .color(TextColor.color(50, 200, 50)))
                );
                return true;
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        switch (args.length) {
            case 1 -> list.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
            case 2 -> {
                list.add("add");
                list.add("set");
                list.add("get");
                list.add("remove");
            }
        }
        list.removeIf(s -> !s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()));
        return list;
    }
}
