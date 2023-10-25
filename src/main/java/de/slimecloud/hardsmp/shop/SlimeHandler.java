package de.slimecloud.hardsmp.shop;

import de.cyklon.spigotutils.item.ItemBuilder;
import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.player.EventPlayer;
import de.slimecloud.hardsmp.player.PlayerController;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class SlimeHandler implements Listener {

    private static final List<Offer> offers = new ArrayList<>();

    public static void setupOffers(FileConfiguration config) {
        ConfigurationSection section = config.getConfigurationSection("shop.offers");
        if (section==null) {
            HardSMP.getInstance().getLogger().warning("skipped registration of offers because they are not defined");
            return;
        }
        for (String key : section.getKeys(false)) {
            String item = section.getString("%s.item".formatted(key));
            if (item==null) {
                HardSMP.getInstance().getLogger().warning("cannot register offer '%s' because the item is not defined".formatted(key));
                continue;
            }
            int amount = section.getInt("%s.amount".formatted(key), 1);
            double requiredPoints = section.getDouble("%s.price.required-points".formatted(key), 0);
            String firstPriceItem = section.getString("%s.price.item".formatted(key));
            if (firstPriceItem==null) {
                HardSMP.getInstance().getLogger().warning("cannot register offer '%s' because the first price item is not defined".formatted(key));
                continue;
            }
            int firstPriceAmount = section.getInt("%s.price.amount".formatted(key), 1);

            String secondPriceItem = section.getString("%s.price.second-item".formatted(key));
            int secondPriceAmount = section.getInt("%s.price.second-amount".formatted(key), 1);

            Price price = new Price(firstPriceItem, firstPriceAmount, secondPriceItem, secondPriceAmount, requiredPoints);
            Offer offer = new Offer(item, amount, price);
            offers.add(offer);
            HardSMP.getInstance().getLogger().info("registered offer '%s'".formatted(key));
        }
    }

    private boolean isShopSlime(Entity entity) {
        String s = entity.getPersistentDataContainer().get(HardSMP.getInstance().SHOP_KEY, PersistentDataType.STRING);
        return s!=null && s.equals("shop");
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!isShopSlime(event.getDamager())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event) {
        if (!isShopSlime(event.getRightClicked())) return;
        Slime slime = (Slime) event.getRightClicked();
        slime.setJumping(true);

        new ShopGui(event.getPlayer(), offers);
    }

    @EventHandler
    public void onTrade(InventoryClickEvent event) {
        if (!event.getInventory().getType().equals(InventoryType.MERCHANT)) return;
        if (!List.of(InventoryAction.PICKUP_ONE, InventoryAction.PICKUP_ALL, InventoryAction.MOVE_TO_OTHER_INVENTORY).contains(event.getAction())) return;
        if (!List.of(ClickType.LEFT, ClickType.SHIFT_LEFT).contains(event.getClick())) return;
        if (!event.getSlotType().equals(InventoryType.SlotType.RESULT)) return;
        if (event.getSlot()!=2) return;
        EventPlayer ep = PlayerController.getPlayer(event.getWhoClicked());
        Inventory inv = event.getInventory();
        ItemStack item = inv.getItem(event.getSlot());
        Offer offer = Offer.byItem(item, offers);
        if (offer.price().requiredPoints()>ep.getPoints()) {
            event.getWhoClicked().sendMessage(ChatColor.RED + "Fehler\nFür den kauf dieses Items sind %s punkte erforderlich, du hast aber erst %s.\nBitte versuche es später erneut.".formatted(offer.price().requiredPoints(), ep.getPoints()));
            event.setCancelled(true);
            return;
        }
        item = new ItemBuilder(item).removeLore(".*benötige punkte.*").build();
        inv.setItem(event.getSlot(), new ItemBuilder(item).removeLore(item.getItemMeta().getLore().size()-1).build());
    }
}
