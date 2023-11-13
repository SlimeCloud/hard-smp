package de.slimecloud.hardsmp.build;

import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

import java.util.Map;

@RequiredArgsConstructor
public class ImmutableBuild implements Build {

    private final Map<Vector, BlockData> blocks;
    private final Map<Vector, EntityData> entities;
    private final byte[] bytes;

    public ImmutableBuild(Build build) {
        this(build.getBlocks(), build.getEntities(), build.getBytes());
    }

    @Override
    public Map<Vector, BlockData> getBlocks() {
        return blocks;
    }

    @Override
    public Map<Vector, EntityData> getEntities() {
        return entities;
    }

    @Override
    public void build(Location location) {
        BuildFormat.build(this, location);
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }
}