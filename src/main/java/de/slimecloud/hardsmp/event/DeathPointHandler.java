package de.slimecloud.hardsmp.event;

import de.slimecloud.hardsmp.player.EventPlayer;
import de.slimecloud.hardsmp.player.PlayerController;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathPointHandler implements Listener {

    @EventHandler()
    private void onDeath(PlayerDeathEvent event) {
        EventPlayer player = PlayerController.getPlayer(Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId()));
        int points = event.getPlayer().getStatistic(Statistic.DEATHS) * 100;

        player.removePoints(points);
    }

}
