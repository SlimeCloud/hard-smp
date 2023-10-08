package de.slimecloud.hardsmp.shop;

import de.slimecloud.hardsmp.Main;
import dev.sergiferry.playernpc.api.NPC;
import dev.sergiferry.playernpc.api.NPCLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

public class ShopNPC {

    private static final ShopHandler handler = new ShopHandler();

    private final NPC.Global npc;

    public static void init() {
        YamlConfiguration configuration = loadConfiguration();
        for (String key : configuration.getKeys(false)) {
            Location location = locationFromString(configuration.getString(key));
            new ShopNPC(key, location, false);
        }
    }

    private ShopNPC(String id, Location location, boolean isNew) {
        if (isNew) {
            YamlConfiguration configuration = loadConfiguration();
            configuration.set(id, locationToString(location));
            try {
                saveConfiguration(configuration);
            } catch (IOException e) {
                Main.getInstance().getLogger().log(Level.SEVERE, "Failed to save ShopNPC configuration", e);
            }
        }
        this.npc = NPCLib.getInstance().generateGlobalNPC(Main.getInstance(), "shop:" + id, location);
        FileConfiguration config = Main.getInstance().getConfig();
        npc.setSkin(config.getString("shop.npc.skins.1.value"), config.getString("shop.npc.skins.1.signature"));
        npc.setCollidable(false);
        npc.addCustomClickAction(handler);
        npc.createAllPlayers();
        npc.show();
    }

    public static ShopNPC create(String id, Location location) {
        return new ShopNPC(id, location, true);
    }

    private static String locationToString(Location location) {
        return String.format("%s,%s,%s,%s,%s,%s", location.getWorld().getUID(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    private static Location locationFromString(String s) {
        String[] args = s.split(",");
        return new Location(Bukkit.getWorld(UUID.fromString(args[0])), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]), Float.parseFloat(args[4]), Float.parseFloat(args[5]));
    }

    private static YamlConfiguration loadConfiguration() {
        return YamlConfiguration.loadConfiguration(new File(Main.getInstance().getDataFolder(), "shopNPC.yml"));
    }

    private static void saveConfiguration(YamlConfiguration configuration) throws IOException {
        configuration.save(new File(Main.getInstance().getDataFolder(), "shopNPC.yml"));
    }
}
