package de.slimecloud.hardsmp.advancement.handler;

import de.cyklon.spigotutils.persistence.PersistentDataHandler;
import de.slimecloud.hardsmp.advancement.AdvancementHandler;
import de.slimecloud.hardsmp.advancement.CustomAdvancement;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GardenAdvancement extends AdvancementHandler {

	private final static List<Material> FLOWERS = List.of(Material.DANDELION, Material.POPPY, Material.BLUE_ORCHID, Material.ALLIUM, Material.AZURE_BLUET, Material.RED_TULIP, Material.ORANGE_TULIP, Material.WHITE_TULIP, Material.PINK_TULIP, Material.OXEYE_DAISY, Material.CORNFLOWER, Material.LILY_OF_THE_VALLEY, Material.WITHER_ROSE, Material.TORCHFLOWER, Material.SUNFLOWER, Material.LILAC, Material.ROSE_BUSH, Material.PEONY);

	private final NamespacedKey key;

	public GardenAdvancement(Plugin plugin) {
		super(plugin, CustomAdvancement.GARDEN);
		this.key = new NamespacedKey(plugin, "flowers.placed");
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		Material material = event.getBlock().getType();
		int index = FLOWERS.indexOf(material);
		if (index==-1) return;
		Player player = event.getPlayer();
		if (isDone(player)) return;
		Set<Integer> collected = Arrays.stream(PersistentDataHandler.get(player).reviseIntArrayWithDefault(key, a -> {
			Set<Integer> set = Arrays.stream(a).boxed().collect(Collectors.toSet());
			set.add(index);
			return set.stream().mapToInt(i->i).toArray();
		}, new int[0])).boxed().collect(Collectors.toSet());
		if (collected.size()==FLOWERS.size()) unlock(player);
	}


}
