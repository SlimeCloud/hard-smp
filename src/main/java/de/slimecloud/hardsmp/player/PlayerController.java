package de.slimecloud.hardsmp.player;

import de.slimecloud.hardsmp.Main;
import de.slimecloud.hardsmp.player.data.Points;
import de.slimecloud.hardsmp.player.data.Team;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PlayerController {

	public static EventPlayer getPlayer(OfflinePlayer player) {
		return new EventPlayerImpl(player);
	}

	public static TeamPlayer getTeamPlayer(OfflinePlayer player, EventTeam team) {
		return new TeamPlayerImpl(player, team);
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
		public TeamPlayer joinTeam(EventTeam team) {
			team.add(this);
			return getTeamPlayer(getOfflinePlayer(), team);
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

		@Override
		public TeamPlayer createTeam(String name) {
			Team team = new Team(UUID.randomUUID(), name, getUniqueId().toString());
			team.add(this);
			team.save();
			return getTeamPlayer(getOfflinePlayer(), new SyncedTeamImpl(team.getUniqueId()));
		}

		@Override
		public @Nullable EventTeam getTeam() {
			if (getPlayer()==null) throw new IllegalStateException("player must be online to get the team");
			UUID id = UUID.fromString(getPlayer().getPersistentDataContainer().get(Main.getInstance().TEAM_KEY, PersistentDataType.STRING));
			return id == null ? null : new SyncedTeamImpl(id);
		}
	}

	private static class TeamPlayerImpl extends EventPlayerImpl implements TeamPlayer {

		private final EventTeam team;

		public TeamPlayerImpl(OfflinePlayer player, EventTeam team) {
			super(player);
			this.team = team;
		}

		@Override
		public void addMultipliedPoints(double points) {
			getTeam().getPlayers().forEach(p -> p.addPoints(points * getTeam().getMultiplier()));
		}

		@Override
		public @NotNull EventTeam getTeam() {
			return team;
		}

		@Override
		public EventPlayer leaveTeam() {
			team.remove(this);
			return PlayerController.getPlayer(player);
		}
	}


}
