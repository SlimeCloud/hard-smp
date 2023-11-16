package de.slimecloud.hardsmp.advancement.handler;

import de.cyklon.spigotutils.persistence.PersistentDataHandler;
import de.slimecloud.hardsmp.advancement.AdvancementHandler;
import de.slimecloud.hardsmp.advancement.CustomAdvancement;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FireworkAdvancement extends AdvancementHandler {

    private final static int RED = 11743532;
    private final static int ORANGE = 15435844;
    private final static int YELLOW = 14602026;
    private final static int LIME = 4312372;
    private final static int GREEN = 3887386;
    private final static int LIGHT_BLUE = 6719955;
    private final static int CYAN = 2651799;
    private final static int BLUE = 2437522;
    private final static int PURPLE = 8073150;
    private final static int MAGENTA = 12801229;
    private final static int PINK = 14188952;
    private final static int WHITE = 15790320;
    private final static int LIGHT_GRAY = 11250603;
    private final static int GRAY = 4408131;
    private final static int BLACK = 1973019;
    private final static int BROWN = 5320730;

    private final static List<Integer> COLORS = List.of(RED, ORANGE, YELLOW, LIME, GREEN, LIGHT_BLUE, CYAN, BLUE, PURPLE, MAGENTA, PINK, WHITE, LIGHT_GRAY, GRAY, BLACK, BROWN);

    private final NamespacedKey key;

    public FireworkAdvancement(Plugin plugin) {
        super(plugin, CustomAdvancement.FIREWORK);
        this.key = new NamespacedKey(plugin, "fireworks.launched");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.getAction().equals(Action.RIGHT_CLICK_AIR))
            return;
        ItemStack item = event.getItem();
        if (item == null || !item.getType().equals(Material.FIREWORK_ROCKET)) return;
        Player player = event.getPlayer();
        if (isDone(player)) return;
        FireworkMeta meta = (FireworkMeta) item.getItemMeta();
        List<FireworkEffect> effects = meta.getEffects();
        if (effects.isEmpty()) return;
        List<Color> colors = effects.get(0).getColors();
        if (colors.isEmpty()) return;
        Color color = colors.get(0);
        Set<Integer> collected = Arrays.stream(PersistentDataHandler.get(player).reviseIntArrayWithDefault(key, a -> {
            Set<Integer> set = Arrays.stream(a).boxed().collect(Collectors.toSet());
            set.add(color.asRGB());
            return set.stream().mapToInt(i -> i).toArray();
        }, new int[0])).boxed().collect(Collectors.toSet());
        if (collected.containsAll(COLORS)) unlock(player);
    }

}
