package de.slimecloud.hardsmp.ui;

import de.cyklon.spigotutils.adventure.Formatter;
import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.player.EventPlayer;
import de.slimecloud.hardsmp.player.PlayerController;
import me.lucko.spark.api.Spark;
import me.lucko.spark.api.statistic.StatisticWindow;
import me.lucko.spark.api.statistic.types.DoubleStatistic;
import net.kyori.adventure.text.Component;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitTask;

public class Tablist implements Listener {

    private final Component header;
    private final String prefix;
    private final String name;
    private final String footer;

    private final Spark spark;

    private final BukkitTask updateTask;

    public Tablist(HardSMP plugin) {
        this.header = Formatter.parseText(plugin.getConfig().getString("ui.tablist.header", "Hard-SMP"));
        this.prefix = plugin.getConfig().getString("ui.tablist.prefix", "[%prefix]");
        this.name = plugin.getConfig().getString("ui.tablist.name", "%prefix %name %points");
        this.footer = plugin.getConfig().getString("ui.tablist.footer", "[%tps]");
        this.spark = plugin.getSpark();
        this.updateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            Bukkit.getOnlinePlayers().forEach(this::updateTabList);
        }, 0, 20*10); // period of 10s
    }

    private void updateTabList(Player player) {
        DoubleStatistic<StatisticWindow.TicksPerSecond> stat = spark.tps();
        double tps;
        if (stat==null) tps = Bukkit.getTPS()[0];
        else tps = stat.poll(StatisticWindow.TicksPerSecond.SECONDS_10);
        ChatColor color;
        if (tps<=15) color = ChatColor.RED;
        else if (tps <= 18) color = ChatColor.YELLOW;
        else color = ChatColor.GREEN;
        player.sendPlayerListHeaderAndFooter(header, Formatter.parseText(footer.replace("%tps", color.toString() + Math.round(tps))));

        User user = HardSMP.getInstance().getLuckPerms().getUserManager().getUser(player.getUniqueId());
        String prefix;
        if (user==null) prefix = null;
        else prefix = user.getCachedData().getMetaData().getPrefix();
        EventPlayer ep = PlayerController.getPlayer((HumanEntity) player);
        int points = (int) Math.round(ep.getActualPoints());
        player.playerListName(Formatter.parseText(name.replace("%prefix", prefix==null ? "" : this.prefix.replace("%prefix", prefix.replace("&", "§"))).replace("%name", player.getName()).replace("%points", String.valueOf(points))));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        updateTabList(player);
    }

}
