package de.slimecloud.hardsmp.item;

import de.cyklon.spigotutils.adventure.Formatter;
import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.claim.ClaimRights;
import de.slimecloud.hardsmp.event.PlayerShopEvent;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;

import java.lang.reflect.Field;

public class PlotBuyer extends CustomItem implements Listener {

    private final int blockAmount;
    private final int quantity;
    private final int index;

    public PlotBuyer(int blockAmount, int pointsRequired, int quantity, int index) {
        super("plot-buyer-" + blockAmount, Material.HEAVY_WEIGHTED_PRESSURE_PLATE, index);
        this.blockAmount = blockAmount;
        this.quantity = quantity;
        this.index = index;

        builder.setDisplayName(ChatColor.RESET + "§6Grundstück " + blockAmount + " Blöcke")
                .setLore(Formatter.parseText("§cBenötigte Punkte: " + pointsRequired),
                        Formatter.parseText("§äKaufe dir ein Grundstück, dass du mit §ö/claim§ä überall sichern kannst!"),
                        Formatter.parseText("§äDieses Grundstück kannst du insgesamt §ö" + quantity + "§ä mal kaufen."))
                        //ToDo pls add an indicator how often the player bought this extension already
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        add();
    }

    @EventHandler
    @SneakyThrows
    private void onShop(PlayerShopEvent event) {
        if (event.getItem().getType() != Material.HEAVY_WEIGHTED_PRESSURE_PLATE) return;
        if (event.getItem().getItemMeta().getCustomModelData() != index) return;

        ClaimRights rights = ClaimRights.load(event.getPlayer().getUniqueId());

        Field field = rights.getClass().getDeclaredField("bought" + blockAmount);
        field.setAccessible(true);
        if ((int) field.get(rights) >= quantity) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(HardSMP.getPrefix().append(Component.text("§cDu hast schon das Maximum gekauft!")));
            return;
        }

        rights.buy(blockAmount);
    }

}
