package de.slimecloud.hardsmp.ui.scoreboard;

import de.cyklon.spigotutils.tuple.Pair;
import de.cyklon.spigotutils.ui.scoreboard.PlayerScoreboardUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Scoreboard {

	private final PlayerScoreboardUI<Component> scoreboard;

	public Scoreboard(Plugin plugin, Player player) {
		this.scoreboard = PlayerScoreboardUI.getAdventurePlayerScoreboard(plugin, player);
		scoreboard.setTitle(Component.text("Hard")
				.color(TextColor.color(0xFFA100))
				.append(Component.text("-")
						.color(TextColor.color(0x434343)))
				.append(Component.text("SMP")
						.color(TextColor.color(0x358D90))));
	}

	private Component getLine(int rank, OfflinePlayer player, int points) {
		int color = switch (rank) {
			case 1 -> 15444788;
			case 2 -> 12632256;
			case 3 -> 13206886;
			default -> -1;
		};
		TextDecoration[] decoration = rank<=3 ?  new TextDecoration[]{TextDecoration.BOLD} : new TextDecoration[0];
		return Component.text(rank + ". ")
				.color(TextColor.color(color))
				.decorate(decoration)
				.append(Component.text(Objects.requireNonNullElse(player.getName(), player.getUniqueId().toString())))
				.append(Component.text("     " + points)
						.color(TextColor.color(0x605AF6)));
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
		while (score.get()>4) scoreboard.setEmptyLine(score.getAndDecrement());

		Pair<Integer, Integer> userData = stats.get(player.getUniqueId());
		Pair<Integer, Map.Entry<UUID, Integer>> nextData = stats.getNext(userData.first());
		Pair<Integer, Map.Entry<UUID, Integer>> previousData = stats.getPrevious(userData.first());

		scoreboard.setEmptyLine(4);

		if (nextData!=null) scoreboard.setLine(3, getLine(nextData));
		else scoreboard.setEmptyLine(3);

		scoreboard.setLine(2, getLine(userData.first(), player, userData.second()));

		if (previousData!=null) scoreboard.setLine(1, getLine(previousData));
		else scoreboard.setEmptyLine(1);

		scoreboard.update();
	}
}
