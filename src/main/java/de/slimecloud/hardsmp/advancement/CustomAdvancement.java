package de.slimecloud.hardsmp.advancement;

import de.cyklon.spigotutils.advancement.AdvancementType;
import org.bukkit.NamespacedKey;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomAdvancement {

	private static final List<AdvancementType> ADVANCEMENTS;

	public static AdvancementType BED;
	public static AdvancementType BLOCKS;
	public static AdvancementType BOAT;
	public static AdvancementType CAVE;
	public static AdvancementType DIAMOND;
	public static AdvancementType FIREWORK;
	public static AdvancementType FISH;
	public static AdvancementType FROG;
	public static AdvancementType GARDEN;
	public static AdvancementType GHAST;
	public static AdvancementType GOLD1;
	public static AdvancementType GOLD2;
	public static AdvancementType GOLD3;
	public static AdvancementType LAVA;
	public static AdvancementType LIGHTNING;
	public static AdvancementType LUMBERJACK;
	public static AdvancementType MUSIC;
	public static AdvancementType POTTERY;
	public static AdvancementType ROOT;
	public static AdvancementType TREASURE;

	public static Collection<AdvancementType> getAdvancements() {
		return ADVANCEMENTS;
	}


	static {
		ADVANCEMENTS = new ArrayList<>();
		for (Field field : CustomAdvancement.class.getDeclaredFields()) {
			int modifiers = field.getModifiers();
			if (Modifier.isStatic(modifiers) && AdvancementType.class.isAssignableFrom(field.getType())) {
				NamespacedKey key = new NamespacedKey("hard-smp", "hard-smp/" + field.getName().toLowerCase());
				AdvancementType type = AdvancementType.getAdvancementType(key);
				ADVANCEMENTS.add(type);
				field.setAccessible(true);
				try {
					field.set(null, type);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
