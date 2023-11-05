package de.slimecloud.hardsmp.ui.scoreboard;

import de.cyklon.spigotutils.adventure.Formatter;
import de.cyklon.spigotutils.tuple.Pair;
import de.cyklon.spigotutils.ui.scoreboard.PlayerScoreboardUI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Scoreboard {

    private final PlayerScoreboardUI<Component> scoreboard;
    private final String lineDefault;
    private final Map<Integer, String> lines;

    public Scoreboard(Plugin plugin, Player player) {
        FileConfiguration config = plugin.getConfig();
        this.lineDefault = config.getString("ui.scoreboard.text.default", "%rank. %name   %points");
        this.lines = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("ui.scoreboard.text");
        if (section != null) for (String key : section.getKeys(false)) {
            if (key.startsWith("rank_")) {
                String line = section.getString(key);
                if (line != null) lines.put(Integer.parseInt(key.split("_")[1]), line);
            }
        }
        this.scoreboard = PlayerScoreboardUI.getAdventurePlayerScoreboard(plugin, player);
        scoreboard.setTitle(Component.text(plugin.getConfig().getString("ui.scoreboard.title", "Hard-SMP")));
    }

    public PlayerScoreboardUI<Component> getUI() {
        return scoreboard;
    }

    private Component getLine(int rank, OfflinePlayer player, int points) {
        return Formatter.parseText(lines.getOrDefault(rank, lineDefault)
                .replace("%rank", String.valueOf(rank))
                .replace("%name", Objects.requireNonNullElse(player.getName(), player.getUniqueId().toString()))
                .replace("%points", String.valueOf(points)));
    }

    private Component getLine(Pair<Integer, Map.Entry<UUID, Integer>> data) {
        return getLine(data.first(), Bukkit.getOfflinePlayer(data.second().getKey()), data.second().getValue());
    }

    public void update(BoardStats stats) {
        Player player = scoreboard.getPlayers().get(0);

        if (!player.isOnline() || scoreboard.isDeleted()) return;

        Map<UUID, Integer> top = stats.getTopPlayers(5);

        scoreboard.clearLines();

        AtomicInteger score = new AtomicInteger(9);
        AtomicInteger rank = new AtomicInteger(1);

        top.forEach((k, v) -> scoreboard.setLine(score.getAndDecrement(), getLine(rank.getAndIncrement(), Bukkit.getOfflinePlayer(k), v)));
        while (score.get() > 4) scoreboard.setEmptyLine(score.getAndDecrement());

        Pair<Integer, Integer> userData = stats.get(player.getUniqueId());
        Pair<Integer, Map.Entry<UUID, Integer>> nextData = stats.getNext(userData.first());
        Pair<Integer, Map.Entry<UUID, Integer>> previousData = stats.getPrevious(userData.first());

        scoreboard.setEmptyLine(4);

        if (nextData != null) scoreboard.setLine(3, getLine(nextData));
        else scoreboard.setEmptyLine(3);

        scoreboard.setLine(2, getLine(userData.first(), player, userData.second()));

        if (previousData != null) scoreboard.setLine(1, getLine(previousData));
        else scoreboard.setEmptyLine(1);

        scoreboard.update();
    }
}
