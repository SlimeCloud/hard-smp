package de.slimecloud.hardsmp.player.data;

import de.cyklon.spigotutils.advancement.DefaultAdvancement;
import de.slimecloud.hardsmp.advancement.CustomAdvancement;
import de.slimecloud.hardsmp.player.PlayerController;
import io.papermc.paper.event.player.PlayerTradeEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.Raid;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Cake;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityBreedEvent;
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
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.STONE_AGE.getNamespacedKey().asString(), 1);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.GETTING_AN_UPGRADE.getNamespacedKey().asString(), 1);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.ACQUIRE_HARDWARE.getNamespacedKey().asString(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SUIT_UP.getNamespacedKey().asString(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.HOT_STUFF.getNamespacedKey().asString(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.ISNT_IT_IRON_PICK.getNamespacedKey().asString(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.NOT_TODAY_THANK_YOU.getNamespacedKey().asString(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.ICE_BUCKET_CHALLENGE.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.DIAMONDS.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WE_NEED_TO_GO_DEEPER.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.COVER_ME_WITH_DIAMONDS.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.ENCHANTER.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.EYE_SPY.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.ZOMBIE_DOCTOR.getNamespacedKey().asString(), 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THE_END.getNamespacedKey().asString(), 4);

        ADVANCEMENTS_RARITY.put(DefaultAdvancement.RETURN_TO_SENDER.getNamespacedKey().asString(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THOSE_WERE_THE_DAYS.getNamespacedKey().asString(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.LOCAL_BREWERY.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SUBSPACE_BUBBLE.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.A_TERRIBLE_FORTRESS.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WHO_IS_CUTTING_ONIONS.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.OH_SHINY.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THIS_BOAT_HAS_LEGS.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WAR_PIGS.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.INTO_FIRE.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.NOT_QUITE_NINE_LIVES.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.BEACONATOR.getNamespacedKey().asString(), 3); //YOu only got rarity 5 for BRING_HOME_THE_BEACON
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.HIDDEN_IN_THE_DEPTHS.getNamespacedKey().asString(), 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.COUNTRY_LODE_TAKE_ME_HOME.getNamespacedKey().asString(), 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SPOOKY_SCARY_SKELETON.getNamespacedKey().asString(), 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.FEELS_LIKE_HOME.getNamespacedKey().asString(), 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.HOT_TOURIST_DESTINATIONS.getNamespacedKey().asString(), 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.COVER_ME_IN_DEBRIS.getNamespacedKey().asString(), 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.UNEASY_ALLIANCE.getNamespacedKey().asString(), 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WITHERING_HEIGHTS.getNamespacedKey().asString(), 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.BRING_HOME_THE_BEACON.getNamespacedKey().asString(), 5);

        ADVANCEMENTS_RARITY.put(DefaultAdvancement.YOU_NEED_A_MINT.getNamespacedKey().asString(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.FREE_THE_END.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.REMOTE_GETAWAY.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THE_END_AGAIN.getNamespacedKey().asString(), 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THE_CITY_AT_THE_END_OF_THE_GAME.getNamespacedKey().asString(), 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SKYS_THE_LIMIT.getNamespacedKey().asString(), 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THE_NEXT_GENERATION.getNamespacedKey().asString(), 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.GREAT_VIEW_FROM_UP_HERE.getNamespacedKey().asString(), 5);

        ADVANCEMENTS_RARITY.put(DefaultAdvancement.MONSTER_HUNTER.getNamespacedKey().asString(), 1);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SWEET_DREAMS.getNamespacedKey().asString(), 1);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.TAKE_AIM.getNamespacedKey().asString(), 1);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.IT_SPREADS.getNamespacedKey().asString(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THE_POWER_OF_BOOKS.getNamespacedKey().asString(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WHAT_A_DEAL.getNamespacedKey().asString(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.OL_BETSY.getNamespacedKey().asString(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WHOS_THE_PILLAGER_NOW.getNamespacedKey().asString(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.RESPECTING_THE_REMNANTS.getNamespacedKey().asString(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SNEAK_100.getNamespacedKey().asString(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.LIGHT_AS_A_RABBIT.getNamespacedKey().asString(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.HERO_OF_THE_VILLAGE.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.CAVES_AND_CLIFFS.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.CRAFTING_A_NEW_LOOK.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.STICKY_SITUATION.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.VOLUNTARY_EXILE.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.POSTMORTAL.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.HIRED_HELP.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.CAREFUL_RESTORATION.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.BULLSEYE.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.IS_IT_A_BIRD.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.IS_IT_A_BALLOON.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.IS_IT_A_PLANE.getNamespacedKey().asString(), 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.A_THROWAWAY_JOKE.getNamespacedKey().asString(), 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.STAR_TRADER.getNamespacedKey().asString(), 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SOUND_OF_MUSIC.getNamespacedKey().asString(), 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SNIPER_DUEL.getNamespacedKey().asString(), 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.VERY_VERY_FRIGHTENING.getNamespacedKey().asString(), 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SMITHING_WITH_STYLE.getNamespacedKey().asString(), 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SURGE_PROTECTOR.getNamespacedKey().asString(), 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.MONSTERS_HUNTED.getNamespacedKey().asString(), 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.TWO_BIRDS_ONE_ARROW.getNamespacedKey().asString(), 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.ARBALISTIC.getNamespacedKey().asString(), 5);

        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THE_PARROTS_AND_THE_BATS.getNamespacedKey().asString(), 1);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.A_SEEDY_PLACE.getNamespacedKey().asString(), 1);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.BEST_FRIENDS_FOREVER.getNamespacedKey().asString(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.GLOW_AND_BEHOLD.getNamespacedKey().asString(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.FISHY_BUSINESS.getNamespacedKey().asString(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.TACTICAL_FISHING.getNamespacedKey().asString(), 2);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WAX_ON.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WAX_OFF.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.BEE_OUR_GUEST.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.YOUVE_GOT_A_FRIEND_IN_ME.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WHATEVER_FLOATS_YOUR_GOAT.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SMELLS_INTERESTING.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.BIRTHDAY_SONG.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THE_CUTEST_PREDATOR.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.TOTAL_BEELOCATION.getNamespacedKey().asString(), 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.BUKKIT_BUKKIT.getNamespacedKey().asString(), 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WHEN_THE_SQUAD_HOPS_INTO_TOWN.getNamespacedKey().asString(), 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.THE_HEALING_POWER_OF_FRIENDSHIP.getNamespacedKey().asString(), 4);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.A_COMPLETE_CATALOGUE.getNamespacedKey().asString(), 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.LITTLE_SNIFFS.getNamespacedKey().asString(), 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.A_BALANCED_DIET.getNamespacedKey().asString(), 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.SERIOUS_DEDICATION.getNamespacedKey().asString(), 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.WITH_OUR_POWERS_COMBINED.getNamespacedKey().asString(), 5);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.PLANTING_THE_PAST.getNamespacedKey().asString(), 5);

        ADVANCEMENTS_RARITY.put(DefaultAdvancement.A_FURIOUS_COCKTAIL.getNamespacedKey().asString(), 6);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.HOW_DID_WE_GET_HERE.getNamespacedKey().asString(), 6);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.ADVENTURING_TIME.getNamespacedKey().asString(), 6);
        ADVANCEMENTS_RARITY.put(DefaultAdvancement.TWO_BY_TWO.getNamespacedKey().asString(), 6);

        ADVANCEMENTS_RARITY.put(CustomAdvancement.GOLD3.getNamespacedKey().asString(), 5);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.FROG.getNamespacedKey().asString(), 5);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.BOAT.getNamespacedKey().asString(), 5);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.LIGHTNING.getNamespacedKey().asString(), 5);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.MUSIC.getNamespacedKey().asString(), 5);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.POTTERY.getNamespacedKey().asString(), 5);

        ADVANCEMENTS_RARITY.put(CustomAdvancement.GOLD2.getNamespacedKey().asString(), 4);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.BLOCKS.getNamespacedKey().asString(), 4);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.GARDEN.getNamespacedKey().asString(), 4);

        ADVANCEMENTS_RARITY.put(CustomAdvancement.LUMBERJACK.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.GOLD1.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.GHAST.getNamespacedKey().asString(), 3);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.DIAMOND.getNamespacedKey().asString(), 3);

        ADVANCEMENTS_RARITY.put(CustomAdvancement.LAVA.getNamespacedKey().asString(), 2);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.TREASURE.getNamespacedKey().asString(), 2);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.FISH.getNamespacedKey().asString(), 2);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.FIREWORK.getNamespacedKey().asString(), 2);

        ADVANCEMENTS_RARITY.put(CustomAdvancement.CAVE.getNamespacedKey().asString(), 1);
        ADVANCEMENTS_RARITY.put(CustomAdvancement.BED.getNamespacedKey().asString(), 1);
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

    /*@EventHandler
    public void onEntityKill(EntityDeathEvent event) {
        if (event.getEntityType().equals(EntityType.PLAYER)) return;
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        if (killer != null)
            addPoints(killer, PointCategory.MOB_KILL, entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
    }*/

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
        Integer rarity = ADVANCEMENTS_RARITY.get(event.getAdvancement().getKey().asString());
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
