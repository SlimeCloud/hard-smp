package de.slimecloud.hardsmp.shop.invshop;

import de.cyklon.spigotutils.item.ItemBuilder;
import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.claim.ClaimRights;
import de.slimecloud.hardsmp.player.PlayerController;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InvShopHandler implements Listener {

    public record InvOffer(ItemBuilder item, int maxAmount, List<ItemStack> prices, double pointsRequired, int index, int blocks) {}

    private final Inventory shopinv;

    private final List<InvOffer> offers = new ArrayList<>();

    public InvShopHandler(){
        shopinv = Bukkit.createInventory(null, 9, Component.text("Bauamt"));
    }

    private boolean isClaimShop(Entity entity) {
        String s = entity.getPersistentDataContainer().get(HardSMP.getInstance().CLAIM_SHOP_KEY, PersistentDataType.STRING);
        return s != null && s.equals("claimshop");
    }

    private boolean isArenaShop(Entity entity) {
        String s = entity.getPersistentDataContainer().get(HardSMP.getInstance().ARENA_SHOP_KEY, PersistentDataType.STRING);
        return s != null && s.equals("arenashop");
    }

    public void addItemsToShop() {
        ConfigurationSection section = HardSMP.getInstance().getConfig().getConfigurationSection("claimshop.offers");
        if (section == null) {
            HardSMP.getInstance().getLogger().warning("skipped registration of offers because they are not defined");
            return;
        }

        for (String key : section.getKeys(false)) {
            String item = section.getString("%s.item".formatted(key));
            if (item == null) {
                HardSMP.getInstance().getLogger().warning("cannot register offer '%s' because the item is not defined".formatted(key));
                continue;
            }

            double requiredPoints = section.getDouble("%s.price.required-points".formatted(key), 0);
            String firstPriceItem = section.getString("%s.price.item".formatted(key));
            if (firstPriceItem == null) {
                HardSMP.getInstance().getLogger().warning("cannot register offer '%s' because the first price item is not defined".formatted(key));
                continue;
            }

            int firstPriceAmount = section.getInt("%s.price.amount".formatted(key), 1);
            int quantity = section.getInt("%s.quantity".formatted(key), 1);
            int blocks = section.getInt("%s.blocks".formatted(key), 1);
            int index = section.getInt("%s.index".formatted(key), 1);

            String secondPriceItem = section.getString("%s.price.second-item".formatted(key));
            int secondPriceAmount = section.getInt("%s.price.second-amount".formatted(key), 1);

            ItemBuilder currentItem = HardSMP.getInstance().getItemManager().getBuilder(item);
            Material mat = Material.getMaterial(firstPriceItem.toUpperCase());
            if (mat == null) {
                HardSMP.getInstance().getLogger().warning("skipped offer '" + item + "' cause item '" + firstPriceItem.toUpperCase() + "' not found");
                continue;
            }
            ItemStack firstPrice = new ItemStack(mat, firstPriceAmount);

            currentItem.addLore(List.of("", "§6Preis: ", "§b" + mat.toString().replace('_', ' ') + " " + firstPriceAmount + "x"));


            if (secondPriceItem == null) {
                currentItem.addLore(List.of("", "", ""));
                List<ItemStack> list = new ArrayList<>();
                list.add(firstPrice);
                offers.add(new InvOffer(currentItem, quantity, list, requiredPoints, index, blocks));
                continue;
            }
            Material mat2 = Material.getMaterial(secondPriceItem.toUpperCase());
            if (mat2 == null) {
                currentItem.addLore(List.of("", "", ""));
                List<ItemStack> list = new ArrayList<>();
                list.add(firstPrice);
                offers.add(new InvOffer(currentItem, quantity, list, requiredPoints, index, blocks));
                continue;
            }
            ItemStack secondPrice = new ItemStack(mat2, secondPriceAmount);

            currentItem.addLore(List.of("§b" + mat2.toString().replace('_', ' ') + " " + secondPriceAmount + "x", "", "", ""));

            List<ItemStack> list = new ArrayList<>();
            list.add(firstPrice);
            list.add(secondPrice);
            offers.add(new InvOffer(currentItem, quantity, list, requiredPoints, index, blocks));
        }
    }

    public void refreshShopItems(Player player) {
        ClaimRights claimRights = ClaimRights.load(player.getUniqueId());
        ItemStack defaultItem = HardSMP.getInstance().getItemManager().getBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ITEM_SPECIFICS)
                .setCustomModelData(3)
                .setDisplayName(" ")
                .build();

        shopinv.clear();
        shopinv.setItem(0, defaultItem);
        shopinv.setItem(1, defaultItem);
        shopinv.setItem(7, defaultItem);
        shopinv.setItem(8, defaultItem);
        for (InvOffer co : offers) {
            shopinv.addItem(co.item
                    .removeLore(Objects.requireNonNull(co.item.build().lore()).size() - 1)
                    .removeLore(Objects.requireNonNull(co.item.build().lore()).size() - 1)
                    .addLore(List.of((PlayerController.getPlayer((OfflinePlayer) player).getActualPoints() >= co.pointsRequired ? "§a" : "§c") + "Benötige Punkte: " + Math.round(co.pointsRequired)))
                    .addLore(List.of(((co.maxAmount - claimRights.getBought(co.blocks)) == 0 ? "§c" : "§e") + (co.maxAmount - claimRights.getBought(co.blocks)) + " übrig"))
                    .setAmount((co.maxAmount - claimRights.getBought(co.blocks) == 0) ? 1 : co.maxAmount - claimRights.getBought(co.blocks)).build());
        }
    }

    @EventHandler
    private void handleShopInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Villager shop)) return;
        if (isClaimShop(shop)) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            refreshShopItems(player);
            player.openInventory(shopinv);
        }
    }

    @EventHandler
    public void handleShopInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getType().equals(Material.GRAY_STAINED_GLASS_PANE) && event.getCurrentItem().getItemMeta().getCustomModelData() == 3)
            event.setCancelled(true);
        if (!event.getCurrentItem().getType().equals(Material.IRON_HOE)) return;

        for (InvOffer offer : offers) {
            if (event.getCurrentItem().getItemMeta().hasCustomModelData() && offer.index == event.getCurrentItem().getItemMeta().getCustomModelData()) {
                event.setCancelled(true);
                shopBuy(offer, player);
                refreshShopItems(player);
                break;
            }
        }
    }

    public void shopBuy(InvOffer offer, Player player) {
        ClaimRights rights = ClaimRights.load(player.getUniqueId());
        if (rights.getBought(offer.blocks) >= offer.maxAmount) {
            player.sendMessage(HardSMP.getPrefix().append(Component.text("§cDu hast schon die maximale Anzahl gekauft!")));
            return;
        }
        if (PlayerController.getPlayer((OfflinePlayer) player).getActualPoints() < offer.pointsRequired) {
            player.sendMessage(HardSMP.getPrefix().append(Component.text("§cDu hast zu wenig Punkte!")));
            return;
        }

        if (offer.prices.size() == 1) {
            if (player.getInventory().containsAtLeast(offer.prices.get(0), offer.prices.get(0).getAmount())) {
                rights.buy(offer.blocks, player);
                player.getInventory().removeItem(offer.prices.get(0));
            } else {
                player.sendMessage(HardSMP.getPrefix().append(Component.text("§cDu kannst dir das nicht leisten!")));
            }
        } else if (offer.prices.size() == 2) {
            if (player.getInventory().containsAtLeast(offer.prices.get(0), offer.prices.get(0).getAmount()) &&
                player.getInventory().containsAtLeast(offer.prices.get(1), offer.prices.get(1).getAmount())) {
                rights.buy(offer.blocks, player);
                player.getInventory().removeItem(offer.prices.get(0));
                player.getInventory().removeItem(offer.prices.get(1));
            } else {
                player.sendMessage(HardSMP.getPrefix().append(Component.text("§cDu kannst dir das nicht leisten!")));
            }
        } else {
            player.sendMessage(HardSMP.getPrefix().append(Component.text("§cFehlkonfiguration, bitte wende dich ans Team!")));
        }

    }

}
