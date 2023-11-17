package de.slimecloud.hardsmp.item;

import de.cyklon.spigotutils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class CustomItem {

    private static final Set<CustomItem> ITEMS = new HashSet<>();

    protected final ItemBuilder builder;
    private final int customModelData;
    private final Material material;

    protected CustomItem(Material material, int customModelData) {
        this.builder = new ItemBuilder(material);
        this.customModelData = customModelData;
        this.material = material;
    }

    protected final void add() {
        ITEMS.add(this);
    }

    protected boolean isItem(ItemStack stack) {
        return stack!=null && stack.getItemMeta().getCustomModelData() == customModelData && stack.getType()==material;
    }

    public ItemStack getItem() {
        return getItem(1);
    }

    public ItemStack getItem(int amount) {
        builder.setAmount(amount);
        builder.setCustomModelData(customModelData);
        return builder.build();
    }

    public static Collection<CustomItem> getItems() {
        return new ArrayList<>(ITEMS);
    }

}
