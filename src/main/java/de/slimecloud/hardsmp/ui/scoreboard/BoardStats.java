package de.slimecloud.hardsmp.ui.scoreboard;

import de.slimecloud.hardsmp.player.EventPlayer;
import de.slimecloud.hardsmp.player.PlayerController;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;
import java.util.stream.Collectors;

public class BoardStats {

	private Map<UUID, Integer> pointMap = new HashMap<>();

	public BoardStats() {
		for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
			EventPlayer player = PlayerController.getPlayer(p);
			//TODO replace getPoints with getActualPoints
			pointMap.put(player.getUniqueId(), (int) Math.round(player.getPoints()));
		}
	}

	public Map<UUID, Integer> getTopPlayers(int limit) {
		return pointMap.entrySet().stream()
				.sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
				.limit(limit)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, LinkedHashMap::new));
	}
}
