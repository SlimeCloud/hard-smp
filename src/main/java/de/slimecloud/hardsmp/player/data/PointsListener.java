package de.slimecloud.hardsmp.player.data;

import de.cyklon.spigotutils.advancement.AdvancementType;
import de.cyklon.spigotutils.advancement.DefaultAdvancement;
import de.slimecloud.hardsmp.advancement.CustomAdvancement;
import de.slimecloud.hardsmp.player.PlayerController;
import io.papermc.paper.event.player.PlayerTradeEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.Raid;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Cake;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.raid.RaidFinishEvent;

import java.util.HashMap;
import java.util.Map;

public class PointsListener implements Listener {

    private final static Map<AdvancementType, Integer> ADVANCEMENTS_RARITY = new HashMap<>();

    static {
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.DIAMONDS, 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.VOLUNTARY_EXILE, 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.INTO_FIRE, 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.HIDDEN_IN_THE_DEPTHS, 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.COUNTRY_LODE_TAKE_ME_HOME, 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.COVER_ME_WITH_DIAMONDS, 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SPOOKY_SCARY_SKELETON, 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.FREE_THE_END, 5);

        ADVANCEMENTS_RARITY.put(DefaultAdvancement.STONE_AGE, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.ENCHANTER, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.EYE_SPY, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THE_END, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.CRAFTING_A_NEW_LOOK, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.GETTING_AN_UPGRADE, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.GLOW_AND_BEHOLD, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THE_CITY_AT_THE_END_OF_THE_GAME, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WHAT_A_DEAL, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WE_NEED_TO_GO_DEEPER, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.ISNT_IT_IRON_PICK, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WITHERING_HEIGHTS, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.MONSTER_HUNTER, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.FISHY_BUSINESS, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.STICKY_SITUATION, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.ACQUIRE_HARDWARE, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.A_TERRIBLE_FORTRESS, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SUIT_UP, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.NOT_TODAY_THANK_YOU, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.CAREFUL_RESTORATION, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.NOT_QUITE_NINE_LIVES, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.TAKE_AIM, 4);

        ADVANCEMENTS_RARITY.put(DefaultAdvancement.BRING_HOME_THE_BEACON, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.A_THROWAWAY_JOKE, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.A_SEEDY_PLACE, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.LOCAL_BREWERY, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THOSE_WERE_THE_DAYS, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.BUKKIT_BUKKIT, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WHOS_THE_PILLAGER_NOW, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.HOT_STUFF, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THIS_BOAT_HAS_LEGS, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.BEST_FRIENDS_FOREVER, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.OL_BETSY, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.TACTICAL_FISHING, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.REMOTE_GETAWAY, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WHEN_THE_SQUAD_HOPS_INTO_TOWN, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.OH_SHINY, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.ICE_BUCKET_CHALLENGE, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SWEET_DREAMS, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.PLANTING_THE_PAST, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THE_PARROTS_AND_THE_BATS, 3);

        ADVANCEMENTS_RARITY.put(DefaultAdvancement.VERY_VERY_FRIGHTENING, 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.YOUVE_GOT_A_FRIEND_IN_ME, 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.TOTAL_BEELOCATION, 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.FEELS_LIKE_HOME, 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.CAVES_AND_CLIFFS, 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WHATEVER_FLOATS_YOUR_GOAT, 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WHO_IS_CUTTING_ONIONS, 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SURGE_PROTECTOR, 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WAR_PIGS, 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.STAR_TRADER, 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SMELLS_INTERESTING, 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THE_HEALING_POWER_OF_FRIENDSHIP, 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.RESPECTING_THE_REMNANTS, 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.BEE_OUR_GUEST, 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THE_CUTEST_PREDATOR, 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.LIGHT_AS_A_RABBIT, 2);

        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SNEAK_100, 1);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SOUND_OF_MUSIC, 1);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.IS_IT_A_BIRD, 1);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.IS_IT_A_PLANE, 1);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THE_POWER_OF_BOOKS, 1);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.BIRTHDAY_SONG, 1);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.LITTLE_SNIFFS, 1);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.IS_IT_A_BALLOON, 1);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WAX_ON, 1);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WAX_OFF, 1);


        ADVANCEMENTS_RARITY.put(CustomAdvancement.GOLD3, 5);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.FROG, 5);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.BOAT, 5);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.LIGHTNING, 5);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.MUSIC, 5);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.POTTERY, 5);

        ADVANCEMENTS_RARITY.put(CustomAdvancement.GOLD2, 4);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.BLOCKS, 4);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.GARDEN, 4);

        ADVANCEMENTS_RARITY.put(CustomAdvancement.LUMBERJACK, 3);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.GOLD1, 3);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.GHAST, 3);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.DIAMOND, 3);

        ADVANCEMENTS_RARITY.put(CustomAdvancement.TREASURE, 2);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.FISH, 2);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.FIREWORK, 2);

        ADVANCEMENTS_RARITY.put(CustomAdvancement.LAVA, 1);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.CAVE, 1);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.BED, 1);
    }

    @EventHandler
    public void onTrade(PlayerTradeEvent event) {
        addPoints(event.getPlayer(), PointCategory.VILLAGER_TRADED);
    }

    @EventHandler
    public void onRaidWin(RaidFinishEvent event) {
        if (event.getRaid().getStatus().equals(Raid.RaidStatus.VICTORY))
            event.getWinners().forEach(p -> addPoints(p, PointCategory.RAID_WIN));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        Player killer = player.getKiller();
        if (killer != null) addPoints(killer, PointCategory.PLAYER_KILL, killer.getHealth());
        addPoints(player, PointCategory.DEATH);
    }

    @EventHandler
    public void onEntityKill(EntityDeathEvent event) {
        if (event.getEntityType().equals(EntityType.PLAYER)) return;
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        if (killer != null)
            addPoints(killer, PointCategory.MOB_KILL, entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        addPoints(event.getEnchanter(), PointCategory.ENCHANT_ITEM, event.getEnchantmentHint().getRarity().getWeight());
    }

    @EventHandler
    public void onFishCaught(PlayerFishEvent event) {
        addPoints(event.getPlayer(), PointCategory.FISH_CAUGHT);
    }

    @EventHandler
    public void onEatCake(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (!(block instanceof Cake) || !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        addPoints(event.getPlayer(), PointCategory.EAT_CAKE_SLICE);
    }

    @EventHandler
    public void onBreed(EntityBreedEvent event) {
        if (event.getBreeder() instanceof Player player) addPoints(player, PointCategory.ANIMALS_BRED);
    }

    @EventHandler
    public void onAdvancementDone(PlayerAdvancementDoneEvent event) {
        AdvancementType type = AdvancementType.getAdvancementType(event.getAdvancement());
        int rarity = ADVANCEMENTS_RARITY.getOrDefault(type, 1);
        addPoints(event.getPlayer(), PointCategory.ADVANCEMENT, rarity);
    }


    private void addPoints(OfflinePlayer player, PointCategory category) {
        PlayerController.getPlayer(player).addPoints(category.getPoints());
    }

    private void addPoints(OfflinePlayer player, PointCategory category, double val) {
        PlayerController.getPlayer(player).addPoints(category.calculate(val));
    }

}
