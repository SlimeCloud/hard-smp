package de.slimecloud.hardsmp.ui.scoreboard;

import de.cyklon.spigotutils.tuple.Pair;
import de.slimecloud.hardsmp.player.EventPlayer;
import de.slimecloud.hardsmp.player.PlayerController;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BoardStats {

    private final List<Map.Entry<UUID, Integer>> topList;

    public BoardStats() {
        Map<UUID, Integer> pointMap = new HashMap<>();
        for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
            EventPlayer player = PlayerController.getPlayer(p);
            pointMap.put(player.getUniqueId(), (int) Math.round(player.getActualPoints()));
        }
        this.topList = pointMap.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .collect(Collectors.toList());
    }

    public Map<UUID, Integer> getTopPlayers(int limit) {
        return topList.stream()
                .limit(limit)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, LinkedHashMap::new));
    }

    private int getRank(UUID uuid) {
        int index = IntStream.range(0, topList.size())
                .filter(i -> topList.get(i).getKey().equals(uuid))
                .findFirst()
                .orElse(-1);
        return (index == -1 ? topList.size() : index) + 1;
    }

    public Pair<Integer, Integer> get(UUID uuid) {
        int rank = getRank(uuid);
        if (rank == topList.size() + 1) return new Pair<>(rank, 0);
        return new Pair<>(rank, topList.get(rank - 1).getValue());
    }

    public Pair<Integer, Map.Entry<UUID, Integer>> getNext(int rank) {
        if (rank - 2 < 0) return null;
        return new Pair<>(rank - 1, topList.get(rank - 2));
    }

    public Pair<Integer, Map.Entry<UUID, Integer>> getPrevious(int rank) {
        if (rank >= topList.size()) return null;
        return new Pair<>(rank + 1, topList.get(rank));
    }
}
