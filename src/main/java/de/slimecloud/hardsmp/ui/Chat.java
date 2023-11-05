package de.slimecloud.hardsmp.ui;

import de.cyklon.spigotutils.adventure.Formatter;
import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.ui.scoreboard.ScoreboardManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Chat implements Listener {

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        event.setCancelled(true);
        Player player = event.getPlayer();
        User user = HardSMP.getInstance().getLuckPerms().getUserManager().getUser(player.getUniqueId());
        String pref;
        if (user==null) pref = null;
        else pref = user.getCachedData().getMetaData().getPrefix();

        Component prefix = pref==null ? Component.empty() : Formatter.parseText("&", "[" + pref + "&r] ");
        int rank = ScoreboardManager.STATS.get(player.getUniqueId()).first();
        String color = switch (rank) {
            case 1 -> "§6";
            case 2 -> "§i";
            case 3 -> "§y";
            default -> "";
        };
        Component name = prefix.append(Formatter.parseText(color + ((rank<=5) ? rank + "#" : "") + player.getName() + "§r: "));

        LegacyComponentSerializer serializer = LegacyComponentSerializer.legacySection();
        String msg = serializer.serialize(event.originalMessage());
        Bukkit.broadcast(name.append(Formatter.parseText("&", msg)));
    }

}
