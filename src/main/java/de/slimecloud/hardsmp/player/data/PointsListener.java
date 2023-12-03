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

    public final static Map<String, Integer> ADVANCEMENTS_RARITY = new HashMap<>();

    static {
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.STONE_AGE.getKey(), 1);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.GETTING_AN_UPGRADE.getKey(), 1);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.ACQUIRE_HARDWARE.getKey(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SUIT_UP.getKey(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.HOT_STUFF.getKey(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.ISNT_IT_IRON_PICK.getKey(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.NOT_TODAY_THANK_YOU.getKey(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.ICE_BUCKET_CHALLENGE.getKey(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.DIAMONDS.getKey(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WE_NEED_TO_GO_DEEPER.getKey(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.COVER_ME_WITH_DIAMONDS.getKey(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.ENCHANTER.getKey(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.EYE_SPY.getKey(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.ZOMBIE_DOCTOR.getKey(), 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THE_END.getKey(), 4);

        ADVANCEMENTS_RARITY.put(DefaultAdvancement.RETURN_TO_SENDER.getKey(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THOSE_WERE_THE_DAYS.getKey(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.LOCAL_BREWERY.getKey(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SUBSPACE_BUBBLE.getKey(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.A_TERRIBLE_FORTRESS.getKey(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WHO_IS_CUTTING_ONIONS.getKey(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.OH_SHINY.getKey(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THIS_BOAT_HAS_LEGS.getKey(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WAR_PIGS.getKey(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.INTO_FIRE.getKey(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.NOT_QUITE_NINE_LIVES.getKey(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.BEACONATOR.getKey(), 3); //YOu only got rarity 5 for BRING_HOME_THE_BEACON
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.HIDDEN_IN_THE_DEPTHS.getKey(), 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.COUNTRY_LODE_TAKE_ME_HOME.getKey(), 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SPOOKY_SCARY_SKELETON.getKey(), 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.FEELS_LIKE_HOME.getKey(), 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.HOT_TOURIST_DESTINATIONS.getKey(), 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.COVER_ME_IN_DEBRIS.getKey(), 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.UNEASY_ALLIANCE.getKey(), 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WITHERING_HEIGHTS.getKey(), 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.BRING_HOME_THE_BEACON.getKey(), 5);

        ADVANCEMENTS_RARITY.put(DefaultAdvancement.YOU_NEED_A_MINT.getKey(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.FREE_THE_END.getKey(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.REMOTE_GETAWAY.getKey(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THE_END_AGAIN.getKey(), 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THE_CITY_AT_THE_END_OF_THE_GAME.getKey(), 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SKYS_THE_LIMIT.getKey(), 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THE_NEXT_GENERATION.getKey(), 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.GREAT_VIEW_FROM_UP_HERE.getKey(), 5);

        ADVANCEMENTS_RARITY.put(DefaultAdvancement.MONSTER_HUNTER.getKey(), 1);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SWEET_DREAMS.getKey(), 1);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.TAKE_AIM.getKey(), 1);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.IT_SPREADS.getKey(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THE_POWER_OF_BOOKS.getKey(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WHAT_A_DEAL.getKey(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.OL_BETSY.getKey(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WHOS_THE_PILLAGER_NOW.getKey(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.RESPECTING_THE_REMNANTS.getKey(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SNEAK_100.getKey(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.LIGHT_AS_A_RABBIT.getKey(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.HERO_OF_THE_VILLAGE.getKey(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.CAVES_AND_CLIFFS.getKey(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.CRAFTING_A_NEW_LOOK.getKey(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.STICKY_SITUATION.getKey(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.VOLUNTARY_EXILE.getKey(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.POSTMORTAL.getKey(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.HIRED_HELP.getKey(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.CAREFUL_RESTORATION.getKey(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.BULLSEYE.getKey(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.IS_IT_A_BIRD.getKey(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.IS_IT_A_BALLOON.getKey(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.IS_IT_A_PLANE.getKey(), 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.A_THROWAWAY_JOKE.getKey(), 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.STAR_TRADER.getKey(), 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SOUND_OF_MUSIC.getKey(), 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SNIPER_DUEL.getKey(), 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.VERY_VERY_FRIGHTENING.getKey(), 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SMITHING_WITH_STYLE.getKey(), 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SURGE_PROTECTOR.getKey(), 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.MONSTERS_HUNTED.getKey(), 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.TWO_BIRDS_ONE_ARROW.getKey(), 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.ARBALISTIC.getKey(), 5);

        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THE_PARROTS_AND_THE_BATS.getKey(), 1);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.A_SEEDY_PLACE.getKey(), 1);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.BEST_FRIENDS_FOREVER.getKey(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.GLOW_AND_BEHOLD.getKey(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.FISHY_BUSINESS.getKey(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.TACTICAL_FISHING.getKey(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WAX_ON.getKey(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WAX_OFF.getKey(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.BEE_OUR_GUEST.getKey(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.YOUVE_GOT_A_FRIEND_IN_ME.getKey(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WHATEVER_FLOATS_YOUR_GOAT.getKey(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SMELLS_INTERESTING.getKey(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.BIRTHDAY_SONG.getKey(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THE_CUTEST_PREDATOR.getKey(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.TOTAL_BEELOCATION.getKey(), 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.BUKKIT_BUKKIT.getKey(), 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WHEN_THE_SQUAD_HOPS_INTO_TOWN.getKey(), 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THE_HEALING_POWER_OF_FRIENDSHIP.getKey(), 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.A_COMPLETE_CATALOGUE.getKey(), 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.LITTLE_SNIFFS.getKey(), 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.A_BALANCED_DIET.getKey(), 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SERIOUS_DEDICATION.getKey(), 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WITH_OUR_POWERS_COMBINED.getKey(), 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.PLANTING_THE_PAST.getKey(), 5);

        ADVANCEMENTS_RARITY.put(DefaultAdvancement.A_FURIOUS_COCKTAIL.getKey(), 6);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.HOW_DID_WE_GET_HERE.getKey(), 6);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.ADVENTURING_TIME.getKey(), 6);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.TWO_BY_TWO.getKey(), 6);

        ADVANCEMENTS_RARITY.put(CustomAdvancement.GOLD3.getKey(), 5);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.FROG.getKey(), 5);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.BOAT.getKey(), 5);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.LIGHTNING.getKey(), 5);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.MUSIC.getKey(), 5);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.POTTERY.getKey(), 5);

        ADVANCEMENTS_RARITY.put(CustomAdvancement.GOLD2.getKey(), 4);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.BLOCKS.getKey(), 4);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.GARDEN.getKey(), 4);

        ADVANCEMENTS_RARITY.put(CustomAdvancement.LUMBERJACK.getKey(), 3);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.GOLD1.getKey(), 3);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.GHAST.getKey(), 3);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.DIAMOND.getKey(), 3);

        ADVANCEMENTS_RARITY.put(CustomAdvancement.TREASURE.getKey(), 2);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.FISH.getKey(), 2);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.FIREWORK.getKey(), 2);

        ADVANCEMENTS_RARITY.put(CustomAdvancement.LAVA.getKey(), 1);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.CAVE.getKey(), 1);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.BED.getKey(), 1);
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
        Integer rarity = ADVANCEMENTS_RARITY.get(type.getKey());
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
