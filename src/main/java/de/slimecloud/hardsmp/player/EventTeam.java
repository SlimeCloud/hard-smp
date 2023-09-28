package de.slimecloud.hardsmp.player;

import org.bukkit.entity.Player;

import java.util.Collection;

public interface EventTeam {

    Collection<TeamPlayer> getPlayers();

    TeamPlayer getPlayer(EventPlayer player);

    TeamPlayer getPlayer(Player player);

    TeamPlayer getTeamLeader();

    String getName();

    double getMultiplier();

}
