package de.slimecloud.hardsmp.player;

import de.slimecloud.hardsmp.player.data.PointCategory;
import de.slimecloud.hardsmp.player.data.Points;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
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
			p.setPoints(p.getPoints() + points);
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
			addPoints(points * -1);
		}

		@Override
		public double getPoints() {
			return getData().getPoints();
		}

		@Override
		public double getActualPoints() {
			double points = getPoints();
			points += PointCategory.CROUCH_ONE_CM.calculate(player.getStatistic(Statistic.CROUCH_ONE_CM));
			points += PointCategory.FLY_ONE_CM.calculate(player.getStatistic(Statistic.FLY_ONE_CM));
			points += PointCategory.SPRINT_ONE_CM.calculate(player.getStatistic(Statistic.SPRINT_ONE_CM));
			points += PointCategory.SWIM_ONE_CM.calculate(player.getStatistic(Statistic.SWIM_ONE_CM));
			points += PointCategory.WALK_ONE_CM.calculate(player.getStatistic(Statistic.WALK_ONE_CM));
			points += PointCategory.WALK_ON_WATER_ONE_CM.calculate(player.getStatistic(Statistic.WALK_ON_WATER_ONE_CM));
			points += PointCategory.WALK_UNDER_WATER_ONE_CM.calculate(player.getStatistic(Statistic.WALK_UNDER_WATER_ONE_CM));
			points += PointCategory.BOAT_ONE_CM.calculate(player.getStatistic(Statistic.BOAT_ONE_CM));
			points += PointCategory.AVIATE_ONE_CM.calculate(player.getStatistic(Statistic.AVIATE_ONE_CM));
			points += PointCategory.HORSE_ONE_CM.calculate(player.getStatistic(Statistic.HORSE_ONE_CM));
			points += PointCategory.MINECART_ONE_CM.calculate(player.getStatistic(Statistic.MINECART_ONE_CM));
			points += PointCategory.PIG_ONE_CM.calculate(player.getStatistic(Statistic.PIG_ONE_CM));
			points += PointCategory.STRIDER_ONE_CM.calculate(player.getStatistic(Statistic.STRIDER_ONE_CM));

			points *= 0.1 * (Math.pow(0.5, (player.getStatistic(Statistic.PLAY_ONE_MINUTE) / (115 * 180d) - 6.5)) + 10);
			return points;
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
