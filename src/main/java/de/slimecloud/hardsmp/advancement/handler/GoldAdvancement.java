package de.slimecloud.hardsmp.advancement.handler;

import de.cyklon.spigotutils.advancement.AdvancementType;
import de.slimecloud.hardsmp.advancement.AdvancementHandler;
import de.slimecloud.hardsmp.advancement.CustomAdvancement;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.plugin.Plugin;

public class GoldAdvancement extends AdvancementHandler {

    private final int requiredGold;

    public GoldAdvancement(Plugin plugin, AdvancementType type, int requiredGold) {
        super(plugin, type);
        this.requiredGold = requiredGold;
    }

    @EventHandler
    public void onStatisticIncrement(PlayerStatisticIncrementEvent event) {
        if (event.getStatistic().equals(Statistic.MINE_BLOCK)) {
            Player player = event.getPlayer();
            if (isDone(player)) return;
            if (event.getMaterial() == null) return;
            if (event.getMaterial().equals(Material.GOLD_ORE) || event.getMaterial().equals(Material.DEEPSLATE_GOLD_ORE)) {
                if (isDone(player)) return;
                int gold = player.getStatistic(Statistic.MINE_BLOCK, Material.GOLD_ORE) + player.getStatistic(Statistic.MINE_BLOCK, Material.DEEPSLATE_GOLD_ORE);
                if (gold >= requiredGold) unlock(player);
            }
        }
    }

    public static class Gold1 extends GoldAdvancement {
        public Gold1(Plugin plugin) {
            super(plugin, CustomAdvancement.GOLD1, 100);
        }
    }

    public static class Gold2 extends GoldAdvancement {
        public Gold2(Plugin plugin) {
            super(plugin, CustomAdvancement.GOLD2, 500);
        }
    }

    public static class Gold3 extends GoldAdvancement {
        public Gold3(Plugin plugin) {
            super(plugin, CustomAdvancement.GOLD3, 1000);
        }
    }
}
