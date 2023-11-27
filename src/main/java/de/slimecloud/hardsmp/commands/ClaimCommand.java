package de.slimecloud.hardsmp.commands;

import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.player.data.Claim;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class ClaimCommand implements CommandExecutor, TabCompleter, Listener {

    public static final Map<UUID, ClaimInfo> claimingPlayers = new HashMap<>();

    private final Team firstTeam;
    private final Team secondTeam;

    public ClaimCommand() {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();

        Team firstTeam = board.getTeam("claimselection1");
        if (firstTeam == null) firstTeam = board.registerNewTeam("claimselection1");

        Team secondTeam = board.getTeam("claimselection2");
        if(secondTeam == null) secondTeam = board.registerNewTeam("claimselection2");

        firstTeam.color(NamedTextColor.BLUE);
        secondTeam.color(NamedTextColor.RED);

        this.firstTeam = firstTeam;
        this.secondTeam = secondTeam;
    }


    public static class ClaimInfo {
        public final ScheduledTask task, particles;
        public Location loc1, loc2;

        public ClaimInfo(Player player) {
            this.task = Bukkit.getAsyncScheduler().runDelayed(HardSMP.getInstance(), x -> {
                claimingPlayers.remove(player.getUniqueId());
                player.sendMessage(HardSMP.getPrefix().append(Component.text("Du hast zu lange gebraucht!\nClaim-Modus beendet!", NamedTextColor.RED)));
            }, 5, TimeUnit.MINUTES);

            this.particles = Bukkit.getAsyncScheduler().runAtFixedRate(HardSMP.getInstance(), x -> {

                Particle.DustOptions firstCorner = new Particle.DustOptions(Color.BLUE, 1.0F);
                if (loc1 != null) player.spawnParticle(Particle.REDSTONE, new Location(player.getWorld(), loc1.getX() + 0.5, loc1.getY() + 0.5, loc1.getZ() + 0.5), 1000, 0.0, 100.0, 0.0, 1.0, firstCorner);

                Particle.DustOptions secondCorner = new Particle.DustOptions(Color.RED, 1.0F);
                if (loc2 != null) player.spawnParticle(Particle.REDSTONE, new Location(player.getWorld(), loc2.getX() + 0.5, loc2.getY() + 0.5, loc2.getZ() + 0.5), 1000, 0.0, 100.0, 0.0, 1.0, secondCorner);

                //ToDo: The Particles are showing a bit too far
                Particle.DustOptions extraCorner = new Particle.DustOptions(Color.WHITE, 1.0F);
                if (loc1 != null && loc2 != null) {
                    player.spawnParticle(Particle.REDSTONE, new Location(player.getWorld(), loc1.getX(), loc1.getY(), loc2.getZ()).add(new Vector(0.5, 0.5, 0.5)), 1000, 0.0, 100.0, 0.0, 1.0, extraCorner);
                    player.spawnParticle(Particle.REDSTONE, new Location(player.getWorld(), loc2.getX(), loc1.getY(), loc1.getZ()).add(new Vector(0.5, 0.5, 0.5)), 1000, 0.0, 100.0, 0.0, 1.0, extraCorner);

                    player.spawnParticle(Particle.REDSTONE, new Location(player.getWorld(), (loc1.getX() + loc2.getX())/2, (loc1.getY() + loc2.getY())/2 + 1, loc1.getZ()).add(new Vector(0.5, 0.5, 0.5)), (int) Math.abs(loc1.getX() - loc2.getX()) * 5, Math.abs(loc1.getX() - loc2.getX())/2, 0.0, 0.0, extraCorner);
                    player.spawnParticle(Particle.REDSTONE, new Location(player.getWorld(), (loc1.getX() + loc2.getX())/2, (loc1.getY() + loc2.getY())/2 + 1, loc2.getZ()).add(new Vector(0.5, 0.5, 0.5)), (int) Math.abs(loc1.getX() - loc2.getX()) * 5, Math.abs(loc1.getX() - loc2.getX())/2, 0.0, 0.0, extraCorner);
                    player.spawnParticle(Particle.REDSTONE, new Location(player.getWorld(), loc1.getX(), (loc1.getY() + loc2.getY())/2 + 1, (loc1.getZ() + loc2.getZ())/2).add(new Vector(0.5, 0.5, 0.5)), (int) Math.abs(loc1.getZ() - loc2.getZ()) * 5, 0.0, 0.0, Math.abs(loc1.getZ() - loc2.getZ())/2, extraCorner);
                    player.spawnParticle(Particle.REDSTONE, new Location(player.getWorld(), loc2.getX(), (loc1.getY() + loc2.getY())/2 + 1, (loc1.getZ() + loc2.getZ())/2).add(new Vector(0.5, 0.5, 0.5)), (int) Math.abs(loc1.getZ() - loc2.getZ()) * 5, 0.0, 0.0, Math.abs(loc1.getZ() - loc2.getZ())/2, extraCorner);
                }



            }, 0L, 500L, TimeUnit.MILLISECONDS);
        }

        public void stopTasks() {
            this.task.cancel();
            this.particles.cancel();
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(commandSender instanceof Player player)) return true;

        if (args.length == 1) {
            UUID uuid = ((Player) commandSender).getUniqueId();

            switch (args[0].toLowerCase()) {
                case "start" -> {
                    if (claimingPlayers.containsKey(uuid)) {
                        commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("Du bist schon im Claim-Modus!", NamedTextColor.RED)));
                        return true;
                    }
                    claimingPlayers.put(uuid, new ClaimInfo(player)
                    );
                    commandSender.sendMessage(HardSMP.getPrefix()
                            .append(Component.text("Claim-Modus erfolgreich gestartet!\n" + "Wähle zwei Ecken mit ", NamedTextColor.GREEN))
                            .append(Component.keybind("key.attack"))
                            .append(Component.text( " und ", NamedTextColor.GREEN))
                            .append(Component.keybind("key.use"))
                            .append(Component.text("!", NamedTextColor.GREEN))
                    );
                }
                case "finish" -> {
                    ClaimInfo task = claimingPlayers.get(uuid);
                    if (task != null) {
                        if (task.loc1 != null && task.loc2 != null) {
                            claimingPlayers.remove(uuid);
                            task.stopTasks();
                            new Claim(uuid.toString(), (int) task.loc1.getX(), (int) task.loc1.getZ(), (int) task.loc2.getX(), (int) task.loc2.getZ()).save();

                            player.getWorld().getEntitiesByClass(Shulker.class).stream().filter(sb -> sb.getScoreboardTags().contains("marker1" + uuid) || sb.getScoreboardTags().contains("marker2" + uuid)).forEach(Entity::remove);

                            commandSender.sendMessage(HardSMP.getPrefix().append(Component.text(
                                    "Der Bereich von (" + (int) task.loc1.getX() + ", " + (int) task.loc1.getZ() + ") bis (" + (int) task.loc2.getX() + ", " + task.loc2.getZ() + ")\nwurde erfolgreich geclaimt!",
                                    NamedTextColor.GREEN
                            )));
                        } else commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("Du hast nicht alle Ecken gesetzt!", NamedTextColor.RED)));
                    } else commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("Du bist nicht im Claim-Modus!", NamedTextColor.RED)));
                }
                case "cancel" -> {
                    ClaimInfo task = claimingPlayers.remove(uuid);
                    if (task != null) {
                        task.stopTasks();

                        player.getWorld().getEntitiesByClass(Shulker.class).stream().filter(sb -> sb.getScoreboardTags().contains("marker1" + uuid) || sb.getScoreboardTags().contains("marker2" + uuid)).forEach(Entity::remove);

                        commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("Claim-Modus erfolgreich beendet!", NamedTextColor.GREEN)));
                    } else commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("Du bist nicht im Claim-Modus!", NamedTextColor.RED)));
                }
                default ->
                        commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("Benutzung: /claim [start/cancel/finish]!", NamedTextColor.RED)));
            }
        } else commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("Benutzung: /claim [start/cancel/finish]!", NamedTextColor.RED)));

        return true;
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        ClaimInfo info = claimingPlayers.get(event.getPlayer().getUniqueId());

        if (info == null) return;
        if (event.getClickedBlock() == null) return;

        Shulker mark;

        if (event.getAction().isLeftClick()) {
            if (info.loc1 != null && event.getClickedBlock().getLocation().getX() == info.loc1.getX() && event.getClickedBlock().getLocation().getZ() == info.loc1.getZ()) return;

            info.loc1 = event.getClickedBlock().getLocation();
            event.getPlayer().sendMessage(HardSMP.getPrefix().append(
                    Component.text("§9Erste §aEcke: " + info.loc1.getX() + ", " + info.loc1.getZ())
            ));

            event.getPlayer().getWorld().getEntitiesByClass(Shulker.class).stream().filter(sb -> sb.getScoreboardTags().contains("marker1" + event.getPlayer().getUniqueId())).forEach(Entity::remove);

            mark = event.getPlayer().getWorld().spawn(event.getClickedBlock().getLocation(), Shulker.class);
            mark.addScoreboardTag("marker1" + event.getPlayer().getUniqueId());

            firstTeam.addEntity(mark);

        } else if (event.getAction().isRightClick()) {
            if (info.loc2 != null && event.getClickedBlock().getLocation().getX() == info.loc2.getX() && event.getClickedBlock().getLocation().getZ() == info.loc2.getZ()) return;

            info.loc2 = event.getClickedBlock().getLocation();
            event.getPlayer().sendMessage(HardSMP.getPrefix().append(
                    Component.text("§cZweite §aEcke: " + info.loc2.getX() + ", " + info.loc2.getZ(), NamedTextColor.GREEN)
            ));

            event.getPlayer().getWorld().getEntitiesByClass(Shulker.class).stream().filter(sb -> sb.getScoreboardTags().contains("marker2" + event.getPlayer().getUniqueId())).forEach(Entity::remove);

            mark = event.getPlayer().getWorld().spawn(event.getClickedBlock().getLocation(), Shulker.class);
            mark.addScoreboardTag("marker2" + event.getPlayer().getUniqueId());

            secondTeam.addEntity(mark);

        } else return;

        mark.setInvisible(true);
        mark.setAI(false);

        mark.setInvulnerable(true);
        mark.setGlowing(true);
        mark.setGravity(false);
        mark.setLootTable(null);
        mark.spawnAt(event.getClickedBlock().getLocation());

        if (info.loc1 != null && info.loc2 != null) {
            int blocks = (int) ((Math.abs(info.loc1.getX() - info.loc2.getX()) + 1) * (Math.abs(info.loc1.getZ() - info.loc2.getZ()) + 1));

            event.getPlayer().sendActionBar(Component.text(blocks + (blocks == 1 ? " Block" : " Blöcke") + " ausgewählt", NamedTextColor.GREEN));
        }

    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {
        if (!claimingPlayers.containsKey(event.getPlayer().getUniqueId())) return;

        ClaimInfo info = claimingPlayers.get(event.getPlayer().getUniqueId());
        int blocks;
        int maxBlocks = 100;

        if (info.loc1 != null && info.loc2 != null) {
            blocks = (int) ((Math.abs(info.loc1.getX() - info.loc2.getX()) + 1) * (Math.abs(info.loc1.getZ() - info.loc2.getZ()) + 1));

            event.getPlayer().sendActionBar(Component.text(blocks + (blocks == 1 ? " Block" : " Blöcke") + " ausgewählt",
                    blocks > maxBlocks ? NamedTextColor.RED : NamedTextColor.GREEN));
        } else if (info.loc1 != null || info.loc2 != null) {
            blocks = (int) (((info.loc1 == null ?
                    Math.abs(event.getPlayer().getTargetBlock(null, 5).getLocation().getX() - info.loc2.getX()) + 1 :
                    Math.abs(event.getPlayer().getTargetBlock(null, 5).getLocation().getX() - info.loc1.getX()) + 1)) *
                    (info.loc1 == null ?
                    Math.abs(event.getPlayer().getTargetBlock(null, 5).getLocation().getZ() - info.loc2.getZ()) + 1 :
                    Math.abs(event.getPlayer().getTargetBlock(null, 5).getLocation().getZ() - info.loc1.getZ()) + 1));

            event.getPlayer().sendActionBar(Component.text(blocks + (blocks == 1 ? " Block" : " Blöcke") + " ausgewählt",
                    blocks > maxBlocks ? NamedTextColor.RED : NamedTextColor.GREEN));
        }

    }

    @EventHandler
    private void onBreak(BlockBreakEvent event) {
        if (claimingPlayers.containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onPlace(BlockPlaceEvent event) {
        if (claimingPlayers.containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return Stream.of("start", "cancel", "finish")
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .toList();
    }
}
