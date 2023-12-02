package de.slimecloud.hardsmp.commands.spawn;

import de.slimecloud.hardsmp.HardSMP;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SpawnCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Location location = Bukkit.getWorld("world").getSpawnLocation();
        Player player = Bukkit.getPlayer(commandSender.getName());
        player.teleport(location);
        return true;
    }
}
