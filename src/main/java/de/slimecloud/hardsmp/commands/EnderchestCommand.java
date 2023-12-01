package de.slimecloud.hardsmp.commands;

import de.cyklon.spigotutils.adventure.Formatter;
import de.slimecloud.hardsmp.HardSMP;
import net.kyori.adventure.text.Component;
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

        if (sender instanceof Player player) {
            Player enderchestPlayer = player;
            if (args.length > 0) {
                Player targetPlayer = Bukkit.getPlayer(args[0]);
                if (targetPlayer != null) enderchestPlayer = targetPlayer;
                else {
                    sender.sendMessage(Formatter.parseText(HardSMP.getInstance().getConfig().getString("enderchest.playerNotFoundErrorMessage", "§cSpieler wurde nicht gefunden!")));
                    return true;
                }
            }
            player.openInventory(enderchestPlayer.getEnderChest());
        } else sender.sendMessage("Der Command kann nur als Spieler ausgeführt werden!");

        return true;
    }
}
