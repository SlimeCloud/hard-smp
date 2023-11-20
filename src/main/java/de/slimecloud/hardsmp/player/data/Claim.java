package de.slimecloud.hardsmp.player.data;

import de.slimecloud.hardsmp.database.DataClass;
import de.slimecloud.hardsmp.database.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bukkit.Location;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Table(name = "claims")
@AllArgsConstructor
@NoArgsConstructor
public class Claim extends DataClass {

    public String uuid;
    public int x1, z1, x2, z2;

    public static List<Claim> load(UUID uuid) {
        return loadAll(Claim::new, Map.of("uuid", uuid.toString()));
    }

    public boolean contains(Location loc) {
        return Math.min(x1, x2) <= loc.x() && loc.x() <= Math.max(x1, x2) && Math.min(z1, z2) <= loc.z() && loc.z() <= Math.max(z1, z2);
    }

}