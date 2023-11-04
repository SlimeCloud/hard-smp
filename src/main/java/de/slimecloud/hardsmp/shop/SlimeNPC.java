package de.slimecloud.hardsmp.shop;

import de.slimecloud.hardsmp.HardSMP;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Slime;
import org.bukkit.persistence.PersistentDataType;

public class SlimeNPC {

	private final Slime slime;

	public SlimeNPC(Location location) {
		slime = location.getWorld().spawn(location, Slime.class);
		slime.setSize(2);
		slime.setWander(false);
		slime.setInvulnerable(true);
		slime.setCollidable(false);
		slime.setCustomNameVisible(true);
		slime.getPersistentDataContainer().set(HardSMP.getInstance().SHOP_KEY, PersistentDataType.STRING, "shop");
		slime.customName(Component.text("Shop"));
	}


}
