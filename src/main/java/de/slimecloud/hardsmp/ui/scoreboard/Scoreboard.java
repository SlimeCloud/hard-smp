package de.slimecloud.hardsmp.ui.scoreboard;

import de.cyklon.spigotutils.ui.scoreboard.PlayerScoreboardUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Scoreboard {

	private PlayerScoreboardUI<Component> scoreboard;

	public Scoreboard(Player player) {
		this.scoreboard = PlayerScoreboardUI.getAdventurePlayerScoreboard(player);
		scoreboard.setTitle(Component.text("Hard")
				.color(TextColor.color(0xFFA100))
				.append(Component.text("-")
						.color(TextColor.color(0x434343)))
				.append(Component.text("SMP")
						.color(TextColor.color(0x358D90))));
	}

	public void update() {
		scoreboard.clearLines();
	}
}
