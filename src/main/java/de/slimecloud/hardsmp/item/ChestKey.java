package de.slimecloud.hardsmp.item;

import de.cyklon.spigotutils.item.ItemBuilder;
import de.slimecloud.hardsmp.player.PlayerController;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.PlayerInventory;

public class ChestKey extends CustomItem implements Listener {

    public ChestKey() {
        super("chest-key", Material.IRON_HOE, 0);
        builder.setDisplayName("Schlüssel")
                .setUnbreakable(true)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!isItem(event.getItem())) return;
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) event.setCancelled(true);
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            switch (event.getClickedBlock().getType()) {
                case CHEST, TRAPPED_CHEST, BARREL -> {
                    PlayerController.getPlayer((HumanEntity) event.getPlayer()).addPoints(1);
                }
                default -> event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onItemMoveInInventory(InventoryMoveItemEvent event) {
        if (event.getSource() instanceof PlayerInventory) return;
        if (event.getSource().getLocation() != null) {
            Block source = event.getSource().getLocation().getBlock();

            switch (source.getType()) {
                case CHEST, TRAPPED_CHEST, BARREL -> {
                    // check if is locked ...
                    event.setCancelled(true);
                }
            }
        }
    }



}
