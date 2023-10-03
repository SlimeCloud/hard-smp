package de.slimecloud.hardsmp.item;

import com.destroystokyo.paper.Namespaced;
import com.google.common.collect.Multimap;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import static net.kyori.adventure.text.Component.text;

public class ItemBuilder {

    private final ItemStack stack;
    private final ItemMeta meta;


    public ItemBuilder(Material material) {
        this.stack = new ItemStack(material);
        this.meta = stack.getItemMeta();
    }

    public ItemBuilder setAmount(int amount) {
        stack.setAmount(amount);
        return this;
    }

    public ItemBuilder setType(Material material) {
        stack.setType(material);
        return this;
    }

    public ItemBuilder setAttributeModifiers(@Nullable Multimap<Attribute, AttributeModifier> map) {
        meta.setAttributeModifiers(map);
        return this;
    }

    public ItemBuilder setDestroyableKeys(Collection<Namespaced> collection) {
        meta.setDestroyableKeys(collection);
        return this;
    }

    public ItemBuilder setPlaceableKeys(Collection<Namespaced> collection) {
        meta.setPlaceableKeys(collection);
        return this;
    }


    public ItemBuilder setUnbreakable(boolean unbreakable) {
        meta.setUnbreakable(unbreakable);
        return this;
    }

    public ItemBuilder setCustomModelData(int model) {
        meta.setCustomModelData(model);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int level, boolean b) {
        meta.addEnchant(enchantment, level, b);
        return this;
    }
    public ItemBuilder addItemFlags(ItemFlag... flags) {
        meta.addItemFlags(flags);
        return this;
    }

    public ItemBuilder addAttributeModifier(Attribute attribute, AttributeModifier modifier) {
        meta.addAttributeModifier(attribute, modifier);
        return this;
    }

    public ItemBuilder setDisplayName(String displayName) {
        return setDisplayName(text(displayName));
    }

    public ItemBuilder setDisplayName(Component component) {
        meta.displayName(component);
        return this;
    }

    public ItemStack build() {
        stack.setItemMeta(meta);
        return stack;
    }


}
