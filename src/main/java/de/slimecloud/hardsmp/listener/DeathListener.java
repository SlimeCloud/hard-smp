package de.slimecloud.hardsmp.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.getPlayer().banPlayer("Du bist gestorben");
    }

}
