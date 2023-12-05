package de.slimecloud.hardsmp.subevent;

import org.bukkit.entity.Player;

import java.util.Collection;

public interface SubEvent {

    void setup(Collection<Player> players);

    void stop();

    void join(Player player);

    void leave(Player player);

    Collection<Player> getPlayers();

}
