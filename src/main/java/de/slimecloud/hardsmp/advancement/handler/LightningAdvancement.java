package de.slimecloud.hardsmp.advancement.handler;

import de.slimecloud.hardsmp.advancement.AdvancementHandler;
import de.slimecloud.hardsmp.advancement.CustomAdvancement;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;

public class LightningAdvancement extends AdvancementHandler {
    public LightningAdvancement(Plugin plugin) {
        super(plugin, CustomAdvancement.LIGHTNING);
    }

    @EventHandler
    public void onEntityDamagedByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof LightningStrike) {
            if (event.getEntity() instanceof Player player) {
                if ((player.getHealth() - event.getFinalDamage()) > 1) unlock(player);
            }
        }
    }
}
