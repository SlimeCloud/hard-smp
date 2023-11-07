package de.slimecloud.hardsmp.ui.scoreboard;

import de.cyklon.spigotutils.adventure.Formatter;
import de.cyklon.spigotutils.tuple.Pair;
import de.cyklon.spigotutils.ui.scoreboard.PlayerScoreboardUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
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
    private int max = 0;

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

    private Component getLine(int rank, OfflinePlayer player, int points, Character style) {
        StringBuilder s = new StringBuilder((style!=null ? '§'+style : "") + lines.getOrDefault(rank, lineDefault)
                .replace("%rank", String.valueOf(rank))
                .replace("%name", Objects.requireNonNullElse(player.getName(), player.getUniqueId().toString())));
        if (style!=null) s.append("§r");
        if (s.length()>max) max = s.length();
        while (s.length()!=max) s.append(" ");
        s = new StringBuilder(s.toString().replace("%points", String.valueOf(points)));
        return Formatter.parseText(s.toString());
    }

    private Component getLine(Pair<Integer, Map.Entry<UUID, Integer>> data, Character style) {
        return getLine(data.first(), Bukkit.getOfflinePlayer(data.second().getKey()), data.second().getValue(), style);
    }

    public void update(BoardStats stats) {
        Player player = scoreboard.getPlayers().get(0);

        if (!player.isOnline() || scoreboard.isDeleted()) return;

        Map<UUID, Integer> top = stats.getTopPlayers(5);

        final int maxBefore = max;

        scoreboard.clearLines();

        AtomicInteger score = new AtomicInteger(9);
        AtomicInteger rank = new AtomicInteger(1);

        top.forEach((k, v) -> scoreboard.setLine(score.getAndDecrement(), getLine(rank.getAndIncrement(), Bukkit.getOfflinePlayer(k), v, null)));
        while (score.get() > 4) scoreboard.setEmptyLine(score.getAndDecrement());

        Pair<Integer, Integer> userData = stats.get(player.getUniqueId());
        Pair<Integer, Map.Entry<UUID, Integer>> nextData = stats.getNext(userData.first());
        Pair<Integer, Map.Entry<UUID, Integer>> previousData = stats.getPrevious(userData.first());

        scoreboard.setEmptyLine(4);

        if (nextData != null) scoreboard.setLine(3, getLine(nextData, 'o'));
        else scoreboard.setEmptyLine(3);

        scoreboard.setLine(2, getLine(userData.first(), player, userData.second(), 'l'));

        if (previousData != null) scoreboard.setLine(1, getLine(previousData, 'o'));
        else scoreboard.setEmptyLine(1);

        if (max!=maxBefore) update(stats);
        else scoreboard.update();
    }
}
