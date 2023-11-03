package de.slimecloud.hardsmp.advancement.handler;

import de.slimecloud.hardsmp.advancement.AdvancementHandler;
import de.slimecloud.hardsmp.advancement.CustomAdvancement;
import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;

public class BlocksAdvancement extends AdvancementHandler {

	public BlocksAdvancement(Plugin plugin) {
		super(plugin, CustomAdvancement.BLOCKS);
	}

	@EventHandler
	public void onBreakBlock(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		int i = player.getStatistic(Statistic.MINE_BLOCK, block.getType());
		if (i >= 10000) unlock(player);
	}

}
