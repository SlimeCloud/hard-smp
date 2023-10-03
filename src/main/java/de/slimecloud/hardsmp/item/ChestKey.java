package de.slimecloud.hardsmp.item;

import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class ChestKey implements Listener {

    private static final ItemStack item = new ItemBuilder(Material.IRON_HOE)
            .setDisplayName("Schlüssel")
            .setUnbreakable(true)
            .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
            .setCustomModelData(0)
            .build();

    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!(event.getItem().getType().equals(Material.IRON_HOE) && event.getItem().getItemMeta().getCustomModelData()==0)) return;
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) event.setCancelled(true);
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            switch (event.getItem().getType()) {
                case CHEST, TRAPPED_CHEST, BARREL -> {

                }
                default -> event.setCancelled(true);
            }
        }
    }



}
