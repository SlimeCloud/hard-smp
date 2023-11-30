package de.slimecloud.hardsmp.claim;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.slimecloud.hardsmp.HardSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class ClaimCommand implements CommandExecutor, TabCompleter, Listener {

    public static final Map<UUID, ClaimInfo> claimingPlayers = new HashMap<>();
    private final List<UUID> deletingPlayers = new ArrayList<>();

    private final Cache<String, Boolean> actionbarColor = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();

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


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(commandSender instanceof Player player)) return false;

        if (args.length == 1) {
            UUID uuid = ((Player) commandSender).getUniqueId();

            switch (args[0].toLowerCase()) {
                case "start" -> {
                    if (claimingPlayers.containsKey(uuid)) {
                        commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("§cDu bist schon im Claim-Modus!")));
                        return true;
                    }
                    if (!player.hasPermission("hardsmp.claim.bypass") && ClaimRights.load(uuid).getClaimCount() <= Claim.allClaims.values().stream().filter(c -> c.getUuid().equals(uuid.toString())).count()) {
                        commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("§cDu hast schon zu viele Claims!")));
                        return true;
                    }

                    claimingPlayers.put(uuid, new ClaimInfo(player)
                    );
                    commandSender.sendMessage(HardSMP.getPrefix()
                            .append(Component.text("§aClaim-Modus erfolgreich gestartet!\n" + "Wähle zwei Ecken mit "))
                            .append(Component.keybind("key.attack"))
                            .append(Component.text( "§a und "))
                            .append(Component.keybind("key.use"))
                            .append(Component.text("§a!"))
                    );
                }
                case "finish" -> {
                    ClaimInfo task = claimingPlayers.get(uuid);
                    if (task != null) {
                        if (task.loc1 != null && task.loc2 != null) {
                            if (Claim.allClaims.values().stream()
                                    .filter(c -> !c.getUuid().equals(player.getUniqueId().toString()))
                                    .anyMatch(c -> c.overlaps(task.loc1, task.loc2))
                            ) {
                                player.sendMessage(Component.text("§cDein Gebiet überschneidet sich mit einem anderen Claim!\nBitte suche dir ein anderes Grundstück!"));
                                return true;
                            }

                            claimingPlayers.remove(uuid);
                            task.stopTasks();
                            new Claim(uuid.toString(), (int) task.loc1.getX(), (int) task.loc1.getZ(), (int) task.loc2.getX(), (int) task.loc2.getZ(), 0).save();

                            player.getWorld().getEntitiesByClass(Shulker.class).stream().filter(sb -> sb.getScoreboardTags().contains("marker1" + uuid) || sb.getScoreboardTags().contains("marker2" + uuid)).forEach(Entity::remove);

                            commandSender.sendMessage(HardSMP.getPrefix().append(Component.text(
                                    "§aDer Bereich von (" + (int) task.loc1.getX() + ", " + (int) task.loc1.getZ() + ") bis (" + (int) task.loc2.getX() + ", " + task.loc2.getZ() + ")\nwurde erfolgreich geclaimt!"
                            )));
                        } else commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("§cDu hast nicht alle Ecken gesetzt!")));
                    } else commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("§cDu bist nicht im Claim-Modus!")));
                }
                case "cancel" -> {
                    if (deletingPlayers.contains(uuid)) {
                        player.sendMessage(HardSMP.getPrefix().append(Component.text("§cLöschen abgebrochen!")));
                        deletingPlayers.remove(uuid);
                        return true;
                    }
                    ClaimInfo task = claimingPlayers.remove(uuid);
                    if (task != null) {
                        task.stopTasks();

                        player.getWorld().getEntitiesByClass(Shulker.class).stream().filter(sb -> sb.getScoreboardTags().contains("marker1" + uuid) || sb.getScoreboardTags().contains("marker2" + uuid)).forEach(Entity::remove);

                        commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("§aClaim-Modus erfolgreich beendet!")));
                    } else commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("§cDu bist nicht im Claim-Modus!")));
                }
                case "remove" -> {
                    if (claimingPlayers.containsKey(player.getUniqueId())) {
                        player.sendMessage(HardSMP.getPrefix().append(Component.text("§cDu befindest dich im Claim-Modus!")));
                        return true;
                    }

                    Claim.allClaims.values().stream()
                            .filter(c -> c.getUuid().equals(uuid.toString()) && c.contains(player.getLocation()))
                            .findAny().ifPresentOrElse(
                                    claim -> {
                                        if (deletingPlayers.contains(uuid)) {
                                            HardSMP.getInstance().getDatabase().run(handle -> handle.createUpdate("delete from claims where id = :id").bind("id", claim.getId()).execute());
                                            deletingPlayers.remove(uuid);
                                            player.sendMessage(HardSMP.getPrefix().append(Component.text("§aClaim gelöscht!")));
                                        } else {
                                            player.sendMessage(HardSMP.getPrefix().append(Component.text("§4Möchtest du dieses claim wirklich löschen?\nBenutze erneut §6/claim remove§4 um dies zu bestätigen!\nBenutze §6/claim cancel§4 um den Prozess abzubrechen!")));
                                            deletingPlayers.add(uuid);
                                            Bukkit.getAsyncScheduler().runDelayed(HardSMP.getInstance(), x -> {
                                                deletingPlayers.remove(uuid);
                                                player.sendMessage(HardSMP.getPrefix().append(Component.text("§cLöschen abgebrochen!")));
                                            }, 1, TimeUnit.MINUTES);
                                        }
                                    },
                                    () -> player.sendMessage(HardSMP.getPrefix().append(Component.text("§cDu befindest dich nicht auf einem deiner Claims!")))
                            );

                    return true;
                }
                default ->
                        commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("§cBenutzung: /claim [start/cancel/finish/remove]!")));
            }
        } else commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("§cBenutzung: /claim [start/cancel/finish/remove]!")));

        return true;
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        ClaimInfo info = claimingPlayers.get(event.getPlayer().getUniqueId());

        if (info == null) return;
        if (event.getClickedBlock() == null) return;

        Shulker mark;

        int maxBlocks = getMaxBlocks(event.getPlayer());

        if (event.getAction().isLeftClick()) {
            if (info.loc1 != null && event.getClickedBlock().getLocation().getX() == info.loc1.getX() && event.getClickedBlock().getLocation().getZ() == info.loc1.getZ()) return;
            if (info.loc2 != null && event.getClickedBlock().getLocation().getX() == info.loc2.getX() && event.getClickedBlock().getLocation().getZ() == info.loc2.getZ()) return;

            var old = info.loc1;
            info.loc1 = event.getClickedBlock().getLocation();

            if (getBlocks(event.getPlayer()) > maxBlocks) {
                info.loc1 = old;
                event.getPlayer().sendMessage(Component.text("§cDie Fläche ist zu groß!"));
                return;
            }

            event.getPlayer().sendMessage(HardSMP.getPrefix().append(
                    Component.text("§9Erste §aEcke: " + info.loc1.getX() + ", " + info.loc1.getZ())
            ));

            event.getPlayer().getWorld().getEntitiesByClass(Shulker.class).stream().filter(sb -> sb.getScoreboardTags().contains("marker1" + event.getPlayer().getUniqueId())).forEach(Entity::remove);

            mark = event.getPlayer().getWorld().spawn(event.getClickedBlock().getLocation(), Shulker.class);
            mark.addScoreboardTag("marker1" + event.getPlayer().getUniqueId());

            firstTeam.addEntity(mark);

        } else if (event.getAction().isRightClick()) {
            if (info.loc2 != null && event.getClickedBlock().getLocation().getX() == info.loc2.getX() && event.getClickedBlock().getLocation().getZ() == info.loc2.getZ()) return;
            if (info.loc1 != null && event.getClickedBlock().getLocation().getX() == info.loc1.getX() && event.getClickedBlock().getLocation().getZ() == info.loc1.getZ()) return;

            var old = info.loc2;
            info.loc2 = event.getClickedBlock().getLocation();

            if (getBlocks(event.getPlayer()) > maxBlocks) {
                info.loc2 = old;
                event.getPlayer().sendMessage(Component.text("§cDie Fläche ist zu groß!"));
                return;
            }
            if (!event.getPlayer().hasPermission("hardsmp.claim.bypass") && getBlocks(event.getPlayer()) > ClaimRights.load(event.getPlayer().getUniqueId()).getTotalClaimSize()) {
                info.loc2 = old;
                event.getPlayer().sendMessage(Component.text("§cDu kannst nicht so viele Blöcke claimen!\nKaufe dir mehr Blöcke im §äShop§c!"));
                return;
            }

            event.getPlayer().sendMessage(HardSMP.getPrefix().append(
                    Component.text("§cZweite §aEcke: " + info.loc2.getX() + ", " + info.loc2.getZ())
            ));

            event.getPlayer().getWorld().getEntitiesByClass(Shulker.class).stream().filter(sb -> sb.getScoreboardTags().contains("marker2" + event.getPlayer().getUniqueId())).forEach(Entity::remove);

            mark = event.getPlayer().getWorld().spawn(event.getClickedBlock().getLocation(), Shulker.class);
            mark.addScoreboardTag("marker2" + event.getPlayer().getUniqueId());

            secondTeam.addEntity(mark);

        } else return;

        actionbarColor.put(event.getPlayer().getUniqueId().toString(), getBlocks(event.getPlayer()) <= ClaimRights.load(event.getPlayer().getUniqueId()).getTotalClaimSize());

        mark.setInvisible(true);
        mark.setAI(false);

        mark.setInvulnerable(true);
        mark.setGlowing(true);
        mark.setGravity(false);
        mark.setLootTable(null);
        mark.spawnAt(event.getClickedBlock().getLocation());

        /*if (info.loc1 != null && info.loc2 != null) {
            int blocks = (int) ((Math.abs(info.loc1.getX() - info.loc2.getX()) + 1) * (Math.abs(info.loc1.getZ() - info.loc2.getZ()) + 1));

            event.getPlayer().sendActionBar(Component.text( "§a" + blocks + (blocks == 1 ? " Block" : " Blöcke") + " ausgewählt"));
        }*/

    }

    public final int defaultMaxBlocks = HardSMP.getInstance().getConfig().getInt("claim.maxblocks");

    public int getMaxBlocks(Player player) {
        if(player.hasPermission("hardsmp.claim.bypass")) return Integer.MAX_VALUE;
        else return defaultMaxBlocks;
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {
        if (!claimingPlayers.containsKey(event.getPlayer().getUniqueId())) {
            Claim.allClaims.values().forEach(c -> {
                if(!c.contains(event.getFrom())) {
                    if(c.contains(event.getTo())) {
                        String name;
                        try {
                            name = Bukkit.getOfflinePlayer(UUID.fromString(c.getUuid())).getName();
                        } catch (IllegalArgumentException e) {
                            name = c.getUuid();
                        }
                        event.getPlayer().sendActionBar(Component.text("Du betrittst das Gebiet von ", NamedTextColor.GREEN).append(Component.text(name, NamedTextColor.BLUE)));
                    }
                } else {
                    if(!c.contains(event.getTo()))
                        event.getPlayer().sendActionBar(Component.text("Du betrittst ", NamedTextColor.GREEN).append(Component.text("Wildnis", NamedTextColor.GRAY)));
                }
            });
            return;
        }

        int blocks = getBlocks(event.getPlayer());
        if (blocks == 0) return;

        Boolean valid = actionbarColor.getIfPresent(event.getPlayer().getUniqueId().toString());
        event.getPlayer().sendActionBar(Component.text(valid != null && valid ? "§a" : "§c" + blocks + (blocks == 1 ? " Block" : " Blöcke") + " ausgewählt"));
    }

    private int getBlocks(Player player) {
        ClaimInfo info = claimingPlayers.get(player.getUniqueId());

        if (info.loc1 != null && info.loc2 != null) {
            return (int) ((Math.abs(info.loc1.getX() - info.loc2.getX()) + 1) * (Math.abs(info.loc1.getZ() - info.loc2.getZ()) + 1));
        } else if (info.loc1 != null || info.loc2 != null) {
            return (int) (((info.loc1 == null ?
                    Math.abs(player.getTargetBlock(null, 5).getLocation().getX() - info.loc2.getX()) + 1 :
                    Math.abs(player.getTargetBlock(null, 5).getLocation().getX() - info.loc1.getX()) + 1)) *
                    (info.loc1 == null ?
                    Math.abs(player.getTargetBlock(null, 5).getLocation().getZ() - info.loc2.getZ()) + 1 :
                    Math.abs(player.getTargetBlock(null, 5).getLocation().getZ() - info.loc1.getZ()) + 1));
        } else return 0;
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
        return Stream.of("start", "cancel", "finish", "remove")
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .toList();
    }
}
