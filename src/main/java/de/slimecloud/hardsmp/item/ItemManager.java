package de.slimecloud.hardsmp.item;

import de.cyklon.spigotutils.item.ItemBuilder;
import de.slimecloud.hardsmp.Main;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ItemManager {

    private final Main main;
    private final Map<String, Supplier<ItemStack>> stackMap;


    public ItemManager() {
        this.main = Main.getInstance();
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
        return stackMap.get(id).get();
    }

    public ItemBuilder getBuilder(String id) {
        return new ItemBuilder(getStack(id));
    }

}
