package de.slimecloud.hardsmp.ui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class AdvancementMessage implements Listener {
    @EventHandler
    private void onAdvancement(PlayerAdvancementDoneEvent event) {
        if(event.getAdvancement().getDisplay() == null) return;

        event.message(Chat.getName(event.getPlayer())
                .append(Component.text(" hat das Advancement ").color(NamedTextColor.GRAY))
                .append(event.getAdvancement().getDisplay().displayName())
                .append(Component.text(" bekommen").color(NamedTextColor.GRAY))
        );
    }
}
