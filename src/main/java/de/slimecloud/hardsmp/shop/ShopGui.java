package de.slimecloud.hardsmp.shop;

import de.slimecloud.hardsmp.Main;
import de.slimecloud.hardsmp.item.ItemManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;

public class ShopGui {
	
	public ShopGui(Player player, List<Offer> offers) {
		ItemManager itemManager = Main.getInstance().getItemManager();
		List<MerchantRecipe> recipes = new ArrayList<>();
		int i = 0;
		for (Offer offer : offers) {
			Price price = offer.price();
			MerchantRecipe recipe = new MerchantRecipe(itemManager.getBuilder(offer.item()).addLore(List.of("", ChatColor.LIGHT_PURPLE + "Points required: " + price.requiredPoints())).setLocalizedName(String.valueOf(i++)).setAmount(offer.amount()).build(), Integer.MAX_VALUE);
			Material mat;
			if ((mat = Material.getMaterial(price.firstItem().toUpperCase()))==null) {
				Main.getInstance().getLogger().warning("skipped offer '" + offer.item() + "' cause item '" + price.firstItem().toUpperCase() + "' not found");
				continue;
			}
			recipe.addIngredient(new ItemStack(mat, price.firstAmount()));

			if (price.secondItem()!=null) {
				if ((mat = Material.getMaterial(price.secondItem().toUpperCase()))==null) {
					Main.getInstance().getLogger().warning("skipped offer '" + offer.item() + "' cause item '" + price.secondItem().toUpperCase() + "' not found");
					continue;
				}
				recipe.addIngredient(new ItemStack(mat, price.secondAmount()));
			}
			recipes.add(recipe);
		}
		Merchant merchant = Bukkit.createMerchant(Component.text("Shop"));
		merchant.setRecipes(recipes);
		player.openMerchant(merchant, true);
	}
	
}
