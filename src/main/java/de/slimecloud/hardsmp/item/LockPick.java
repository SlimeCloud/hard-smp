package de.slimecloud.hardsmp.item;

import de.cyklon.spigotutils.adventure.Formatter;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;

public class LockPick extends CustomItem implements Listener {

	private final ChestKey chestKey;
	private final double probability;

	public LockPick(ChestKey chestKey) {
		super("lock-pick", Material.IRON_HOE, 1);
		builder.setDisplayName("Dietrich")
				.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		this.chestKey = chestKey;
		this.probability = chestKey.getPlugin().getConfig().getDouble("chest-key.lockpick.probability", 2);
		add();
	}


	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction().equals(Action.LEFT_CLICK_BLOCK) && isItem(event.getItem())) event.setCancelled(true);
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			Block clickedBlock = event.getClickedBlock();
			if (clickedBlock != null && chestKey.getLOCKABLE().contains(clickedBlock.getType())) {
				ItemStack item = event.getItem();
				Player player = event.getPlayer();
				if (isItem(item)) {
					if (clickedBlock.getState() instanceof Container container) {
						if (chestKey.isContainerLocked(container) && !chestKey.isCracked(container)) {
							if (player.isSneaking()) {
								tryCrack(player, clickedBlock, item);
								event.setCancelled(true);
							}
						}
					}
					if (clickedBlock.getState() instanceof Container container && chestKey.isContainerLocked(container) && !chestKey.isCracked(container) && !player.isSneaking()) event.setCancelled(true);

				}
			} else if (isItem(event.getItem())) event.setCancelled(true);
		}
	}

	private void tryCrack(Player player, Block block, ItemStack lockpick) {
		if (random()) {
			chestKey.crack(block);
			player.sendActionBar(Formatter.parseText(chestKey.getPlugin().getConfig().getString("chest-key.success.crack", "§2Geknackt")));
			player.sendMessage(Formatter.parseText(chestKey.getPlugin().getConfig().getString("chest-key.success.crack-msg", "§2Gecknackt")));
			player.getWorld().playSound(block.getLocation(), Sound.ITEM_SHIELD_BREAK, 100, 1.2f);
		} else player.getWorld().playSound(block.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 100, 1.6f);
		PlayerInventory inv = player.getInventory();
		boolean isOffhand = !inv.getItemInMainHand().isSimilar(lockpick);
		Damageable meta = (Damageable) lockpick.getItemMeta();
		meta.setDamage(meta.getDamage()+1);
		lockpick.setItemMeta(meta);
		if (meta.getDamage()>=lockpick.getType().getMaxDurability()) {
			inv.clear(isOffhand ? 40 : inv.getHeldItemSlot());
			player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
		}
	}


	private boolean random() {
		return Math.random()*100<probability;
	}
}
