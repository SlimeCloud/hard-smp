package de.slimecloud.hardsmp.ui;

import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.player.PlayerController;
import de.slimecloud.hardsmp.player.data.PointCategory;
import de.slimecloud.hardsmp.player.data.PointsListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class AdvancementMessage implements Listener {
    @EventHandler
    private void onAdvancement(PlayerAdvancementDoneEvent event) {
        if(event.getAdvancement().getDisplay() == null) return;

        Integer rarity = PointsListener.ADVANCEMENTS_RARITY.get(event.getAdvancement().getKey().asString());
        if(rarity == null) return;

        event.message(Chat.getName(event.getPlayer())
                .append(Component.text(" hat das Advancement ").color(NamedTextColor.GRAY))
                .append(event.getAdvancement().getDisplay().displayName())
                .append(Component.text(" bekommen ").color(NamedTextColor.GRAY))
                .append(Component.text("[").color(NamedTextColor.DARK_GRAY))
                .append(Component.text(PointCategory.ADVANCEMENT.calculate(rarity)).color(HardSMP.getInstance().getGreenColor()).hoverEvent(HoverEvent.showText(
                        Component.text((int) PlayerController.applyFormula(PointCategory.ADVANCEMENT.calculate(rarity), event.getPlayer()))
                                .color(TextColor.color(0xF6ED82))
                )))
                .append(Component.text("]").color(NamedTextColor.DARK_GRAY))
        );
    }
}
