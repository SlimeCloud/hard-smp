package de.slimecloud.hardsmp.commands;

import de.slimecloud.hardsmp.HardSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public class SpawnCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Location location = Bukkit.getWorld("world").getSpawnLocation();
        Player player = Bukkit.getPlayer(commandSender.getName());
        player.sendMessage(HardSMP.getPrefix().append(Component.text("Du wirst in 3sek zum Spawn teleportiert!", TextColor.color(0x88d657))));
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                player.teleport(location);
            }
        }.runTaskLater(HardSMP.getInstance(), 60);

        return true;
    }
}
