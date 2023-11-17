package de.slimecloud.hardsmp.item;

import de.cyklon.spigotutils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class ChestKey extends CustomItem implements Listener {

    public static final ItemBuilder BUILDER = new ItemBuilder(Material.IRON_HOE)
            .setDisplayName("Schlüssel")
            .setUnbreakable(true)
            .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
            .setCustomModelData(0);

    public ChestKey() {
        super(Material.IRON_HOE, 0);
        builder.setDisplayName("Schlüssel")
                .setUnbreakable(true)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!isItem(event.getItem())) return;
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) event.setCancelled(true);
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            switch (event.getItem().getType()) {
                case CHEST, TRAPPED_CHEST, BARREL -> {

                }
            }
        }
    }



}
