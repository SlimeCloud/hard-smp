package de.slimecloud.hardsmp.advancement.handler;

import de.slimecloud.hardsmp.advancement.AdvancementHandler;
import de.slimecloud.hardsmp.advancement.CustomAdvancement;
import de.slimecloud.hardsmp.event.PlayerVerifyEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;

public class RootAdvancement extends AdvancementHandler {
    public RootAdvancement(Plugin plugin) {
        super(plugin, CustomAdvancement.ROOT);
    }

    @EventHandler
    public void onVerify(PlayerVerifyEvent event) {
        unlock(event.getPlayer());
    }
}
