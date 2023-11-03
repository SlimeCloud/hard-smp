package de.slimecloud.hardsmp.ui.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScoreboardManager implements Listener {

    private final Map<UUID, Scoreboard> SCOREBOARD_MAP = new HashMap<>();
    private static BukkitTask updateTask = null;

    private final Plugin plugin;

    public ScoreboardManager(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getOnlinePlayers().forEach(this::add);
        if (updateTask==null) updateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            BoardStats stats = new BoardStats();
            SCOREBOARD_MAP.forEach((k, v) -> v.update(stats));
        }, 0, 20*5);
    }

    private void add(Player player) {
        if (!SCOREBOARD_MAP.containsKey(player.getUniqueId())) SCOREBOARD_MAP.put(player.getUniqueId(), new Scoreboard(plugin, player));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        add(event.getPlayer());
    }
}
