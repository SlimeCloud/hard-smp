package de.slimecloud.hardsmp.advancement.handler;

import de.cyklon.spigotutils.persistence.PersistentDataHandler;
import de.slimecloud.hardsmp.advancement.AdvancementHandler;
import de.slimecloud.hardsmp.advancement.CustomAdvancement;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PotteryAdvancement extends AdvancementHandler {

	private final static List<Material> SHERDS;

	static {
		SHERDS = new ArrayList<>();
		for (Material value : Material.values()) if (value.name().contains("POTTERY_SHERD")) SHERDS.add(value);
	}

	private final NamespacedKey key;

	public PotteryAdvancement(Plugin plugin) {
		super(plugin, CustomAdvancement.POTTERY);
		this.key = new NamespacedKey(plugin, "sherds.found");
	}

	private void check(Player player, ItemStack stack) {
		Material material = stack.getType();
		int index = SHERDS.indexOf(material);
		if (index==-1) return;
		if (isDone(player)) return;
		Set<Integer> collected = Arrays.stream(PersistentDataHandler.get(player).reviseIntArrayWithDefault(key, a -> {
			Set<Integer> set = Arrays.stream(a).boxed().collect(Collectors.toSet());
			set.add(index);
			return set.stream().mapToInt(i->i).toArray();
		}, new int[0])).boxed().collect(Collectors.toSet());
		if (collected.size()==SHERDS.size()) unlock(player);
	}

	@EventHandler
	public void onPickUpItem(EntityPickupItemEvent event) {
		if (event.getEntity() instanceof Player player) check(player, event.getItem().getItemStack());
	}

	@EventHandler
	public void onInventory(InventoryMoveItemEvent event) {
		if (event.getDestination() instanceof PlayerInventory inv) if (inv.getHolder() instanceof Player player) check(player, event.getItem());
	}

}
