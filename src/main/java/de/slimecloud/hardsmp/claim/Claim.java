package de.slimecloud.hardsmp.claim;

import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.database.Autoincrement;
import de.slimecloud.hardsmp.database.DataClass;
import de.slimecloud.hardsmp.database.Key;
import de.slimecloud.hardsmp.database.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Table(name = "claims")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Claim extends DataClass {

    public static Map<Integer, Claim> allClaims = loadAll(Claim::new, Collections.emptyMap()).stream().collect(Collectors.toMap(Claim::getId, c -> c));
    private String uuid;
    private int x1, z1, x2, z2;

    @Key
    @Autoincrement
    private int id;

    public static Set<Claim> load(UUID uuid) {
        return loadAll(Claim::new, Map.of("uuid", uuid.toString())).stream().peek(c -> allClaims.put(c.id, c)).collect(Collectors.toSet());
    }

    @Override
    public synchronized Claim save() {
        super.save();
        allClaims.put(id, this);
        for (Claim c : allClaims.values()) {
            System.out.println(c.x1);
            System.out.println(c.z1);
            System.out.println(c.x2);
            System.out.println(c.z2);

        }
        return this;
    }

    public boolean contains(Location loc) {
        return Math.min(x1, x2) <= loc.x() && loc.x() <= Math.max(x1, x2) && Math.min(z1, z2) <= loc.z() && loc.z() <= Math.max(z1, z2);
    }

    public boolean containsPlayer(Location loc) {
        Location newLoc = new Location(
                loc.getWorld(),
                loc.getX() < 0 ? (int) loc.getX() - 1 : (int) loc.getX(),
                loc.getY(),
                loc.getZ() < 0 ? (int) loc.getZ() - 1 : (int) loc.getZ()
        );
        return Math.min(x1, x2) <= newLoc.x() && newLoc.x() <= Math.max(x1, x2) && Math.min(z1, z2) <= newLoc.z() && newLoc.z() <= Math.max(z1, z2);
    }

    public boolean overlaps(Location loc1, Location loc2) {
        return Math.min(x1, x2) <= Math.max(loc1.getX(), loc2.getX())
                && Math.min(z1, z2) <= Math.max(loc1.getZ(), loc2.getZ())
                && Math.min(loc1.getX(), loc2.getX()) <= Math.max(x1, x2)
                && Math.min(loc1.getZ(), loc2.getZ()) <= Math.max(z1, z2);
    }

    public void delete() {
        HardSMP.getInstance().getDatabase().run(handle -> handle.createUpdate("delete from claims where id = :id").bind("id", id).execute());
        allClaims.remove(id);
    }

    public int getSize() {
        return Math.abs(x1 - x2) * Math.abs(z1 - z2);
    }
}