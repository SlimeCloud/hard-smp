package de.slimecloud.hardsmp.player.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.BiFunction;

@RequiredArgsConstructor
public enum PointCategory {

    ANIMALS_BRED(0.25),
    EAT_CAKE_SLICE(1),

    //Every 100cm
    CROUCH_ONE_CM(1d/1.83),
    FLY_ONE_CM(1d/4.35),
    SPRINT_ONE_CM(1d/5.73),
    SWIM_ONE_CM(1d/4),
    WALK_ONE_CM(1d/4.41),
    WALK_ON_WATER_ONE_CM(1d/3),
    WALK_UNDER_WATER_ONE_CM(1d/2),
    BOAT_ONE_CM(1d/8),
    AVIATE_ONE_CM(1d/36),
    HORSE_ONE_CM(1d/12),
    MINECART_ONE_CM(1d/8),
    PIG_ONE_CM(1d/2.42),
    STRIDER_ONE_CM(1d/4.14),


    FISH_CAUGHT(0.5),
    ENCHANT_ITEM(1, (points, weight) -> points/weight), // points / enchant weight  (lower weight is better enchantment)
    JUMP(1d/20),
    MOB_KILL(0.05, (points, mob_hp) -> points*mob_hp), //  points*mob-hp
    DEATH(1),
    PLAYER_KILL(0.5, (points, hp) -> points*hp), // points * hp after kill
    RAID_WIN(5),
    VILLAGER_TRADED(1d/15),
    PLAY_TIME(1), // per 5 min
    ADVANCEMENT(1, (points, lvl) -> ((9*lvl-5)/4)*points);

    @Getter
    private final double points;
    private final BiFunction<Double, Double, Double> calcFunction;

    PointCategory(double points) {
        this(points, (p, val) -> p);
    }

    public double calculate(double val) {
        return calcFunction.apply(getPoints(), val);
    }
}
