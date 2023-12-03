package de.slimecloud.hardsmp.ui;

import de.cyklon.spigotutils.advancement.AdvancementType;
import de.slimecloud.hardsmp.player.data.PointCategory;
import de.slimecloud.hardsmp.player.data.PointsListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class AdvancementMessage implements Listener {
    @EventHandler
    private void onAdvancement(PlayerAdvancementDoneEvent event) {
        if(event.getAdvancement().getDisplay() == null) return;

        AdvancementType type = AdvancementType.getAdvancementType(event.getAdvancement());
        Integer rarity = PointsListener.ADVANCEMENTS_RARITY.get(type.getKey());
        if(rarity == null) return;

        event.message(Chat.getName(event.getPlayer())
                .append(Component.text(" hat das Advancement ").color(NamedTextColor.GRAY))
                .append(event.getAdvancement().getDisplay().displayName())
                .append(Component.text(" bekommen ").color(NamedTextColor.GRAY))
                .append(Component.text("[").color(NamedTextColor.DARK_GRAY))
                .append(Component.text(PointCategory.ADVANCEMENT.calculate(rarity)).color(NamedTextColor.RED))
                .append(Component.text("]").color(NamedTextColor.DARK_GRAY))
        );
    }
}
