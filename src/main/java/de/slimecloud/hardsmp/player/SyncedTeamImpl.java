package de.slimecloud.hardsmp.player;

import de.slimecloud.hardsmp.database.DataClass;
import de.slimecloud.hardsmp.player.data.Team;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class SyncedTeamImpl implements EventTeam {

	private final UUID id;

	private Team load() {
		return DataClass.load(() -> new Team(id), Map.of("id", id)).orElseGet(() -> new Team(id));
	}

	@Override
	public Collection<TeamPlayer> getPlayers() {
		return load().getPlayers();
	}

	@Override
	public TeamPlayer getPlayer(EventPlayer player) {
		return load().getPlayer(player);
	}

	@Override
	public TeamPlayer getPlayer(OfflinePlayer player) {
		return load().getPlayer(player);
	}

	@Override
	public TeamPlayer getTeamLeader() {
		return load().getTeamLeader();
	}

	@Override
	public void add(EventPlayer player) {
		load().add(player);
	}

	@Override
	public void remove(TeamPlayer player) {
		load().remove(player);
	}

	@Override
	public String getName() {
		return load().getName();
	}

	@Override
	public double getMultiplier() {
		return load().getMultiplier();
	}

	@Override
	public UUID getUniqueId() {
		return load().getUniqueId();
	}
}
