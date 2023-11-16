package de.slimecloud.hardsmp.advancement.handler;

import de.slimecloud.hardsmp.advancement.AdvancementHandler;
import de.slimecloud.hardsmp.advancement.CustomAdvancement;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class MusicAdvancement extends AdvancementHandler {

    private final static List<Material> DISCS;

    static {
        DISCS = new ArrayList<>();
        for (Material value : Material.values()) if (value.name().contains("MUSIC_DISC")) DISCS.add(value);
    }

    public MusicAdvancement(Plugin plugin) {
        super(plugin, CustomAdvancement.MUSIC);
    }

    @EventHandler
    public void onStatisticIncrement(PlayerStatisticIncrementEvent event) {
        if (event.getStatistic().equals(Statistic.USE_ITEM)) {
            if (!DISCS.contains(event.getMaterial())) return;
            Player player = event.getPlayer();
            if (isDone(player)) return;
            if (checkDiscs(player)) unlock(player);
        }
    }

    private boolean checkDiscs(Player player) {
        for (Material disc : DISCS) if (player.getStatistic(Statistic.USE_ITEM, disc) == 0) return false;
        return true;
    }
}
