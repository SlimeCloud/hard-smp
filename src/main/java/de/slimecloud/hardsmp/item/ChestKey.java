package de.slimecloud.hardsmp.item;

import com.google.common.primitives.Longs;
import de.cyklon.spigotutils.persistence.PersistentDataHandler;
import de.cyklon.spigotutils.tuple.Tuple;
import de.cyklon.spigotutils.adventure.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
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
        Set<String> list = new HashSet<>(plugin.getConfig().getStringList("chest-key.lockable"));
        list.forEach(s -> LOCKABLE.add(Material.valueOf(s.toUpperCase())));
        if (LOCKABLE.contains(Material.SHULKER_BOX)) for (Material value : Material.values()) {
            if (value.name().contains("SHULKER_BOX")) LOCKABLE.add(value);
        }
        this.idKey = new NamespacedKey(plugin, "lock-id");
        add();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK) && isItem(event.getItem())) event.setCancelled(true);
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.getClickedBlock() != null) {
                Block clickedBlock = event.getClickedBlock();
                ItemStack item = event.getItem();
                Player player = event.getPlayer();
                boolean flag = true;
                if (isItem(item)) {
                    if (LOCKABLE.contains(clickedBlock.getType()) && clickedBlock.getState() instanceof Container container) {
                        if (isContainerLocked(container)) {
                            if (player.isSneaking()) {
                                unbindKey(event.getClickedBlock(), item);
                                event.getPlayer().sendActionBar(Formatter.parseText(plugin.getConfig().getString("chest-key.success.unlock", "§2Geöffnet")));
                                flag = false;
                            }
                        } else {
                            if (player.isSneaking()) {
                                bindKey(event.getClickedBlock(), item);
                                event.getPlayer().sendActionBar(Formatter.parseText(plugin.getConfig().getString("chest-key.success.lock", "§2Verschlossen")));
                                flag = false;
                            }
                        }
                    } else flag = false;
                }

                if (flag) {
                    if (clickedBlock.getState() instanceof Container container && isContainerLocked(container)) {
                        if (!playerHasKeyForContainer(player, getID(container))) {
                            player.sendActionBar(Formatter.parseText(plugin.getConfig().getString("chest-key.no-key", "§cVerschlossen")));
                            event.setCancelled(!event.getPlayer().isSneaking());
                        }
                    }
                } else event.setCancelled(true);

            } else if (isItem(event.getItem())) event.setCancelled(true);
        }
    }

    @EventHandler
    @SuppressWarnings("ConstantConditions")
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!(event.getBlock().getState() instanceof Chest)) return;
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Tuple<Boolean, Inventory, Inventory> isDoubleChest = isDoubleChest(event.getBlockPlaced());
            if (isDoubleChest.first()) {
                Inventory left = isDoubleChest.second();
                Inventory right = isDoubleChest.third();

                boolean leftHasId = event.getBlockPlaced().getLocation().equals(right.getLocation());
                if (isInventoryBlockLocked(leftHasId ? left : right)) {
                    long id = getID((Container) (leftHasId ? left : right).getLocation().getBlock().getState());
                    bindIdToBlock((leftHasId ? right : left).getLocation().getBlock(), id);
                }
            }
        }, 1);
    }


    @EventHandler
    public void onItemMoveInInventory(InventoryMoveItemEvent event) {
        event.setCancelled(isInventoryBlockLocked(event.getSource()) || isInventoryBlockLocked(event.getDestination()));
    }

    private boolean isInventoryBlockLocked(Inventory inventory) {
        if (inventory instanceof PlayerInventory) return false;
        if (inventory.getLocation() != null) {
            Block block = inventory.getLocation().getBlock();
            return isContainerLocked(block.getState());
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

    private Tuple<Boolean, Inventory, Inventory> isDoubleChest(Block block) {
        boolean isDoubleChest = false;
        Inventory leftSide = null;
        Inventory rightSide = null;

        if (block.getState() instanceof Chest chest && chest.getInventory() instanceof DoubleChestInventory doubleChest) {
            isDoubleChest = true;
            leftSide = doubleChest.getLeftSide();
            rightSide = doubleChest.getRightSide();
        }
        return new Tuple<>(isDoubleChest, leftSide, rightSide);
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


    private void bindIdToBlock(Block block, long id) {
        if (block.getState() instanceof Container blockContainer) {
            PersistentDataHandler.get(blockContainer).set(idKey, id);
            blockContainer.update();
        }
    }

    /**
     * bind a container to a key
     * @param block the container to be bound to the key
     * @param key the key to which the container is to be bound
     */
    private void bindKey(Block block, ItemStack key) {
        if (block.getState() instanceof Container) {
            Tuple<Boolean, Inventory, Inventory> isDoubleChest = isDoubleChest(block);
            List<Block> lockBlocks = new ArrayList<>();
            if (isDoubleChest.first() && isDoubleChest.second().getLocation() != null && isDoubleChest.third().getLocation() != null) {
                lockBlocks.add(isDoubleChest.second().getLocation().getBlock());
                lockBlocks.add(isDoubleChest.third().getLocation().getBlock());
            } else lockBlocks.add(block);

            long id = System.nanoTime();
            for (Block lockBlock : lockBlocks) {
                bindIdToBlock(lockBlock, id);
            }

            PersistentDataHandler data = PersistentDataHandler.get(key);
            Set<Long> ids = new HashSet<>(Longs.asList(Objects.requireNonNullElse(data.getLongArray(idKey), new long[0])));
            ids.add(id);
            data.set(idKey, Longs.toArray(ids));
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void unbindKey(Block block, ItemStack key) {
        if (block.getState() instanceof Container) {
            Tuple<Boolean, Inventory, Inventory> isDoubleChest = isDoubleChest(block);
            List<Block> lockBlocks = new ArrayList<>();
            if (isDoubleChest.first() && isDoubleChest.second().getLocation() != null && isDoubleChest.third().getLocation() != null) {
                lockBlocks.add(isDoubleChest.second().getLocation().getBlock());
                lockBlocks.add(isDoubleChest.third().getLocation().getBlock());
            } else lockBlocks.add(block);

            PersistentDataHandler data;
            long id = 0;
            for (Block lockBlock : lockBlocks) {
                if (lockBlock.getState() instanceof Container blockContainer) {
                    data = PersistentDataHandler.get(blockContainer);
                    if (!data.contains(idKey)) return;
                    id = data.getLong(idKey);
                    data.remove(idKey);
                    blockContainer.update();
                }
            }

            data = PersistentDataHandler.get(key);
            Set<Long> ids = new HashSet<>(Longs.asList(Objects.requireNonNullElse(data.getLongArray(idKey), new long[0])));
            ids.remove(id);
            data.set(idKey, Longs.toArray(ids));
        }
    }

    private boolean playerHasKeyForContainer(Player player, long id) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (isItem(item) && Longs.asList(getIDS(item)).contains(id)) return true;
        }
        return false;
    }

}
