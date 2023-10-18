package de.slimecloud.hardsmp.player;

import de.slimecloud.hardsmp.player.data.Points;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PlayerController {

	public static EventPlayer getPlayer(HumanEntity player) {
		return getPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
	}

	public static EventPlayer getPlayer(OfflinePlayer player) {
		return new EventPlayerImpl(player);
	}

	@RequiredArgsConstructor
	private static class EventPlayerImpl implements EventPlayer {

		protected final OfflinePlayer player;


		protected Points getData() {
			return Points.load(getOfflinePlayer().getUniqueId().toString());
		}

		@Override
		public void addPoints(double points) {
			Points p = getData();
			p.setPoints(p.getPoints()+points);
			p.save();
		}

		@Override
		public void setPoints(double points) {
			Points p = getData();
			p.setPoints(points);
			p.save();
		}

		@Override
		public void removePoints(double points) {
			addPoints(points*-1);
		}

		@Override
		public double getPoints() {
			return getData().getPoints();
		}

		@Override
		@Nullable
		public Player getPlayer() {
			return getOfflinePlayer().getPlayer();
		}

		@Override
		public OfflinePlayer getOfflinePlayer() {
			return player;
		}

		@Override
		public UUID getUniqueId() {
			return getOfflinePlayer().getUniqueId();
		}
	}

}
