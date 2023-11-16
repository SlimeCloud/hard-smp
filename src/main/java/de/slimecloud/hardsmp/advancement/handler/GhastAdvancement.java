package de.slimecloud.hardsmp.advancement.handler;

import de.cyklon.spigotutils.persistence.PersistentDataHandler;
import de.slimecloud.hardsmp.advancement.AdvancementHandler;
import de.slimecloud.hardsmp.advancement.CustomAdvancement;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.generator.structure.StructureType;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StructureSearchResult;

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
            if (killer != null) {
                if (isDone(killer)) return;
                Location location = killer.getLocation();
                StructureSearchResult result = location.getWorld().locateNearestStructure(location, StructureType.FORTRESS, 4, false);
                if (result != null) {
                    int i = PersistentDataHandler.get(killer).reviseIntWithDefault(key, c -> ++c, 0);
                    if (i >= 5) unlock(killer);
                }
            }
        }
    }
}
