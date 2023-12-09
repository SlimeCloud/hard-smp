package de.slimecloud.hardsmp.build;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class BuildFormat {

    private static final String SEPERATOR = " ";

    private static Vector relativ(Vector relativPoint, Vector other) {
        return new Vector(
                other.getX() - relativPoint.getX(),
                other.getY() - relativPoint.getY(),
                other.getZ() - relativPoint.getZ()
        );
    }

    private static Vector parseVector(String s) {
        String[] c = s.split(",");
        return new Vector(Double.parseDouble(c[0]), Double.parseDouble(c[1]), Double.parseDouble(c[2]));
    }

    static Build scan(Vector pos1, Vector pos2, World world, boolean copyAir, boolean copyEntities) {
        BuildImpl build = new BuildImpl();
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); PrintWriter writer = new PrintWriter(bos, true, StandardCharsets.UTF_8)) {
            Vector fV1 = new Vector(Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()));
            Vector fV2 = new Vector(Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()));

            //fV1relativ is always 0, 0, 0
            Vector fV2relativ = relativ(fV1, fV2);

            for (double x = 0; x <= fV2relativ.getX(); x++) {
                for (double y = 0; y <= fV2relativ.getY(); y++) {
                    for (double z = 0; z <= fV2relativ.getZ(); z++) {
                        Vector vRelativ = new Vector(x, y, z);
                        Vector v = vRelativ.clone().add(fV1);

                        Block block = world.getBlockAt(v.toLocation(world));
                        BlockData blockData = block.getBlockData();

                        String s = String.join(",", String.valueOf(vRelativ.getBlockX()), String.valueOf(vRelativ.getBlockY()), String.valueOf(vRelativ.getBlockZ())) + SEPERATOR + blockData.toString().substring(15, blockData.toString().length() - 1);
                        if (!copyAir && block.getType().equals(Material.AIR)) continue;
                        writer.println(s);
                        build.putBlock(vRelativ, blockData);
                    }
                }
            }

            if (copyEntities) {
                for (Entity e : world.getEntities()) {
                    Vector entityPosition = e.getLocation().toVector();
                    if (entityPosition.getX() >= fV1.getX() && entityPosition.getX() <= fV2.getX() && entityPosition.getY() >= fV1.getY() && entityPosition.getY() <= fV2.getY() && entityPosition.getZ() >= fV1.getX() && entityPosition.getZ() <= fV2.getZ()) {
                        if (!(e instanceof Player)) {
                            writer.println("Entity"
                                    + SEPERATOR
                                    + relativ(fV1, e.getLocation().toVector())
                                    + SEPERATOR
                                    + e.getType().name()
                                    + SEPERATOR
                                    + e.getVelocity()
                                    + SEPERATOR
                                    + e.getCustomName());
                            build.putEntity(relativ(fV1, e.getLocation().toVector()), new ImmutableEntityData(e.getType(), e.getVelocity(), e.getCustomName()));
                        }

                    }
                }
            }
            build.setBytes(bos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ImmutableBuild(build);
    }

    public static byte[] calculateBytes(Build build) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); PrintWriter writer = new PrintWriter(bos, true, StandardCharsets.UTF_8)) {
            for (Map.Entry<Vector, BlockData> data : build.getBlocks().entrySet()) {
                Vector vRelativ = data.getKey();
                ;
                BlockData blockData = data.getValue();
                String s = String.join(",", String.valueOf(vRelativ.getBlockX()), String.valueOf(vRelativ.getBlockY()), String.valueOf(vRelativ.getBlockZ())) + SEPERATOR + blockData.toString().substring(15, blockData.toString().length() - 1);
                writer.println(s);
            }

            for (Map.Entry<Vector, EntityData> data : build.getEntities().entrySet()) {
                EntityData ed = data.getValue();
                writer.println("Entity"
                        + SEPERATOR
                        + data.getKey()
                        + SEPERATOR
                        + ed.getType().name()
                        + SEPERATOR
                        + ed.getVelocity()
                        + SEPERATOR
                        + ed.getCustomName());
            }
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static Build load(byte[] data) {
        BuildImpl build = new BuildImpl();
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data); BufferedReader br = new BufferedReader(new InputStreamReader(bis))) {
            for (String line; (line = br.readLine()) != null; ) {
                String[] astring = line.split(SEPERATOR);
                if (astring[0].equals("Entity"))
                    build.putEntity(parseVector(astring[1]), new ImmutableEntityData(EntityType.valueOf(astring[2]), parseVector(astring[3]), astring[4]));
                else build.putBlock(parseVector(astring[0]), Bukkit.createBlockData(astring[1]));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ImmutableBuild(build);
    }


    @RequiredArgsConstructor
    private static class ImmutableEntityData implements EntityData {

        private final EntityType type;
        private final Vector velocity;
        private final String customName;

        @Override
        public EntityType getType() {
            return type;
        }

        @Override
        public Vector getVelocity() {
            return velocity;
        }

        @Override
        public String getCustomName() {
            return customName;
        }
    }

    static void build(Build build, Location location) {
        World world = location.getWorld();
        Vector pos = location.toVector();
        for (Map.Entry<Vector, BlockData> data : build.getBlocks().entrySet())
            world.getBlockAt(data.getKey().add(pos).toLocation(world)).setBlockData(data.getValue());
        for (Map.Entry<Vector, EntityData> data : build.getEntities().entrySet()) {
            EntityData ed = data.getValue();
            Entity e = world.spawnEntity(data.getKey().add(pos).toLocation(world), ed.getType());
            e.setVelocity(ed.getVelocity());
            e.setCustomName(ed.getCustomName());
        }
    }

    @Setter
    private static class BuildImpl implements Build {

        private Map<Vector, BlockData> blocks;
        private Map<Vector, EntityData> entities;
        private byte[] bytes;

        public BuildImpl() {
            this.blocks = new HashMap<>();
            this.entities = new HashMap<>();
            this.bytes = new byte[0];
        }

        public void putBlock(Vector pos, BlockData data) {
            blocks.put(pos, data);
        }

        public void putEntity(Vector pos, EntityData data) {
            entities.put(pos, data);
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
        public boolean equals(Object o) {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            BuildImpl build = (BuildImpl) o;

            return new EqualsBuilder().append(bytes, build.bytes).isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37).append(bytes).toHashCode();
        }
    }

}
