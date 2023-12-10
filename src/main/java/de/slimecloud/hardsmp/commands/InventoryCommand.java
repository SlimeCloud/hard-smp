package de.slimecloud.hardsmp.commands;

import de.slimecloud.hardsmp.util.InventoryStorage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class InventoryCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String cmd, @NotNull String[] args) {
        if(args.length < 1) return false;
        if(!(commandSender instanceof Player player)) {
            commandSender.sendMessage("§cKann nur als Spieler ausgeführt werden");
            return true;
        }

        Player target = args.length >= 2 ? Bukkit.getPlayer(args[1]) : player;

        if(target == null) {
            player.sendMessage("§cSpieler nicht gefunden");
            return true;
        }

        return switch(args[0]) {
            case "save" -> {
                if(!InventoryStorage.saveInventory(target)) player.sendMessage("§cEs besteht bereits eine Zwischenspeicherung!");
                else {
                    target.getInventory().clear();
                    player.sendMessage("Inventar von " + target.getName() + " zwischengespeichert");
                }

                yield  true;
            }

            case "restore" -> {
                InventoryStorage.restoreInventory(target);
                player.sendMessage("Inventar wiederhergestellt");

                yield true;
            }

            default -> false;
        };
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String cmd, @NotNull String[] args) {
        if(args.length == 1) return Stream.of("save", "restore")
                .filter(s -> s.startsWith(args[0]))
                .toList();

        if(args.length == 2) return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(p -> p.startsWith(args[1]))
                .toList();

        return Collections.emptyList();
    }
}
