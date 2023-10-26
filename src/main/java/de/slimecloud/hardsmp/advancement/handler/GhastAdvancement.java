package de.slimecloud.hardsmp.advancement.handler;

import de.slimecloud.hardsmp.advancement.AdvancementHandler;
import de.slimecloud.hardsmp.advancement.CustomAdvancement;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Biome;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class GhastAdvancement extends AdvancementHandler {
    private final NamespacedKey key;
    public GhastAdvancement(Plugin plugin) {
        super(plugin, CustomAdvancement.GHAST);
        this.key = new NamespacedKey(plugin, "killed.ghast.nether");
    }

    @EventHandler
    public void onEntityKill(EntityDeathEvent event) {
        if (event.getEntity() instanceof Ghast ghast) {
            Player killer = ghast.getKiller();
            if (killer!=null) {
                if (killer.getLocation().getBlock().getBiome().equals(Biome.NETHER_WASTES)) {
                    PersistentDataContainer container = killer.getPersistentDataContainer();
                    int c = 0;
                    if (container.has(key)) c = container.get(key, PersistentDataType.INTEGER);
                    container.set(key, PersistentDataType.INTEGER, ++c);
                    if (c>=5) unlock(killer);
                }
            }
        }
    }
}
