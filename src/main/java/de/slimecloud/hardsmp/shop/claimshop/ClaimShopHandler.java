package de.slimecloud.hardsmp.shop.claimshop;

import de.cyklon.spigotutils.item.ItemBuilder;
import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.claim.ClaimRights;
import de.slimecloud.hardsmp.item.PlotBuyer;
import de.slimecloud.hardsmp.player.PlayerController;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class ClaimShopHandler implements Listener {

    public record ClaimOffer(ItemBuilder item, int amount, int maxAmount, List<ItemStack> prices, double pointsRequired, int index, int blocks) {

    }

    private Inventory shopinv;

    private List<ClaimOffer> offers = new ArrayList<>();

    public ClaimShopHandler(){
        shopinv = Bukkit.createInventory(null, 9, Component.text("Bauamt"));
    }

    private boolean isClaimShop(Entity entity) {
        String s = entity.getPersistentDataContainer().get(HardSMP.getInstance().CLAIM_SHOP_KEY, PersistentDataType.STRING);
        return s != null && s.equals("claimshop");
    }

    public void addItemsToShop(Player player) {
        ConfigurationSection section = HardSMP.getInstance().getConfig().getConfigurationSection("claimshop.offers");
        if (section == null) {
            HardSMP.getInstance().getLogger().warning("skipped registration of offers because they are not defined");
            return;
        }

        shopinv.clear();

        ClaimRights claimRights = ClaimRights.load(player.getUniqueId());

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

            ItemBuilder currentItem = HardSMP.getInstance().getItemManager().getBuilder(item).addLore(
                    List.of("", (PlayerController.getPlayer((OfflinePlayer) player).getActualPoints() >= requiredPoints ? "§a" : "§c") + "Benötige Punkte: " + Math.round(requiredPoints))
            ).setAmount(1);
            Material mat = Material.getMaterial(firstPriceItem.toUpperCase());
            if (mat == null) {
                HardSMP.getInstance().getLogger().warning("skipped offer '" + item + "' cause item '" + firstPriceItem.toUpperCase() + "' not found");
                continue;
            }
            ItemStack firstPrice = new ItemStack(mat, firstPriceAmount);


            if (secondPriceItem == null) {
                List<ItemStack> list = new ArrayList<>();
                list.add(firstPrice);
                offers.add(new ClaimOffer(currentItem, quantity - claimRights.getBought(blocks), quantity, list, requiredPoints, index, blocks));
                continue;
            }
            Material mat2 = Material.getMaterial(secondPriceItem.toUpperCase());
            if (mat2 == null) {
                List<ItemStack> list = new ArrayList<>();
                list.add(firstPrice);
                offers.add(new ClaimOffer(currentItem, quantity - claimRights.getBought(blocks), quantity, list, requiredPoints, index, blocks));
                continue;
            }
            ItemStack secondPrice = new ItemStack(mat2, secondPriceAmount);

            List<ItemStack> list = new ArrayList<>();
            list.add(firstPrice);
            list.add(secondPrice);
            offers.add(new ClaimOffer(currentItem, quantity - claimRights.getBought(blocks), quantity, list, requiredPoints, index, blocks));
        }

        for (ClaimOffer co : offers) {
            addItem(co, offers.indexOf(co));
        }

    }

    private void addItem(ClaimOffer offer, int slot) {
        if (offer.prices.size() == 1)
            shopinv.addItem(offer.item.addLore(List.of("", "§6Preis: ",
                    offer.prices.get(0).getType() + " " + offer.prices.get(0).getAmount() + "x")).setAmount(offer.amount).build());
        else if (offer.prices.size() == 2) {
            shopinv.addItem(offer.item.addLore(List.of("", "§6Preis: ",
                    offer.prices.get(0).getType() + " " + offer.prices.get(0).getAmount() + "x",
                    offer.prices.get(1).getType() + " " + offer.prices.get(1).getAmount() + "x")).setAmount(offer.amount).build());
        }
    }

    @EventHandler
    private void handleShopInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Villager)) return;
        Villager shop = (Villager) event.getRightClicked();
        if (isClaimShop(shop)) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            addItemsToShop(player);
            player.openInventory(shopinv);
        }
    }

    @EventHandler
    public void handleShopInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        if (event.getCurrentItem() == null) return;
        if (!event.getCurrentItem().getType().equals(Material.HEAVY_WEIGHTED_PRESSURE_PLATE)) return;

        for (ClaimOffer offer : offers) {
            if (offer.index == event.getCurrentItem().getItemMeta().getCustomModelData()) {
                shopBuy(offer, player);
                break;
            }
        }
    }

    public void shopBuy(ClaimOffer offer, Player player) {
        /*Thread t = new Thread(() -> {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });*/
        ClaimRights rights = ClaimRights.load(player.getUniqueId());
        if (rights.getBought(offer.blocks) >= offer.maxAmount) {
            player.sendMessage(HardSMP.getPrefix().append(Component.text("§Du hast schon die maximale Anzahl gekauft!")));
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
