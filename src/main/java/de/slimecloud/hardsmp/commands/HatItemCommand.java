package de.slimecloud.hardsmp.commands;

import de.cyklon.spigotutils.adventure.Formatter;
import de.slimecloud.hardsmp.HardSMP;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HatItemCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player player) {
            Player targetPlayer = player;
            if (args.length > 0) {
                Player tempPlayer = Bukkit.getPlayer(args[0]);
                if (tempPlayer != null) targetPlayer = tempPlayer;
                else {
                    sender.sendMessage(Formatter.parseText(HardSMP.getInstance().getConfig().getString("hatitem.playerNotFoundErrorMessage", "§cSpieler wurde nicht gefunden!")));
                    return true;
                }
            }
            targetPlayer.getInventory().setHelmet(player.getInventory().getItemInMainHand());
        } else sender.sendMessage("Der Command kann nur als Spieler ausgeführt werden!");

        return true;
    }
}
