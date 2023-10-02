package de.slimecloud.hardsmp.player;

import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

public class PlayerController {

	public static EventPlayer getPlayer(OfflinePlayer player) {
		return new EventPlayerImpl(player);
	}

	@RequiredArgsConstructor
	private static class EventPlayerImpl implements EventPlayer {

		protected final OfflinePlayer player;


		@Override
		public void addPoints(double points) {

		}

		@Override
		public void setPoints(double points) {

		}

		@Override
		public void removePoints(double points) {

		}

		@Override
		public double getPoints() {
			return 0;
		}

		@Override
		public void setTeam(EventTeam team) {

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
		public EventTeam createTeam() {
			return null;
		}
	}

	private static class TeamPlayerImpl extends EventPlayerImpl implements TeamPlayer {

		public TeamPlayerImpl(OfflinePlayer player) {
			super(player);
		}

		@Override
		public void addMultipliedPoints(double points) {
			addPoints(points* getTeam().getMultiplier());
		}

		@Override
		public EventTeam getTeam() {
			return null;
		}

		@Override
		public void leaveTeam() {

		}
	}

	private static class EventTeamImpl implements EventTeam {

		@Override
		public Collection<TeamPlayer> getPlayers() {
			return Collections.emptyList();
		}

		@Override
		public TeamPlayer getPlayer(EventPlayer player) {
			return null;
		}

		@Override
		public TeamPlayer getPlayer(Player player) {
			return null;
		}

		@Override
		public TeamPlayer getTeamLeader() {
			return null;
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public double getMultiplier() {
			return 1d/getPlayers().size();
		}
	}

}
