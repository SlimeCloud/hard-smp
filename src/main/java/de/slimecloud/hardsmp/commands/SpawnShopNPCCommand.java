package de.slimecloud.hardsmp.commands;

import de.slimecloud.hardsmp.shop.SlimeNPC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SpawnShopNPCCommand implements CommandExecutor, EmptyTabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player player) {
            new SlimeNPC(player.getLocation());
            return true;
        }
        return false;
    }

}
