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
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.STONE_AGE, 1);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.GETTING_AN_UPGRADE, 1);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.ACQUIRE_HARDWARE, 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SUIT_UP, 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.HOT_STUFF, 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.ISNT_IT_IRON_PICK, 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.NOT_TODAY_THANK_YOU, 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.ICE_BUCKET_CHALLENGE, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.DIAMONDS, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WE_NEED_TO_GO_DEEPER, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.COVER_ME_WITH_DIAMONDS, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.ENCHANTER, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.EYE_SPY, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.ZOMBIE_DOCTOR, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THE_END, 4);

        ADVANCEMENTS_RARITY.put(DefaultAdvancement.RETURN_TO_SENDER, 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THOSE_WERE_THE_DAYS, 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.LOCAL_BREWERY, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SUBSPACE_BUBBLE, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.A_TERRIBLE_FORTRESS, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WHO_IS_CUTTING_ONIONS, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.OH_SHINY, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THIS_BOAT_HAS_LEGS, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WAR_PIGS, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.INTO_FIRE, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.NOT_QUITE_NINE_LIVES, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.BEACONATOR, 3); //YOu only got rarity 5 for BRING_HOME_THE_BEACON
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.HIDDEN_IN_THE_DEPTHS, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.COUNTRY_LODE_TAKE_ME_HOME, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SPOOKY_SCARY_SKELETON, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.FEELS_LIKE_HOME, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.HOT_TOURIST_DESTINATIONS, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.COVER_ME_IN_DEBRIS, 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.UNEASY_ALLIANCE, 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WITHERING_HEIGHTS, 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.BRING_HOME_THE_BEACON, 5);

        ADVANCEMENTS_RARITY.put(DefaultAdvancement.YOU_NEED_A_MINT, 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.FREE_THE_END, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.REMOTE_GETAWAY, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THE_END_AGAIN, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THE_CITY_AT_THE_END_OF_THE_GAME, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SKYS_THE_LIMIT, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THE_NEXT_GENERATION, 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.GREAT_VIEW_FROM_UP_HERE, 5);

        ADVANCEMENTS_RARITY.put(DefaultAdvancement.MONSTER_HUNTER, 1);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SWEET_DREAMS, 1);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.TAKE_AIM, 1);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.IT_SPREADS, 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THE_POWER_OF_BOOKS, 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WHAT_A_DEAL, 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.OL_BETSY, 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WHOS_THE_PILLAGER_NOW, 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.RESPECTING_THE_REMNANTS, 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SNEAK_100, 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.LIGHT_AS_A_RABBIT, 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.HERO_OF_THE_VILLAGE, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.CAVES_AND_CLIFFS, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.CRAFTING_A_NEW_LOOK, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.STICKY_SITUATION, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.VOLUNTARY_EXILE, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.POSTMORTAL, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.HIRED_HELP, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.CAREFUL_RESTORATION, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.BULLSEYE, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.IS_IT_A_BIRD, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.IS_IT_A_BALLOON, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.IS_IT_A_PLANE, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.A_THROWAWAY_JOKE, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.STAR_TRADER, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SOUND_OF_MUSIC, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SNIPER_DUEL, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.VERY_VERY_FRIGHTENING, 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SMITHING_WITH_STYLE, 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SURGE_PROTECTOR, 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.MONSTERS_HUNTED, 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.TWO_BIRDS_ONE_ARROW, 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.ARBALISTIC, 5);

        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THE_PARROTS_AND_THE_BATS, 1);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.A_SEEDY_PLACE, 1);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.BEST_FRIENDS_FOREVER, 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.GLOW_AND_BEHOLD, 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.FISHY_BUSINESS, 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.TACTICAL_FISHING, 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WAX_ON, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WAX_OFF, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.BEE_OUR_GUEST, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.YOUVE_GOT_A_FRIEND_IN_ME, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WHATEVER_FLOATS_YOUR_GOAT, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SMELLS_INTERESTING, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.BIRTHDAY_SONG, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THE_CUTEST_PREDATOR, 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.TOTAL_BEELOCATION, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.BUKKIT_BUKKIT, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WHEN_THE_SQUAD_HOPS_INTO_TOWN, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THE_HEALING_POWER_OF_FRIENDSHIP, 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.A_COMPLETE_CATALOGUE, 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.LITTLE_SNIFFS, 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.A_BALANCED_DIET, 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SERIOUS_DEDICATION, 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WITH_OUR_POWERS_COMBINED, 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.PLANTING_THE_PAST, 5);

        ADVANCEMENTS_RARITY.put(DefaultAdvancement.A_FURIOUS_COCKTAIL, 6);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.HOW_DID_WE_GET_HERE, 6);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.ADVENTURING_TIME, 6);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.TWO_BY_TWO, 6);

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
        Integer rarity = ADVANCEMENTS_RARITY.get(type);
        if(rarity == null) return;

        addPoints(event.getPlayer(), PointCategory.ADVANCEMENT, rarity);
    }


    private void addPoints(OfflinePlayer player, PointCategory category) {
        PlayerController.getPlayer(player).addPoints(category.getPoints());
    }

    private void addPoints(OfflinePlayer player, PointCategory category, double val) {
        PlayerController.getPlayer(player).addPoints(category.calculate(val));
    }

}
