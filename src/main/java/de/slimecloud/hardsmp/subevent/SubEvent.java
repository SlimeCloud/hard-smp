package de.slimecloud.hardsmp.subevent;

import org.bukkit.entity.Player;

import java.util.Collection;

public interface SubEvent {

    void start(Collection<Player> players);

    void join(Player player);

    void leave(Player player);

    Collection<Player> getPlayers();

}
