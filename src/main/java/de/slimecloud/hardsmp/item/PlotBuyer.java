package de.slimecloud.hardsmp.item;

import de.cyklon.spigotutils.adventure.Formatter;
import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.claim.ClaimRights;
import de.slimecloud.hardsmp.event.PlayerShopEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;

public class PlotBuyer extends CustomItem implements Listener {

    private final int blockAmount;
    private final int quantity;

    public PlotBuyer(int blockAmount, int pointsRequired, int quantity, int index) {
        super("plot-buyer-" + blockAmount, Material.HEAVY_WEIGHTED_PRESSURE_PLATE, index);
        this.blockAmount = blockAmount;
        this.quantity = quantity;

        builder.setDisplayName(ChatColor.RESET + "§6Grundstück " + blockAmount + " Blöcke")
                .setLore(Formatter.parseText("§cBenötigte Punkte: " + pointsRequired),
                        Formatter.parseText("§äKaufe dir ein Grundstück, dass du mit §ö/claim§ä überall platzieren kannst!"),
                        Formatter.parseText("§äDieses Grundstück kannst du insgesamt §ö" + quantity + "§ä mal kaufen."))
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        add();
    }

    @EventHandler
    private void onShop(PlayerShopEvent event) {
        ClaimRights rights = ClaimRights.load(event.getPlayer().getUniqueId());
        if(rights.getBought().get(blockAmount) >= quantity) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(HardSMP.getPrefix().append(Component.text("§cDu hast schon das Maximum gekauft!")));
            return;
        }
        rights.buy(blockAmount);
    }

}
