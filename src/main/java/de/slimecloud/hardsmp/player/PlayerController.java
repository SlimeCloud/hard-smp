package de.slimecloud.hardsmp.player;

import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.claim.ClaimRights;
import de.slimecloud.hardsmp.player.data.PointCategory;
import de.slimecloud.hardsmp.player.data.Points;
import de.slimecloud.hardsmp.verify.Verification;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PlayerController {
    public static EventPlayer getPlayer(HumanEntity player) {
        return getPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
    }

    public static EventPlayer getPlayer(OfflinePlayer player) {
        return new EventPlayerImpl(player);
    }

    public static double applyFormula(double points, OfflinePlayer player) {
        int hours = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / (20 * 3600);
        return points * 0.01 * (Math.pow(0.5, (hours / 70.0 - 6.5)) + 10);
    }

    @RequiredArgsConstructor
    private static class EventPlayerImpl implements EventPlayer {

        protected final OfflinePlayer player;


        protected Points getData() {
            return Points.load(getOfflinePlayer().getUniqueId().toString());
        }

        @Override
        public void addPoints(double points) {
            if(getPlayer() == null || getPlayer().hasPermission("hardsmp.points.bypass")) return;

            HardSMP.getInstance().getLogger().info("Added " + points + " points to player " + player.getName());

            points = applyFormula(points, player);
            if(points > 50 && player.getPlayer() != null) player.getPlayer().sendMessage(HardSMP.getPrefix().append(Component.text("Dir wurden ").append(Component.text((int) points).color(NamedTextColor.RED)).append(Component.text(" Punkte hinzugefügt"))));

            double current = getActualPoints();

            if(current < 500 && current + points >= 500) {
                getPlayer().sendMessage(HardSMP.getPrefix().append(Component.text("§aDu kannst jetzt §61 §aClaim platzieren!")));
                ClaimRights.load(getUniqueId()).setClaimCount(1);
            } else if(current < 1000 && current + points >= 1000) {
                getPlayer().sendMessage(HardSMP.getPrefix().append(Component.text("§aDu kannst jetzt §62 §aClaims platzieren!")));
                ClaimRights.load(getUniqueId()).setClaimCount(2);
            } else if(current < 5000 && current + points >= 5000) {
                getPlayer().sendMessage(HardSMP.getPrefix().append(Component.text("§aDu kannst jetzt §63 §aClaims platzieren!")));
                ClaimRights.load(getUniqueId()).setClaimCount(3);
            } else if(current < 10000 && current + points >= 10000) {
                getPlayer().sendMessage(HardSMP.getPrefix().append(Component.text("§aDu kannst jetzt §64 §aClaims platzieren!")));
                ClaimRights.load(getUniqueId()).setClaimCount(4);
            } else if(current < 20000 && current + points >= 20000) {
                getPlayer().sendMessage(HardSMP.getPrefix().append(Component.text("§aDu kannst jetzt §65 §aClaims platzieren!")));
                ClaimRights.load(getUniqueId()).setClaimCount(5);
            }

            Points p = getData();
            p.setPoints(p.getPoints() + points);
            p.save();
        }

        @Override
        public void setPoints(double points) {
            HardSMP.getInstance().getLogger().info("Set " + player.getName() + "'s points to " + points);

            Points p = getData();
            p.setPoints(applyFormula(points, player));
            p.save();
        }

        @Override
        public void removePoints(double points) {
            HardSMP.getInstance().getLogger().info("Removed " + points + " points to player " + player.getName());
            addPoints(points * -1);
        }

        @Override
        public double getPoints() {
            return getData().getPoints();
        }

        @Override
        public double getActualPoints() {
            double statPoints = 0;

            if(getPlayer() == null || !getPlayer().hasPermission("hardsmp.points.bypass")) {
                statPoints += PointCategory.CROUCH_ONE_CM.calculate(player.getStatistic(Statistic.CROUCH_ONE_CM));
                statPoints += PointCategory.FLY_ONE_CM.calculate(player.getStatistic(Statistic.FLY_ONE_CM));
                statPoints += PointCategory.SPRINT_ONE_CM.calculate(player.getStatistic(Statistic.SPRINT_ONE_CM));
                statPoints += PointCategory.SWIM_ONE_CM.calculate(player.getStatistic(Statistic.SWIM_ONE_CM));
                statPoints += PointCategory.WALK_ONE_CM.calculate(player.getStatistic(Statistic.WALK_ONE_CM));
                statPoints += PointCategory.WALK_ON_WATER_ONE_CM.calculate(player.getStatistic(Statistic.WALK_ON_WATER_ONE_CM));
                statPoints += PointCategory.WALK_UNDER_WATER_ONE_CM.calculate(player.getStatistic(Statistic.WALK_UNDER_WATER_ONE_CM));
                statPoints += PointCategory.BOAT_ONE_CM.calculate(player.getStatistic(Statistic.BOAT_ONE_CM));
                statPoints += PointCategory.AVIATE_ONE_CM.calculate(player.getStatistic(Statistic.AVIATE_ONE_CM));
                statPoints += PointCategory.HORSE_ONE_CM.calculate(player.getStatistic(Statistic.HORSE_ONE_CM));
                statPoints += PointCategory.MINECART_ONE_CM.calculate(player.getStatistic(Statistic.MINECART_ONE_CM));
                statPoints += PointCategory.PIG_ONE_CM.calculate(player.getStatistic(Statistic.PIG_ONE_CM));
                statPoints += PointCategory.STRIDER_ONE_CM.calculate(player.getStatistic(Statistic.STRIDER_ONE_CM));
            }

            return getPoints() + applyFormula(statPoints / 15, player);
        }

        @Override
        @Nullable
        public Player getPlayer() {
            return getOfflinePlayer().getPlayer();
        }

        @Override
        public OfflinePlayer getOfflinePlayer() {
            return player;
        }

        @Override
        public UUID getUniqueId() {
            return getOfflinePlayer().getUniqueId();
        }

        @Override
        public @Nullable User getDiscord() {
            Verification verify = Verification.load(getOfflinePlayer().getUniqueId().toString());
            long discordUserID = verify.getDiscordID();
            return discordUserID == 0 ? null : HardSMP.getInstance().getDiscordBot().jdaInstance.getUserById(discordUserID);
        }
    }

}
