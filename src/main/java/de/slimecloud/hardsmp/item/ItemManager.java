package de.slimecloud.hardsmp.item;

import de.cyklon.spigotutils.item.ItemBuilder;
import de.slimecloud.hardsmp.HardSMP;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Level;

public class ItemManager {

    private final HardSMP main;
    private final Map<String, Supplier<ItemStack>> stackMap;


    public ItemManager() {
        this.main = HardSMP.getInstance();
        this.stackMap = new HashMap<>();
    }

    public void registerItem(String id, Supplier<ItemStack> stack) {
        if (stackMap.containsKey(id)) {
            main.getLogger().warning("item \"%s\" was not registered because an item with this id already exists".formatted(id));
            main.getLogger().exiting(getClass().getName(), "registerItem");
            return;
        }
        stackMap.put(id, stack);
    }

    public void unregisterItem(String id) {
        stackMap.remove(id);
    }

    public ItemStack getStack(String id) {
        Supplier<ItemStack> sup = stackMap.get(id);
        if (sup==null) {
            HardSMP.getInstance().getLogger().warning("cannot get stack '%s' because it`s not registered".formatted(id));
            return new ItemBuilder(Material.BREAD).setDisplayName("Error, please report").build();
        }
        return sup.get();
    }

    public ItemBuilder getBuilder(String id) {
        return new ItemBuilder(getStack(id));
    }

}
