package de.slimecloud.hardsmp.build;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

import java.io.*;
import java.util.Map;

public interface Build {

    static Build scan(File file, Location pos1, Location pos2) throws IOException {
        return scan(file, pos1, pos2, false, false);
    }

    static Build scan(File file, Location pos1, Location pos2, boolean copyAir, boolean copyEntities) throws IOException {
        Build build = scan(pos1, pos2, copyAir, copyEntities);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(build.getBytes());
        }
        return build;
    }

    static Build scan(Location pos1, Location pos2) {
        return scan(pos1, pos2, false, false);
    }

    static Build scan(Location pos1, Location pos2, boolean copyAir, boolean copyEntities) {
        return BuildFormat.scan(pos1.toVector(), pos2.toVector(), pos1.getWorld(), copyAir, copyEntities);
    }

    static Build load(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            return load(fis.readAllBytes());
        }
    }

    static Build load(byte[] data) {
        return BuildFormat.load(data);
    }

    Map<Vector, BlockData> getBlocks();

    Map<Vector, EntityData> getEntities();

    //starts with the corner that has the smallest coordinates
    // -> fv1 = 0 0 0 = location
    //all block-coord are subtracted with the fv1, so you get the relativ coods in the Schematic
    void build(Location location);

    byte[] getBytes();

}
