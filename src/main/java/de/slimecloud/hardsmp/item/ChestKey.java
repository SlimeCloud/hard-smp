package de.slimecloud.hardsmp.item;

import com.google.common.primitives.Longs;
import de.cyklon.spigotutils.adventure.Formatter;
import de.cyklon.spigotutils.persistence.PersistentDataHandler;
import de.cyklon.spigotutils.server.BukkitServer;
import de.cyklon.spigotutils.tuple.Tuple;
import de.slimecloud.hardsmp.HardSMP;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;

import java.util.*;

@Getter
public class ChestKey extends CustomItem implements Listener {

    private final HardSMP plugin;
    private final Set<Material> LOCKABLE;
    private final NamespacedKey idKey;
    private final NamespacedKey idCracked;
    private final Component unlockMsg;
    private final Component lockMsg;
    private final Component noKeyMsg;

    public ChestKey(HardSMP plugin) {
        super("chest-key", Material.IRON_HOE, 2);
        builder.setDisplayName(ChatColor.RESET + "§öSchlüssel")
                .setLore(Formatter.parseText("§äMit diesem Schlüssel kannst du mit §öSHIFT + RECHTSKLICK §äKisten/Shulker absperren."),
                        Formatter.parseText("§äDeine Schlüssel kannst du in §ö/keys §äverstauen."),
                        Formatter.parseText("§ä§lAber §r§ädarauf den Schlüssel im Inv zu haben wenn du deine Kisten wieder öffnen willst!")
                        )
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
        this.idCracked = new NamespacedKey(plugin, "cracked");
        this.unlockMsg = Formatter.parseText(plugin.getConfig().getString("chest-key.success.unlock", "§2Geöffnet"));
        this.lockMsg = Formatter.parseText(plugin.getConfig().getString("chest-key.success.lock", "§2Verschlossen"));
        this.noKeyMsg = Formatter.parseText(plugin.getConfig().getString("chest-key.no-key", "§cVerschlossen"));
        add();
    }

    private int lastCall = -1;
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (lastCall == BukkitServer.getCurrentTick()) return;
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK) && isItem(event.getItem())) {
            event.setCancelled(true);
            return;
        }
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock != null && LOCKABLE.contains(clickedBlock.getType())) {
                lastCall = BukkitServer.getCurrentTick();
                ItemStack item = event.getItem();
                Player player = event.getPlayer();
                boolean flag = true;
                boolean isCracked = isCracked(clickedBlock.getState());
                if (isItem(item)) {
                    if (clickedBlock.getState() instanceof Container container) {
                        if (isContainerLocked(container)) {
                            if (player.isSneaking() && Longs.asList(getIDS(item)).contains(getID(container))) {
                                unbindKey(clickedBlock, item);
                                if (isCracked) unCrack(clickedBlock);
                                event.getPlayer().sendActionBar(unlockMsg);
                                flag = false;
                            }
                        } else {
                            if (player.isSneaking()) {
                                bindKey(clickedBlock, item);
                                if (isCracked) unCrack(clickedBlock);
                                event.getPlayer().sendActionBar(lockMsg);
                                flag = false;
                            }
                        }
                    } else flag = false;
                }
                if (flag) {
                    if (clickedBlock.getState() instanceof Container container && isContainerLocked(container)) {
                        if (!(playerHasKeyForContainer(player, getID(container)) || isCracked)) {
                            if (event.isBlockInHand()) {
                                if (!player.isSneaking()) {
                                    player.sendActionBar(noKeyMsg);
                                    event.setCancelled(true);
                                }
                            } else {
                                player.sendActionBar(noKeyMsg);
                                event.setCancelled(true);
                            }
                        } else if (isCracked) {
                            if (plugin.getLockPick().isItem(item) && player.isSneaking()) event.setCancelled(true);
                            else unCrack(clickedBlock);
                        }
                    }
                } else event.setCancelled(true);

            } else if (isItem(event.getItem())) event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!(block.getState() instanceof Container container)) return;
        Player player = event.getPlayer();
        if (isContainerLocked(container) && !(playerHasKeyForContainer(player, getID(container)) || isCracked(container))) {
            player.sendActionBar(noKeyMsg);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(b -> b.getState() instanceof Container container && isContainerLocked(container) && !isCracked(container));
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(b -> b.getState() instanceof Container container && isContainerLocked(container) && !isCracked(container));
    }

    @EventHandler
    public void onPiston(BlockPistonExtendEvent event) {
        event.getBlocks().forEach(b -> {
            if (b.getState() instanceof Container container && isContainerLocked(container) && !isCracked(container)) event.setCancelled(true);
        });
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

    public boolean isInventoryBlockLocked(Inventory inventory) {
        if (inventory instanceof PlayerInventory) return false;
        if (inventory.getLocation() != null) {
            Block block = inventory.getLocation().getBlock();
            return isContainerLocked(block.getState());
        }
        return false;
    }

    /**
     * checks whether the container is contained in LOCKABLE, whether it is a container and whether the container is bound to a key
     *
     * @param block the block to be checked
     * @return true if the container is bound to a key. otherwise false
     */
    public boolean isContainerLocked(BlockState block) {
        return LOCKABLE.contains(block.getType()) && block instanceof Container container && PersistentDataHandler.get(container).contains(idKey);
    }

    public Tuple<Boolean, Inventory, Inventory> isDoubleChest(Block block) {
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
     *
     * @param container the container from which the id is to be returned
     * @return the id of the container
     */
    @SuppressWarnings("ConstantConditions")
    public long getID(Container container) {
        return PersistentDataHandler.get(container).getLong(idKey);
    }

    /**
     * returns the IDs of all containers that are bound to this key
     *
     * @param key the key from which the bound containers are to be returned
     * @return the IDs of all containers that are bound to this key
     */
    public long[] getIDS(ItemStack key) {
        return PersistentDataHandler.get(key).getLongArrayOrDefault(idKey, new long[0]);
    }


    public void bindIdToBlock(Block block, long id) {
        if (block.getState() instanceof Container blockContainer) {
            PersistentDataHandler.get(blockContainer).set(idKey, id);
            blockContainer.update();
        }
    }

    private List<Block> getLockBlocks(Block block) {
        List<Block> lockBlocks = new ArrayList<>();
        if (block.getState() instanceof Container) {
            Tuple<Boolean, Inventory, Inventory> isDoubleChest = isDoubleChest(block);
            if (isDoubleChest.first() && isDoubleChest.second().getLocation() != null && isDoubleChest.third().getLocation() != null) {
                lockBlocks.add(isDoubleChest.second().getLocation().getBlock());
                lockBlocks.add(isDoubleChest.third().getLocation().getBlock());
            } else lockBlocks.add(block);
        }
        return lockBlocks;
    }

    /**
     * bind a container to a key
     *
     * @param block the container to be bound to the key
     * @param key   the key to which the container is to be bound
     */
    public void bindKey(Block block, ItemStack key) {
        List<Block> lockBlocks = getLockBlocks(block);
        if (!lockBlocks.isEmpty()) {
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
    public void unbindKey(Block block, ItemStack key) {
        long id = unbindKey(block);
        if (id != -1) unbindKey(key, id);
    }

    @SuppressWarnings("ConstantConditions")
    public long unbindKey(Block block) {
        long id = -1;
        List<Block> lockBlocks = getLockBlocks(block);
        if (!lockBlocks.isEmpty()) {
            PersistentDataHandler data;
            for (Block lockBlock : lockBlocks) {
                if (lockBlock.getState() instanceof Container blockContainer) {
                    data = PersistentDataHandler.get(blockContainer);
                    if (!data.contains(idKey)) break;
                    id = data.getLong(idKey);
                    data.remove(idKey);
                    blockContainer.update();
                }
            }
        }
        return id;
    }

    public void unbindKey(ItemStack key, long id) {
        PersistentDataHandler data = PersistentDataHandler.get(key);
        Set<Long> ids = new HashSet<>(Longs.asList(data.getLongArrayOrDefault(idKey, new long[0])));
        ids.remove(id);
        data.set(idKey, Longs.toArray(ids));
    }

    /**
     * Important: returns also true if the player has the permission "hardsmp.admin.chestkey"
     */
    public boolean playerHasKeyForContainer(Player player, long id) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (isItem(item) && Longs.asList(getIDS(item)).contains(id)) return true;
        }
        return player.hasPermission("hardsmp.admin.chestkey");
    }

    public void crack(Block block) {
        List<Block> lockBlocks = getLockBlocks(block);
        if (!lockBlocks.isEmpty()) {
            PersistentDataHandler data;
            for (Block lockBlock : lockBlocks) {
                if (lockBlock.getState() instanceof Container blockContainer) {
                    data = PersistentDataHandler.get(blockContainer);
                    if (!data.contains(idKey)) break;
                    data.set(idCracked, BukkitServer.getCurrentTick());
                    blockContainer.update();
                }
            }
        }
    }

    public void unCrack(Block block) {
        List<Block> lockBlocks = getLockBlocks(block);
        if (!lockBlocks.isEmpty()) {
            PersistentDataHandler data;
            for (Block lockBlock : lockBlocks) {
                if (lockBlock.getState() instanceof Container blockContainer) {
                    data = PersistentDataHandler.get(blockContainer);
                    Integer tick = data.getInt(idCracked);
                    if (tick==null) continue;
                    if (tick==BukkitServer.getCurrentTick()) break;
                    data.remove(idCracked);
                    blockContainer.update();
                }
            }
        }
    }

    public boolean isCracked(BlockState block) {
        return LOCKABLE.contains(block.getType()) && block instanceof Container container && PersistentDataHandler.get(container).contains(idCracked);
    }

}
