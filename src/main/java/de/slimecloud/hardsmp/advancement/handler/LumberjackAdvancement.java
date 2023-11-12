package de.slimecloud.hardsmp.advancement.handler;

import de.slimecloud.hardsmp.advancement.AdvancementHandler;
import de.slimecloud.hardsmp.advancement.CustomAdvancement;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.plugin.Plugin;

import java.util.Set;

public class LumberjackAdvancement extends AdvancementHandler {

    private final static Set<Material> WOOD = Set.of(Material.OAK_LOG, Material.SPRUCE_LOG, Material.BIRCH_LOG, Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.DARK_OAK_LOG, Material.MANGROVE_LOG, Material.CHERRY_LOG, Material.CRIMSON_STEM, Material.WARPED_STEM);

    public LumberjackAdvancement(Plugin plugin) {
        super(plugin, CustomAdvancement.LUMBERJACK);
    }

    @EventHandler
    public void onStatisticIncrement(PlayerStatisticIncrementEvent event) {
        if (event.getStatistic().equals(Statistic.MINE_BLOCK)) {
            if (!WOOD.contains(event.getMaterial())) return;
            Player player = event.getPlayer();
            if (isDone(player)) return;
            int i = getLogsMined(player);
            //1000 = 1000 tree`s
            //5 = 5 log`s per tree
            if (i >= 1000 * 5) unlock(player);
        }
    }

    private int getLogsMined(Player player) {
        int i = 0;
        for (Material material : WOOD) i += player.getStatistic(Statistic.MINE_BLOCK, material);
        return i;
    }

}
