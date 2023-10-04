package de.slimecloud.hardsmp.player.data;

import de.slimecloud.hardsmp.Main;
import de.slimecloud.hardsmp.database.DataClass;
import de.slimecloud.hardsmp.database.Key;
import de.slimecloud.hardsmp.player.EventPlayer;
import de.slimecloud.hardsmp.player.EventTeam;
import de.slimecloud.hardsmp.player.PlayerController;
import de.slimecloud.hardsmp.player.TeamPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;


public class Team extends DataClass implements EventTeam {

	@Key
	private final String id;

	private String name;
	private String leader;

	private String playersJson;

	private transient Collection<UUID> players;

	public Team(UUID id) {
		this.id = id.toString();
	}

	public Team(UUID id, String name, String leader) {
		this.id = id.toString();
		this.name = name;
		this.leader = leader;
	}



	@Override
	@SuppressWarnings({"unchecked"})
	protected void finishedLoading() {
		players = gson.fromJson(playersJson, Collection.class);
	}

	@Override
	public synchronized DataClass save() {
		playersJson = gson.toJson(players);
		super.save();
		return this;
	}


	@Override
	public Collection<TeamPlayer> getPlayers() {
		Collection<TeamPlayer> result = new ArrayList<>();
		players.forEach(p -> result.add(getPlayer(Bukkit.getPlayer(p))));
		return result;
	}

	@Override
	public TeamPlayer getPlayer(EventPlayer player) {
		return getPlayer(player.getOfflinePlayer());
	}

	@Override
	public TeamPlayer getPlayer(OfflinePlayer player) {
		return PlayerController.getTeamPlayer(player, this);
	}

	@Override
	public TeamPlayer getTeamLeader() {
		return getPlayer(Bukkit.getPlayer(UUID.fromString(leader)));
	}

	@Override
	public void add(EventPlayer player) {
		if (player.getPlayer()==null) throw new IllegalStateException("player must be online to be added to a team");
		player.getPlayer().getPersistentDataContainer().set(Main.getInstance().TEAM_KEY, PersistentDataType.STRING, id);
		players.add(player.getUniqueId());
		save();
	}

	@Override
	public void remove(TeamPlayer player) {
		players.remove(player.getUniqueId());
		save();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public double getMultiplier() {
		return 1d/getPlayers().size();
	}

	@Override
	public UUID getUniqueId() {
		return UUID.fromString(id);
	}
}
