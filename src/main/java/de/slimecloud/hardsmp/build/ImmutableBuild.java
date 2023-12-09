package de.slimecloud.hardsmp.build;

import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
public class ImmutableBuild implements Build {

    private final Map<Vector, BlockData> blocks;
    private final Map<Vector, EntityData> entities;
    private final byte[] bytes;
    private final Function<Object, Boolean> equalFunc;

    public ImmutableBuild(Build build) {
        this(build.getBlocks(), build.getEntities(), build.getBytes(), build::equals);
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

    @Override
    public boolean equals(Object obj) {
        return equalFunc.apply(obj);
    }
}