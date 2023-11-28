package de.slimecloud.hardsmp.claim;

import de.slimecloud.hardsmp.database.DataClass;
import de.slimecloud.hardsmp.database.Table;
import lombok.*;
import org.bukkit.Location;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Table(name = "claims")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Claim extends DataClass {

    private String uuid;
    private int x1, z1, x2, z2;

    public static List<Claim> load(UUID uuid) {
        return loadAll(Claim::new, Map.of("uuid", uuid.toString()));
    }

    public boolean contains(Location loc) {
        return Math.min(x1, x2) <= loc.x() && loc.x() <= Math.max(x1, x2) && Math.min(z1, z2) <= loc.z() && loc.z() <= Math.max(z1, z2);
    }

    public boolean overlaps(Location loc1, Location loc2) {
        return Math.min(x1, x2) <= Math.max(loc1.getX(), loc2.getX())
                && Math.min(z1, z2) <= Math.max(loc1.getZ(), loc2.getZ())
                && Math.min(loc1.getX(), loc2.getX()) <= Math.max(x1, x2)
                && Math.min(loc1.getZ(), loc2.getZ()) <= Math.max(z1, z2);
    }

}