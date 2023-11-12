package de.slimecloud.hardsmp.ui;

import de.cyklon.spigotutils.adventure.Formatter;
import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.ui.scoreboard.ScoreboardManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public class Chat implements Listener {

    private final String prefix;
    private final String format;
    private final Map<Integer, String> nameColor;
    private final Map<Integer, String> rankColor;

    public Chat(FileConfiguration config) {
        this.prefix = config.getString("ui.chat.prefix", "[%prefix]");
        this.format = config.getString("ui.chat.format", "%rank %prefix %name:");
        this.nameColor = new HashMap<>();
        this.rankColor = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("ui.chat.color");

        if (section != null) for (String key : section.getKeys(false)) {
            String line = section.getString(key);
            if (line != null) {
                if (key.equals("name_default")) nameColor.put(-1, line);
                else if (key.startsWith("name_")) nameColor.put(Integer.parseInt(key.split("_")[1]), line);

                else if (key.equals("rank_default")) rankColor.put(-1, line);
                else if (key.startsWith("rank_")) rankColor.put(Integer.parseInt(key.split("_")[1]), line);
            }
        }
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        event.setCancelled(true);
        Player player = event.getPlayer();
        User user = HardSMP.getInstance().getLuckPerms().getUserManager().getUser(player.getUniqueId());
        String prefix;
        if (user == null) prefix = null;
        else prefix = user.getCachedData().getMetaData().getPrefix();

        prefix = prefix == null ? "" : this.prefix.replace("%prefix", prefix.replace("&", "§"));
        int rank = ScoreboardManager.STATS.get(player.getUniqueId()).first();
        String nameColor = this.nameColor.getOrDefault(rank, this.nameColor.get(-1));
        String rankColor = this.rankColor.getOrDefault(rank, this.rankColor.get(-1));
        Component format = Formatter.parseText(this.format
                .replace("%colr", rankColor)
                .replace("%coln", nameColor)
                .replace("%rank", String.valueOf(rank))
                .replace("%prefix", prefix)
                .replace("%name", player.getName()));

        LegacyComponentSerializer serializer = LegacyComponentSerializer.legacySection();
        String msg = serializer.serialize(event.originalMessage());
        Bukkit.broadcast(format.append(Formatter.parseText("&", "&r" + msg)));
    }

}
