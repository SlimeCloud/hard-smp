package de.slimecloud.hardsmp.advancement.handler;

import de.cyklon.spigotutils.persistence.PersistentDataHandler;
import de.slimecloud.hardsmp.advancement.AdvancementHandler;
import de.slimecloud.hardsmp.advancement.CustomAdvancement;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class BedAdvancement extends AdvancementHandler {

    //10 in game days in ticks
    private final static int TICKS = 24000*10;
    private final NamespacedKey key;
    public BedAdvancement(Plugin plugin) {
        super(plugin, CustomAdvancement.BED);
        this.key = new NamespacedKey(plugin, "awake.ticks");
    }

    @EventHandler
    public void onSleep(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        PersistentDataHandler.get(player).set(key, player.getStatistic(Statistic.PLAY_ONE_MINUTE));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PersistentDataHandler handler = PersistentDataHandler.get(player);
        if (!handler.contains(key)) handler.set(key, player.getStatistic(Statistic.PLAY_ONE_MINUTE));
    }

    @Override
    protected void update() {
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (!isDone(p)) {
                int currTime = p.getStatistic(Statistic.PLAY_ONE_MINUTE);
                int timeElapsed = currTime - PersistentDataHandler.get(p).getIntOrDefault(key, currTime);
                if (timeElapsed>=TICKS) unlock(p);
            }
        });
    }
}
