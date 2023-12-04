package de.slimecloud.hardsmp.subevent.replika;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class Plot {

    private final Replika replika;
    @Getter
    private final Location position;

    public Plot(Replika replika, Location position) {
        this.replika = replika;
        this.position = position;
    }

    public void build() {
        replika.getPlotSchematic().build(position);

        //only require for Barriers
        World world = position.getWorld();
        for (int y = position.getBlockY(); y <= position.getBlockY()+replika.getTopBorderHeight(); y++) {
            for (int x = position.getBlockX(); x <= position.getBlockX()+replika.getPlotWidth(); x++) {
                world.getBlockAt(x, y, position.getBlockZ()-1).setType(Material.BARRIER);
                world.getBlockAt(x, y, position.getBlockZ()+replika.getPlotLength()+1).setType(Material.BARRIER);
            }
        }
        for (int y = position.getBlockY(); y <= position.getBlockY()+replika.getTopBorderHeight(); y++) {
            for (int z = position.getBlockZ(); z <= position.getBlockZ() + replika.getPlotLength(); z++) {
                world.getBlockAt(position.getBlockY()-1, y, z).setType(Material.BARRIER);
                world.getBlockAt(position.getBlockY()+replika.getPlotWidth()+1, y, z).setType(Material.BARRIER);
            }
        }
        for (int z = position.getBlockZ(); z <= position.getBlockZ() + replika.getPlotLength(); z++) {
            for (int x = position.getBlockX(); x <= position.getBlockX()+replika.getPlotWidth(); x++) {
                world.getBlockAt(x, position.getBlockY()+replika.getTopBorderHeight(), z).setType(Material.BARRIER);
            }
        }
    }
}
