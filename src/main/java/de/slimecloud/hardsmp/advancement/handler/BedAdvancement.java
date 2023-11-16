package de.slimecloud.hardsmp.advancement.handler;

import de.slimecloud.hardsmp.advancement.AdvancementHandler;
import de.slimecloud.hardsmp.advancement.CustomAdvancement;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.plugin.Plugin;

public class BedAdvancement extends AdvancementHandler {

    //10 in game days in ticks
    private final static int TICKS = 24000 * 10;

    public BedAdvancement(Plugin plugin) {
        super(plugin, CustomAdvancement.BED);
    }

    @EventHandler
    public void onStatisticIncrement(PlayerStatisticIncrementEvent event) {
        if (event.getStatistic().equals(Statistic.TIME_SINCE_REST)) {
            Player p = event.getPlayer();
            if (p.getStatistic(Statistic.TIME_SINCE_REST) >= TICKS) unlock(p);
        }
    }
}
