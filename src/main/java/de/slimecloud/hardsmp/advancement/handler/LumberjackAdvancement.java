package de.slimecloud.hardsmp.advancement.handler;

import de.cyklon.spigotutils.persistence.PersistentDataHandler;
import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.advancement.AdvancementHandler;
import de.slimecloud.hardsmp.advancement.CustomAdvancement;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;

import java.util.Set;

public class LumberjackAdvancement extends AdvancementHandler {

	private final static Set<Material> WOOD = Set.of(Material.OAK_LOG, Material.SPRUCE_LOG, Material.BIRCH_LOG, Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.DARK_OAK_LOG, Material.MANGROVE_LOG, Material.CHERRY_LOG, Material.CRIMSON_STEM, Material.WARPED_STEM);

	private final NamespacedKey key;

	public LumberjackAdvancement(Plugin plugin) {
		super(plugin, CustomAdvancement.LUMBERJACK);
		this.key = new NamespacedKey(plugin, "wood.farmed");
	}

	@EventHandler
	public void onBreakBlock(BlockBreakEvent event) {
		Block block = event.getBlock();
		if (!WOOD.contains(block.getType())) return;
		Player player = event.getPlayer();
		if (isDone(player)) return;
		if (!HardSMP.getInstance().getBlockHandler().isNatural(block)) return;
		int i = PersistentDataHandler.get(player).reviseIntWithDefault(key, c -> ++c, 0);
		//1000 = 1000 tree`s
		//5 = 5 log`s per tree
		if (i >= 1000 * 5) unlock(player);
	}

}
