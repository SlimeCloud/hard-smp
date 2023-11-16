package de.slimecloud.hardsmp.shop;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public record Offer(String item, int amount, Price price) {

    public static Offer byItem(ItemStack item, List<Offer> offers) {
        return offers.get(Integer.parseInt(item.getItemMeta().getLocalizedName()));
    }

}
