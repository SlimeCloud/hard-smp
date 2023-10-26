package de.slimecloud.hardsmp.advancement.handler;

import de.slimecloud.hardsmp.advancement.AdvancementHandler;
import de.slimecloud.hardsmp.advancement.CustomAdvancement;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.plugin.Plugin;

public class Lightning extends AdvancementHandler {
    public Lightning(Plugin plugin) {
        super(plugin, CustomAdvancement.LIGHTNING);
    }

    @EventHandler
    public void onLightningBold(LightningStrikeEvent event) {
        Location location = event.getLightning().getLocation();
        Entity[] entities = location.getChunk().getEntities();
        for (Entity entity : entities) {
            if (entity.getLocation().getBlockX()==location.getBlockX() && entity.getLocation().getBlockZ()==location.getBlockZ() && (entity.getLocation().getBlockY()==location.getBlockY() || entity.getLocation().getBlockY()-1==location.getBlockY())) {

            }
        }
    }
}
