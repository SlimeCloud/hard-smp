package de.slimecloud.hardsmp.build;

import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

public interface EntityData {

    EntityType getType();

    Vector getVelocity();

    String getCustomName();

}
