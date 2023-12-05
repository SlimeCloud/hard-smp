package de.slimecloud.hardsmp.commands;

import de.slimecloud.hardsmp.HardSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class EnderchestCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cCommand kann nur von einem Spieler ausgeführt werden");
            return true;
        }
        Player enderchestPlayer = player;
        if (args.length > 0) {
            if (!player.hasPermission("hardsmp.command.enderchest.other")) {
                player.sendMessage(HardSMP.getPrefix().append(Component.text("Du kannst die Enderchest anderer nicht öffnen!", NamedTextColor.RED)));
                return true;
            }
            enderchestPlayer = Bukkit.getPlayer(args[0]);
            if (enderchestPlayer == null) {
                player.sendMessage(HardSMP.getPrefix().append(Component.text("Spieler konnte nicht gefunden werden!", NamedTextColor.RED)));
                return true;
            }
        }
        player.openInventory(enderchestPlayer.getEnderChest()).setTitle("Enderchest von " + enderchestPlayer.getName());
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.hasPermission("hardsmp.command.enderchest.other") && args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(p -> p.startsWith(args[0]))
                    .toList();
        }

        return Collections.emptyList();
    }
}
