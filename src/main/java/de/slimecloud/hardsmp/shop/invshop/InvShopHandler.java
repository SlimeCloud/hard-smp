package de.slimecloud.hardsmp.shop.invshop;

import de.cyklon.spigotutils.item.ItemBuilder;
import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.claim.ClaimRights;
import de.slimecloud.hardsmp.player.PlayerController;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Warden;
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

    public record ArenaOffer(ItemBuilder item, List<ItemStack> prices, double pointsRequired) {}

    public record ClaimOffer(ItemBuilder item, int maxAmount, List<ItemStack> prices, double pointsRequired, int index, int blocks) {}

    private final Inventory claimshopinv;
    private final Inventory arenashopinv;

    private final List<ClaimOffer> claimoffers = new ArrayList<>();
    private final List<ArenaOffer> arenaoffers = new ArrayList<>();

    public InvShopHandler(){
        claimshopinv = Bukkit.createInventory(null, 9, Component.text("Bauamt"));
        arenashopinv = Bukkit.createInventory(null, 9, Component.text("Arena-Shop"));
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
            HardSMP.getInstance().getLogger().warning("skipped registration of offers for claimshop because they are not defined");
        } else {
            for (String key : section.getKeys(false)) {
                registerClaimItem(section, key);
            }
        }

        section = HardSMP.getInstance().getConfig().getConfigurationSection("arenashop.offers");
        if (section == null) {
            HardSMP.getInstance().getLogger().warning("skipped registration of offers for arenashop because they are not defined");
        } else {
            for (String key : section.getKeys(false)) {
                registerInvItem(section, key);
            }
        }
    }

    private void registerClaimItem(ConfigurationSection section, String key) {
        String item = section.getString("%s.item".formatted(key));
        if (item == null) {
            HardSMP.getInstance().getLogger().warning("cannot register offer '%s' because the item is not defined".formatted(key));
            return;
        }

        double requiredPoints = section.getDouble("%s.price.required-points".formatted(key), 0);
        String firstPriceItem = section.getString("%s.price.item".formatted(key));
        if (firstPriceItem == null) {
            HardSMP.getInstance().getLogger().warning("cannot register offer '%s' because the first price item is not defined".formatted(key));
            return;
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
            return;
        }
        ItemStack firstPrice = new ItemStack(mat, firstPriceAmount);

        currentItem.addLore(List.of("", "§6Preis: ", "§b" + mat.toString().replace('_', ' ') + " " + firstPriceAmount + "x"));


        if (secondPriceItem == null) {
            currentItem.addLore(List.of("", "", ""));
            List<ItemStack> list = new ArrayList<>();
            list.add(firstPrice);
            claimoffers.add(new ClaimOffer(currentItem, quantity, list, requiredPoints, index, blocks));
            return;
        }
        Material mat2 = Material.getMaterial(secondPriceItem.toUpperCase());
        if (mat2 == null) {
            currentItem.addLore(List.of("", "", ""));
            List<ItemStack> list = new ArrayList<>();
            list.add(firstPrice);
            claimoffers.add(new ClaimOffer(currentItem, quantity, list, requiredPoints, index, blocks));
            return;
        }
        ItemStack secondPrice = new ItemStack(mat2, secondPriceAmount);

        currentItem.addLore(List.of("§b" + mat2.toString().replace('_', ' ') + " " + secondPriceAmount + "x", "", "", ""));

        List<ItemStack> list = new ArrayList<>();
        list.add(firstPrice);
        list.add(secondPrice);
        claimoffers.add(new ClaimOffer(currentItem, quantity, list, requiredPoints, index, blocks));
    }

    @SneakyThrows
    private void registerInvItem(ConfigurationSection section, String key) {
        String item = section.getString("%s.item".formatted(key));
        if (item == null) {
            HardSMP.getInstance().getLogger().warning("cannot register offer '%s' because the item is not defined".formatted(key));
            return;
        }

        double requiredPoints = section.getDouble("%s.price.required-points".formatted(key), 0);
        String firstPriceItem = section.getString("%s.price.item".formatted(key));
        if (firstPriceItem == null) {
            HardSMP.getInstance().getLogger().warning("cannot register offer '%s' because the first price item is not defined".formatted(key));
            return;
        }

        int firstPriceAmount = section.getInt("%s.price.amount".formatted(key), 1);

        String secondPriceItem = section.getString("%s.price.second-item".formatted(key));
        int secondPriceAmount = section.getInt("%s.price.second-amount".formatted(key), 1);

        ItemBuilder currentItem = HardSMP.getInstance().getItemManager().getBuilder(item);
        Material mat = Material.getMaterial(firstPriceItem.toUpperCase());
        if (mat == null) {
            HardSMP.getInstance().getLogger().warning("skipped offer '" + item + "' cause item '" + firstPriceItem.toUpperCase() + "' not found");
            return;
        }
        ItemStack firstPrice = new ItemStack(mat, firstPriceAmount);

        currentItem.addLore(List.of("", "§6Preis: ", "§b" + mat.toString().replace('_', ' ') + " " + firstPriceAmount + "x"));


        if (secondPriceItem == null) {
            currentItem.addLore(List.of("", "", ""));
            List<ItemStack> list = new ArrayList<>();
            list.add(firstPrice);
            arenaoffers.add(new ArenaOffer(currentItem, list, requiredPoints));
            return;
        }
        Material mat2 = Material.getMaterial(secondPriceItem.toUpperCase());
        if (mat2 == null) {
            currentItem.addLore(List.of("", "", ""));
            List<ItemStack> list = new ArrayList<>();
            list.add(firstPrice);
            arenaoffers.add(new ArenaOffer(currentItem, list, requiredPoints));
            return;
        }
        ItemStack secondPrice = new ItemStack(mat2, secondPriceAmount);

        currentItem.addLore(List.of("§b" + mat2.toString().replace('_', ' ') + " " + secondPriceAmount + "x", "", "", ""));

        List<ItemStack> list = new ArrayList<>();
        list.add(firstPrice);
        list.add(secondPrice);
        arenaoffers.add(new ArenaOffer(currentItem, list, requiredPoints));
    }

    public void refreshClaimShopItems(Player player) {
        ClaimRights claimRights = ClaimRights.load(player.getUniqueId());
        ItemStack defaultItem = HardSMP.getInstance().getItemManager().getBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ITEM_SPECIFICS)
                .setCustomModelData(3)
                .setDisplayName(" ")
                .build();

        claimshopinv.clear();
        claimshopinv.setItem(0, defaultItem);
        claimshopinv.setItem(1, defaultItem);
        claimshopinv.setItem(7, defaultItem);
        claimshopinv.setItem(8, defaultItem);
        for (ClaimOffer co : claimoffers) {
            claimshopinv.addItem(co.item
                    .removeLore(Objects.requireNonNull(co.item.build().lore()).size() - 1)
                    .removeLore(Objects.requireNonNull(co.item.build().lore()).size() - 1)
                    .addLore(List.of((PlayerController.getPlayer((OfflinePlayer) player).getActualPoints() >= co.pointsRequired ? "§a" : "§c") + "Benötige Punkte: " + Math.round(co.pointsRequired)))
                    .addLore(List.of(((co.maxAmount - claimRights.getBought(co.blocks)) == 0 ? "§c" : "§e") + (co.maxAmount - claimRights.getBought(co.blocks)) + " übrig"))
                    .setAmount((co.maxAmount - claimRights.getBought(co.blocks) == 0) ? 1 : co.maxAmount - claimRights.getBought(co.blocks)).build());
        }
    }

    public void refreshArenaShopItems(Player player) {
        ItemStack defaultItem = HardSMP.getInstance().getItemManager().getBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ITEM_SPECIFICS)
                .setCustomModelData(3)
                .setDisplayName(" ")
                .build();

        arenashopinv.clear();
        arenashopinv.setItem(0, defaultItem);
        arenashopinv.setItem(4, defaultItem);
        arenashopinv.setItem(8, defaultItem);
        for (ArenaOffer io : arenaoffers) {
            claimshopinv.addItem(io.item
                    .removeLore(Objects.requireNonNull(io.item.build().lore()).size() - 1)
                    .addLore(List.of((PlayerController.getPlayer((OfflinePlayer) player).getActualPoints() >= io.pointsRequired ? "§a" : "§c") + "Benötige Punkte: " + Math.round(io.pointsRequired)))
                    .build());
        }
    }

    @EventHandler
    private void handleShopInteract(PlayerInteractEntityEvent event) {
        if (!((event.getRightClicked() instanceof Villager) || (event.getRightClicked() instanceof Warden))) return;
        Entity shop = event.getRightClicked();
        event.setCancelled(true);
        Player player = event.getPlayer();

        if (isClaimShop(shop)) {
            refreshClaimShopItems(player);
            player.openInventory(claimshopinv);
        } else if (isArenaShop(shop)) {
            refreshArenaShopItems(player);
            player.openInventory(arenashopinv);
        }
    }

    @EventHandler
    public void handleShopInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getType().equals(Material.GRAY_STAINED_GLASS_PANE) && event.getCurrentItem().getItemMeta().getCustomModelData() == 3) {
            event.setCancelled(true);
            return;
        }

        if (event.getInventory().equals(claimshopinv))
            for (ClaimOffer offer : claimoffers) {
                if (event.getCurrentItem().getItemMeta().hasCustomModelData() && offer.index == event.getCurrentItem().getItemMeta().getCustomModelData()) {
                    event.setCancelled(true);
                    shopBuy(offer, player);
                    refreshClaimShopItems(player);
                    break;
                }
            }
        else if (event.getInventory().equals(arenashopinv)) {
            for (ArenaOffer offer : arenaoffers) {
                if (event.getCurrentItem().getType() == offer.item.build().getType()) {
                    event.setCancelled(true);
                    shopBuy(offer, player);
                    refreshArenaShopItems(player);
                    break;
                }
            }
        }
    }

    public void shopBuy(ClaimOffer offer, Player player) {
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

    public void shopBuy(ArenaOffer offer, Player player) {

        if (PlayerController.getPlayer((OfflinePlayer) player).getActualPoints() < offer.pointsRequired) {
            player.sendMessage(HardSMP.getPrefix().append(Component.text("§cDu hast zu wenig Punkte!")));
            return;
        }

        if (offer.prices.size() == 1) {
            if (player.getInventory().containsAtLeast(offer.prices.get(0), offer.prices.get(0).getAmount())) {
                ArenaShopSpawner.buy(player, offer.item.build().getType(), offer.item.build().getAmount());
                player.getInventory().removeItem(offer.prices.get(0));
            } else {
                player.sendMessage(HardSMP.getPrefix().append(Component.text("§cDu kannst dir das nicht leisten!")));
            }
        } else if (offer.prices.size() == 2) {
            if (player.getInventory().containsAtLeast(offer.prices.get(0), offer.prices.get(0).getAmount()) &&
                    player.getInventory().containsAtLeast(offer.prices.get(1), offer.prices.get(1).getAmount())) {
                ArenaShopSpawner.buy(player, offer.item.build().getType(), offer.item.build().getAmount());
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
