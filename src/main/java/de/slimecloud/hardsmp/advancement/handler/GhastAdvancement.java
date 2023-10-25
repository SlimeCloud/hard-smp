package de.slimecloud.hardsmp.advancement.handler;

import de.slimecloud.hardsmp.advancement.AdvancementHandler;
import de.slimecloud.hardsmp.advancement.CustomAdvancement;
import org.bukkit.block.Biome;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.Plugin;

public class GhastAdvancement extends AdvancementHandler {
    public GhastAdvancement(Plugin plugin) {
        super(plugin, CustomAdvancement.GHAST);
    }

    @EventHandler
    public void onEntityKill(EntityDeathEvent event) {
        if (event.getEntity() instanceof Ghast ghast) {
            Player killer = ghast.getKiller();
            if (killer!=null) {
                if (killer.getLocation().getBlock().getBiome().equals(Biome.NETHER_WASTES)) {

                }
            }
        }
    }
}
