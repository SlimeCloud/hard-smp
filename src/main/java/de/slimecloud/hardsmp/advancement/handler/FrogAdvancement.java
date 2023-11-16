package de.slimecloud.hardsmp.advancement.handler;

import de.cyklon.spigotutils.persistence.PersistentDataHandler;
import de.slimecloud.hardsmp.advancement.AdvancementHandler;
import de.slimecloud.hardsmp.advancement.CustomAdvancement;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Frog;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class FrogAdvancement extends AdvancementHandler {

    private final NamespacedKey key;

    public FrogAdvancement(Plugin plugin) {
        super(plugin, CustomAdvancement.FROG);
        this.key = new NamespacedKey(plugin, "frog.killed");
    }

    @EventHandler
    public void onEntityKill(EntityDeathEvent event) {
        if (event.getEntity() instanceof Frog frog) {
            Player killer = frog.getKiller();
            if (killer != null) {
                if (isDone(killer)) return;
                Set<Integer> collected = Arrays.stream(PersistentDataHandler.get(killer).reviseIntArrayWithDefault(key, a -> {
                    Set<Integer> set = Arrays.stream(a).boxed().collect(Collectors.toSet());
                    set.add(frog.getVariant().ordinal());
                    return set.stream().mapToInt(i -> i).toArray();
                }, new int[0])).boxed().collect(Collectors.toSet());
                if (collected.size() == Frog.Variant.values().length) unlock(killer);
            }
        }
    }
}
