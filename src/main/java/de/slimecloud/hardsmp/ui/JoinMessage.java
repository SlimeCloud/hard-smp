package de.slimecloud.hardsmp.ui;

import de.slimecloud.hardsmp.HardSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinMessage implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.joinMessage(Component.text("--> ").color(NamedTextColor.GREEN).append(Chat.getName(event.getPlayer())));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        event.quitMessage(Component.text("<-- ").color(NamedTextColor.RED).append(Chat.getName(event.getPlayer())));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.deathMessage(Chat.getName(event.getPlayer())
                .append(Component.text(" ist gestorben ").color(NamedTextColor.GRAY))
                .append(Component.text("[").color(NamedTextColor.DARK_GRAY))
                .append(Component.text("-" + event.getPlayer().getStatistic(Statistic.DEATHS) * 100).color(NamedTextColor.RED))
                .append(Component.text("]").color(NamedTextColor.DARK_GRAY))
        );
    }
}
