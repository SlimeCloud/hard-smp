package de.slimecloud.hardsmp.commands;

import de.cyklon.spigotutils.adventure.Formatter;
import de.slimecloud.hardsmp.HardSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class EnderchestCommand implements CommandExecutor {
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
}
