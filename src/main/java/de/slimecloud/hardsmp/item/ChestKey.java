package de.slimecloud.hardsmp.item;

import com.google.common.primitives.Longs;
import de.cyklon.spigotutils.adventure.Formatter;
import de.cyklon.spigotutils.persistence.PersistentDataHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
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

    private final Plugin plugin;
    private final Set<Material> LOCKABLE;
    private final NamespacedKey idKey;

    public ChestKey(Plugin plugin) {
        super("chest-key", Material.IRON_HOE, 0);
        builder.setDisplayName("Schlüssel")
                .setUnbreakable(true)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);

        this.plugin = plugin;
        this.LOCKABLE = new HashSet<>();
        List<String> list = plugin.getConfig().getStringList("chest-key.lockable");
        list.forEach(s -> LOCKABLE.add(Material.valueOf(s.toUpperCase())));
        this.idKey = new NamespacedKey(plugin, "lock-id");
        add();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK) && isItem(event.getItem())) event.setCancelled(true);
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.getClickedBlock() != null) {
                Block clickedBlock = event.getClickedBlock();
                if (isContainerLocked(clickedBlock.getState()) || (!LOCKABLE.contains(clickedBlock.getType()) && clickedBlock instanceof Container)) {
                    long keyId = getID((Container) clickedBlock.getState());
                    if (!playerHasKeyForContainer(event.getPlayer(), keyId)) {
                        event.getPlayer().sendActionBar(Formatter.parseText(plugin.getConfig().getString("chest-key.no-key", "§cVerschlossen")));
                        event.setCancelled(true);
                    }
                } else {
                    if (isItem(event.getItem())) {
                        if (LOCKABLE.contains(clickedBlock.getType())) {
                            if (event.getPlayer().isSneaking()) {
                                bindKey((Container) clickedBlock.getState(), event.getItem());
                                event.getPlayer().sendActionBar(Formatter.parseText(plugin.getConfig().getString("chest-key.success", "§2Verschlossen")));
                            }
                        } else event.setCancelled(true);
                    }
                }


            } else if (isItem(event.getItem())) event.setCancelled(true);
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
        return LOCKABLE.contains(block.getType()) && block instanceof Container container && PersistentDataHandler.get(container).contains(idKey);
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
        data.set(idKey, id);
        container.update();
        data = PersistentDataHandler.get(key);
        Set<Long> ids = new HashSet<>(Longs.asList(Objects.requireNonNullElse(data.getLongArray(idKey), new long[0])));
        ids.add(id);
        data.set(idKey, Longs.toArray(ids));
    }

    @SuppressWarnings("ConstantConditions")
    private void unbindKey(Container container, ItemStack key) {
        PersistentDataHandler data = PersistentDataHandler.get(container);
        if (!data.contains(idKey)) return;
        long id = data.getLong(idKey);
        data.remove(idKey);
        container.update();
        Set<Long> ids = new HashSet<>(Longs.asList(Objects.requireNonNullElse(data.getLongArray(idKey), new long[0])));
        ids.remove(id);
        data.set(idKey, Longs.toArray(ids));
    }

    private boolean playerHasKeyForContainer(Player player, long id) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (isItem(item) && Longs.asList(getIDS(item)).contains(id)) return true;
        }
        return false;
    }

}
