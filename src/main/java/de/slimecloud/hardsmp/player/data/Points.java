package de.slimecloud.hardsmp.player.data;

import de.slimecloud.hardsmp.database.DataClass;
import de.slimecloud.hardsmp.database.Key;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;
import java.util.function.Supplier;

@Data
@EqualsAndHashCode(callSuper = true)
public class Points extends DataClass {

	@Key
	private final String id;

	private double points = 0;

	@Override
	public synchronized Points save() {
		super.save();
		return this;
	}

	public static Points load(String id) {
		Supplier<Points> sup = () -> new Points(id);
		return load(sup, Map.of("id", id)).orElseGet(sup);
	}
}
