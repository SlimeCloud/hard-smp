package de.slimecloud.hardsmp.ui;

import de.cyklon.spigotutils.adventure.Formatter;
import de.slimecloud.hardsmp.HardSMP;
import me.lucko.spark.api.Spark;
import me.lucko.spark.api.statistic.StatisticWindow;
import me.lucko.spark.api.statistic.types.DoubleStatistic;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Tablist implements Listener {

    private final Component header;
    private final String footer;

    private final Spark spark;

    public Tablist(HardSMP plugin) {
        this.header = Formatter.parseText(plugin.getConfig().getString("ui.tablist.header", "Hard-SMP"));
        this.footer = plugin.getConfig().getString("ui.tablist.footer", "[%tps]");
        this.spark = plugin.getSpark();
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> Bukkit.getOnlinePlayers().forEach(this::updateTabList), 0, 20 * 10); // period of 10s
    }

    private void updateTabList(Player player) {
        DoubleStatistic<StatisticWindow.TicksPerSecond> stat = spark.tps();
        double tps;
        if (stat == null) tps = Bukkit.getTPS()[0];
        else tps = stat.poll(StatisticWindow.TicksPerSecond.SECONDS_10);
        String color;
        if (tps <= 15) color = "§c";
        else if (tps <= 18) color = "§p";
        else color = "§ä";
        player.sendPlayerListHeaderAndFooter(header, Formatter.parseText(footer
                .replace("%tps", color.toString() + Math.round(tps))
                .replace("%players", String.valueOf(Bukkit.getOnlinePlayers().size()))
        ));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        updateTabList(player);
    }

}
