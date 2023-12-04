package de.slimecloud.hardsmp.commands.home;

import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.database.Autoincrement;
import de.slimecloud.hardsmp.database.DataClass;
import de.slimecloud.hardsmp.database.Key;
import de.slimecloud.hardsmp.database.Table;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.*;

@Table(name = "homes")
@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HomeData extends DataClass {

    @Key
    @Autoincrement
    private int id;
    @Getter
    private String uuid;
    @Getter
    private double xPos;
    @Getter
    private double yPos;
    @Getter
    private double zPos;
    @Getter
    private String worldName;
    @Getter
    private String homeName;

    public static List<HomeData> load(UUID uuid) {
        return loadAll(HomeData::new, Map.of("uuid", uuid.toString()));
    }

    public static Optional<HomeData> load(UUID uuid, String name) {
        return load(HomeData::new, Map.of("uuid", uuid.toString(), "homeName", name));
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(worldName), xPos, yPos, zPos);
    }

    public HomeData newHome(Location location, World world, String name, UUID playerUuid) {
        xPos = location.getX();
        yPos = location.getY();
        zPos = location.getZ();
        worldName = world.getName();
        homeName = name;
        uuid = playerUuid.toString();

        return this;
    }

    public void delete() {
        HardSMP.getInstance().getDatabase().run(handle -> handle.createUpdate("delete from homes where id = :id").bind("id", id).execute());
    }
}
