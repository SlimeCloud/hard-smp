package de.slimecloud.hardsmp.item;

import com.google.common.primitives.Longs;
import de.cyklon.spigotutils.persistence.PersistentDataHandler;
import de.slimecloud.hardsmp.player.PlayerController;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class ChestKey extends CustomItem implements Listener {

    private final Set<Material> LOCKABLE;
    private final NamespacedKey lockKey;
    private final NamespacedKey idKey;

    public ChestKey(Plugin plugin) {
        super("chest-key", Material.IRON_HOE, 0);
        builder.setDisplayName("Schlüssel")
                .setUnbreakable(true)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);

        this.LOCKABLE = new HashSet<>();
        List<String> list = plugin.getConfig().getStringList("chest-key.lockable");
        list.forEach(s -> LOCKABLE.add(Material.valueOf(s.toUpperCase())));
        this.lockKey = new NamespacedKey(plugin, "locked");
        this.idKey = new NamespacedKey(plugin, "lock-id");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!isItem(event.getItem())) return;
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) event.setCancelled(true);
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.getClickedBlock() != null) {
                if (LOCKABLE.contains(event.getClickedBlock().getType()))
                        PlayerController.getPlayer((HumanEntity) event.getPlayer()).addPoints(1);

                else event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemMoveInInventory(InventoryMoveItemEvent event) {
        event.setCancelled(isInventoryBlockLocked(event.getSource()) || isInventoryBlockLocked(event.getDestination()));
    }

    private boolean isInventoryBlockLocked(Inventory inventory) {
        if (inventory instanceof PlayerInventory) return false;
        if (inventory.getLocation() != null) {
            Block block = inventory.getLocation().getBlock();

            if (LOCKABLE.contains(block.getType())) return isContainerLocked(block.getState());
        }
        return false;
    }

    /**
     * checks whether the container is contained in LOCKABLE, whether it is a container and whether the container is bound to a key
     * @param block the block to be checked
     * @return true if the container is bound to a key. otherwise false
     */
    private boolean isContainerLocked(BlockState block) {
        return LOCKABLE.contains(block.getType()) && block instanceof Container container && PersistentDataHandler.get(container).contains(lockKey);
    }


    /**
     * check with isContainerLocked before using this method
     * @param container the container from which the id is to be returned
     * @return the id of the container
     */
    @SuppressWarnings("ConstantConditions")
    private long getID(Container container) {
        return PersistentDataHandler.get(container).getLong(idKey);
    }

    /**
     * returns the IDs of all containers that are bound to this key
     * @param key the key from which the bound containers are to be returned
     * @return the IDs of all containers that are bound to this key
     */
    private long[] getIDS(ItemStack key) {
        return PersistentDataHandler.get(key).getLongArray(idKey);
    }

    /**
     * bind a container to a key
     * @param container the container to be bound to the key
     * @param key the key to which the container is to be bound
     */
    private void bindKey(Container container, ItemStack key) {
        long id = System.nanoTime();
        PersistentDataHandler data = PersistentDataHandler.get(container);
        data.set(lockKey, true);
        data.set(idKey, id);
        container.update();
        data = PersistentDataHandler.get(key);
        List<Long> ids = Longs.asList(Objects.requireNonNullElse(data.getLongArray(idKey), new long[0]));
        ids.add(id);
        data.set(idKey, Longs.toArray(ids));
    }

}
