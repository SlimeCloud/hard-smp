package de.slimecloud.hardsmp.player.data;

import de.slimecloud.hardsmp.database.DataClass;
import de.slimecloud.hardsmp.database.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

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

}