package de.slimecloud.hardsmp.util;

import com.jeff_media.morepersistentdatatypes.DataType;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;

public class InventoryStorage {
    public final static NamespacedKey key = new NamespacedKey("hard-smp", "temp-inventory");

    public static boolean saveInventory(Player player) {
        return saveInventory(player, false);
    }

    public static boolean saveInventory(Player player, boolean override) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        if(data.has(key) && !override) return false;

        data.set(key, DataType.ITEM_STACK_ARRAY, player.getInventory().getContents());
        return true;
    }

    public static void restoreInventory(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        if(!data.has(key)) return;

        player.getInventory().setContents(data.get(key, DataType.ITEM_STACK_ARRAY));
        data.remove(key);
    }
}
