package de.slimecloud.hardsmp.player.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.BiFunction;

@RequiredArgsConstructor
public enum PointCategory {

    ANIMALS_BRED(0.25),
    EAT_CAKE_SLICE(1),

    //Every 100cm
    CROUCH_ONE_CM(1d / 1.83, (points, cm) -> points * cm),
    FLY_ONE_CM(1d / 4.35, (points, cm) -> points * cm),
    SPRINT_ONE_CM(1d / 5.73, (points, cm) -> points * cm),
    SWIM_ONE_CM(1d / 4, (points, cm) -> points * cm),
    WALK_ONE_CM(1d / 4.41, (points, cm) -> points * cm),
    WALK_ON_WATER_ONE_CM(1d / 3, (points, cm) -> points * cm),
    WALK_UNDER_WATER_ONE_CM(1d / 2, (points, cm) -> points * cm),
    BOAT_ONE_CM(1d / 8, (points, cm) -> points * cm),
    AVIATE_ONE_CM(1d / 36, (points, cm) -> points * cm),
    HORSE_ONE_CM(1d / 12, (points, cm) -> points * cm),
    MINECART_ONE_CM(1d / 8, (points, cm) -> points * cm),
    PIG_ONE_CM(1d / 2.42, (points, cm) -> points * cm),
    STRIDER_ONE_CM(1d / 4.14, (points, cm) -> points * cm),


    FISH_CAUGHT(0.5),
    ENCHANT_ITEM(1, (points, weight) -> points / weight), // points / enchant weight  (lower weight is better enchantment)
    JUMP(1d / 20),
    MOB_KILL(0.05, (points, mob_hp) -> points * mob_hp), //  points*mob-hp
    DEATH(1),
    PLAYER_KILL(0.5, (points, hp) -> points * hp), // points * hp after kill
    RAID_WIN(5),
    VILLAGER_TRADED(1d / 15),
    PLAY_TIME(1), // per 5 min
    ADVANCEMENT(1, (points, lvl) -> ((9 * lvl - 5) / 4) * points);

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
