package de.slimecloud.hardsmp.block;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import de.cyklon.spigotutils.persistence.PersistentDataFile;
import de.cyklon.spigotutils.persistence.PersistentDataHandler;
import io.papermc.paper.event.block.BlockBreakBlockEvent;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public class BlockHandler extends BukkitRunnable implements Listener {

	private final PersistentDataFile placedBlocksContainer;

	private final Plugin plugin;

	public BlockHandler(Plugin plugin) {
		this.plugin = plugin;
		this.placedBlocksContainer = new PersistentDataFile(new File(plugin.getDataFolder(), "blocks_placed.dat"));
		runTaskTimer(plugin, 20 * 60 * 10, 20 * 60 * 10);
	}

	public void save() {
		placedBlocksContainer.save();
	}

	private NamespacedKey getKey(Location location) {
		return new NamespacedKey(plugin, String.format("%s.%s.%s.%s.natural", location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ()));
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		PersistentDataHandler.get(placedBlocksContainer).set(getKey(event.getBlock().getLocation()), false);
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		PersistentDataHandler.get(placedBlocksContainer).remove(getKey(event.getBlock().getLocation()));
	}

	@EventHandler
	public void onBreakBlock(BlockBreakBlockEvent event) {
		PersistentDataHandler.get(placedBlocksContainer).remove(getKey(event.getBlock().getLocation()));
	}

	@EventHandler
	public void onDestroy(BlockDestroyEvent event) {
		PersistentDataHandler.get(placedBlocksContainer).remove(getKey(event.getBlock().getLocation()));
	}

	@EventHandler
	public void onBlockGrow(BlockGrowEvent event) {
		PersistentDataHandler.get(placedBlocksContainer).remove(getKey(event.getNewState().getLocation()));
	}

	@EventHandler
	public void onStructureGrow(StructureGrowEvent event) {
		PersistentDataHandler.get(placedBlocksContainer).remove(getKey(event.getLocation()));
	}

	public boolean isNatural(Block block) {
		return PersistentDataHandler.get(placedBlocksContainer).getBoolOrDefault(getKey(block.getLocation()), true);
	}

	@Override
	public void run() {
		save();
	}
}
