package de.slimecloud.hardsmp.claim;

import de.slimecloud.hardsmp.HardSMP;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.util.Vector;

import java.util.concurrent.TimeUnit;

public class ClaimInfo {
    public final ScheduledTask task, particles;
    public Location loc1, loc2;

    public ClaimInfo(Player player) {
        this.task = Bukkit.getAsyncScheduler().runDelayed(HardSMP.getInstance(), x -> {
            ClaimCommand.claimingPlayers.remove(player.getUniqueId());
            player.sendMessage(HardSMP.getPrefix().append(Component.text("§cDu hast zu lange gebraucht!\nClaim-Modus beendet!")));
            player.getWorld().getEntitiesByClass(Shulker.class).stream().filter(sb -> sb.getScoreboardTags().contains("marker1" + player.getUniqueId()) || sb.getScoreboardTags().contains("marker2" + player.getUniqueId())).forEach(Entity::remove);
            stopTasks();
            ClaimCommand.claimingPlayers.remove(player.getUniqueId());
        }, 5, TimeUnit.MINUTES);

        this.particles = Bukkit.getAsyncScheduler().runAtFixedRate(HardSMP.getInstance(), x -> {

            Particle.DustOptions firstCorner = new Particle.DustOptions(Color.BLUE, 1.0F);
            if (loc1 != null)
                player.spawnParticle(Particle.REDSTONE, new Location(player.getWorld(), loc1.getX() + 0.5, loc1.getY() + 0.5, loc1.getZ() + 0.5), 100, 0.0, 10, 0.0, 1.0, firstCorner);

            Particle.DustOptions secondCorner = new Particle.DustOptions(Color.RED, 1.0F);
            if (loc2 != null)
                player.spawnParticle(Particle.REDSTONE, new Location(player.getWorld(), loc2.getX() + 0.5, loc2.getY() + 0.5, loc2.getZ() + 0.5), 100, 0.0, 10.0, 0.0, 1.0, secondCorner);

            Particle.DustOptions extraCorner = new Particle.DustOptions(Color.WHITE, 1.0F);
            if (loc1 != null && loc2 != null) {
                player.spawnParticle(Particle.REDSTONE, new Location(player.getWorld(), loc1.getX(), loc1.getY(), loc2.getZ()).add(new Vector(0.5, 0.5, 0.5)), 100, 0.0, 10.0, 0.0, 1.0, extraCorner);
                player.spawnParticle(Particle.REDSTONE, new Location(player.getWorld(), loc2.getX(), loc1.getY(), loc1.getZ()).add(new Vector(0.5, 0.5, 0.5)), 100, 0.0, 10.0, 0.0, 1.0, extraCorner);

                player.spawnParticle(Particle.REDSTONE, new Location(player.getWorld(), (loc1.getX() + loc2.getX()) / 2, (loc1.getY() + loc2.getY()) / 2 + 1, loc1.getZ()).add(new Vector(0.5, 0.5, 0.5)), (int) Math.abs(loc1.getX() - loc2.getX()) * 5, Math.abs(loc1.getX() - loc2.getX()) / 4, 0.0, 0.0, extraCorner);
                player.spawnParticle(Particle.REDSTONE, new Location(player.getWorld(), (loc1.getX() + loc2.getX()) / 2, (loc1.getY() + loc2.getY()) / 2 + 1, loc2.getZ()).add(new Vector(0.5, 0.5, 0.5)), (int) Math.abs(loc1.getX() - loc2.getX()) * 5, Math.abs(loc1.getX() - loc2.getX()) / 4, 0.0, 0.0, extraCorner);
                player.spawnParticle(Particle.REDSTONE, new Location(player.getWorld(), loc1.getX(), (loc1.getY() + loc2.getY()) / 2 + 1, (loc1.getZ() + loc2.getZ()) / 2).add(new Vector(0.5, 0.5, 0.5)), (int) Math.abs(loc1.getZ() - loc2.getZ()) * 5, 0.0, 0.0, Math.abs(loc1.getZ() - loc2.getZ()) / 4, extraCorner);
                player.spawnParticle(Particle.REDSTONE, new Location(player.getWorld(), loc2.getX(), (loc1.getY() + loc2.getY()) / 2 + 1, (loc1.getZ() + loc2.getZ()) / 2).add(new Vector(0.5, 0.5, 0.5)), (int) Math.abs(loc1.getZ() - loc2.getZ()) * 5, 0.0, 0.0, Math.abs(loc1.getZ() - loc2.getZ()) / 4, extraCorner);
            }


        }, 0L, 500L, TimeUnit.MILLISECONDS);
    }

    public void stopTasks() {
        this.task.cancel();
        this.particles.cancel();
    }
}
