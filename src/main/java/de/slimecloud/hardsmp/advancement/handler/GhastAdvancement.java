package de.slimecloud.hardsmp.advancement.handler;

import de.slimecloud.hardsmp.advancement.AdvancementHandler;
import de.slimecloud.hardsmp.advancement.CustomAdvancement;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.Plugin;

public class GhastAdvancement extends AdvancementHandler {
    public GhastAdvancement(Plugin plugin) {
        super(plugin, CustomAdvancement.GHAST);
    }

    @EventHandler
    public void onEntityKill(EntityDeathEvent event) {

    }
}
