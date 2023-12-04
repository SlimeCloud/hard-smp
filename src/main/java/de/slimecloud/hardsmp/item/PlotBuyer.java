package de.slimecloud.hardsmp.item;

import de.cyklon.spigotutils.adventure.Formatter;
import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.claim.ClaimRights;
import de.slimecloud.hardsmp.event.PlayerShopEvent;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemFlag;

import java.lang.reflect.Field;
import java.util.Objects;

import static net.kyori.adventure.text.format.TextColor.color;

public class PlotBuyer extends CustomItem implements Listener {

    private final int blockAmount;
    private final int quantity;
    private final int index;

    public PlotBuyer(int blockAmount, int pointsRequired, int quantity, int index) {
        super("plot-buyer-" + blockAmount, Material.IRON_HOE, index);
        this.blockAmount = blockAmount;
        this.quantity = quantity;
        this.index = index;

        builder.setDisplayName(ChatColor.RESET + "§6Grundstück " + blockAmount + " Blöcke")
                .setLore(Formatter.parseText("§äKaufe dir ein Grundstück, dass du mit §ö/claim§ä überall sichern kannst!"),
                        Formatter.parseText("§äDieses Grundstück kannst du insgesamt §ö" + quantity + "§ä mal kaufen."))
                        //ToDo pls add an indicator how often the player bought this extension already
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        add();
    }



    /*@EventHandler
    private void onLayItemInInv(InventoryClickEvent event) {
        System.out.println(event.getAction());
        if (event.getAction() == InventoryAction.PLACE_ALL || event.getAction() == InventoryAction.PLACE_ONE || event.getAction() == InventoryAction.PLACE_SOME) {
            event.setCancelled(true);
            event.setCursor(null);
        }

        if (event.getCurrentItem() == null) return;
        if (!Objects.equals(event.getCurrentItem().getType(), super.getItem().getType())) return;
        if (!Objects.equals(event.getCurrentItem().getItemMeta().getCustomModelData(), super.getItem().getItemMeta().getCustomModelData())) return;
        if (event.getClickedInventory() == null) return;
        //if (!(event.getClickedInventory().getHolder() instanceof Player)) return;

        //System.out.println(event.getCurrentItem());
        if (event.getCursor().getType() == super.getItem().getType())
            event.setCursor(null);
        //event.getClickedInventory().removeItemAnySlot(event.getCurrentItem());
    }

    @EventHandler
    private void onItemDrag(InventoryDragEvent event) {
        event.setCancelled(true);
        event.setCursor(null);
    }*/

}
