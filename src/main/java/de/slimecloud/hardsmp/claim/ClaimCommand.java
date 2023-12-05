package de.slimecloud.hardsmp.claim;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.player.PlayerController;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static net.kyori.adventure.text.format.TextColor.color;

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
        if (secondTeam == null) secondTeam = board.registerNewTeam("claimselection2");

        firstTeam.color(NamedTextColor.BLUE);
        secondTeam.color(NamedTextColor.RED);

        this.firstTeam = firstTeam;
        this.secondTeam = secondTeam;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(commandSender instanceof Player player)) return false;
        if (!player.hasPermission("hardsmp.command.claim")) {
            player.sendMessage(HardSMP.getPrefix().append(Component.text("§cDu darfst das nicht!")));
            return false;
        }

        if (args.length >= 1) {
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
                            .append(Component.text("Claim-Modus erfolgreich gestartet!\n" + "Wähle zwei Ecken mit ", color(0x88D657)))
                            .append(Component.keybind("key.attack"))
                            .append(Component.text( " und ", color(0x88D657)))
                            .append(Component.keybind("key.use"))
                            .append(Component.text("!\n", color(0x88D657)))
                            .append(Component.text("Abbrechen mit ", color(0x88D657)))
                            .append(Component.text("§6/claim cancel").clickEvent(ClickEvent.suggestCommand("/claim cancel")))
                            .append(Component.text(", Fertigstellen mit ", color(0x88D657)))
                            .append(Component.text("§6/claim finish").clickEvent(ClickEvent.suggestCommand("/claim finish")))
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

                            ClaimRights rights = ClaimRights.load(uuid);
                            rights.setTotalClaimed(getBlocks(player));
                            rights.save();

                            actionbarColor.invalidate(uuid.toString());
                            claimingPlayers.remove(uuid);
                            task.stopTasks();
                            new Claim(uuid.toString(), (int) task.loc1.getX(), (int) task.loc1.getZ(), (int) task.loc2.getX(), (int) task.loc2.getZ(), 0).save();

                            player.getWorld().getEntitiesByClass(Shulker.class).stream().filter(sb -> sb.getScoreboardTags().contains("marker1" + uuid) || sb.getScoreboardTags().contains("marker2" + uuid)).forEach(Entity::remove);

                            commandSender.sendMessage(HardSMP.getPrefix().append(Component.text(
                                    "Der Bereich von (" + (int) task.loc1.getX() + ", " + (int) task.loc1.getZ() + ") bis (" + (int) task.loc2.getX() + ", " + task.loc2.getZ() + ")\nwurde erfolgreich geclaimt!", color(0x88D657)
                            )));

                            commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("Hinweis: ").color(NamedTextColor.RED)).append(Component.text("Du kannst jetzt in diesem Claim ein Home mit /homeset setzen!").color(NamedTextColor.GRAY)));
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

                        actionbarColor.invalidate(uuid.toString());
                        player.getWorld().getEntitiesByClass(Shulker.class).stream().filter(sb -> sb.getScoreboardTags().contains("marker1" + uuid) || sb.getScoreboardTags().contains("marker2" + uuid)).forEach(Entity::remove);

                        commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("Claim-Modus erfolgreich beendet!", color(0x88D657))));
                    } else commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("§cDu bist nicht im Claim-Modus!")));
                }
                case "remove" -> {
                    if (claimingPlayers.containsKey(player.getUniqueId())) {
                        player.sendMessage(HardSMP.getPrefix().append(Component.text("§cDu befindest dich im Claim-Modus!")));
                        return true;
                    }

                    Claim.allClaims.values().stream()
                            .filter(c -> c.getUuid().equals(uuid.toString()) && c.containsPlayer(player.getLocation()))
                            .findAny().ifPresentOrElse(
                                    claim -> {
                                        if (deletingPlayers.contains(uuid)) {
                                            ClaimRights rights = ClaimRights.load(uuid);
                                            rights.setTotalClaimed(rights.getTotalClaimed() - claim.getSize());
                                            rights.save();
                                            claim.delete();
                                            deletingPlayers.remove(uuid);
                                            player.sendMessage(HardSMP.getPrefix().append(Component.text("Claim gelöscht!", color(0x88D657))));
                                            player.sendActionBar(Component.text("Du betrittst ", color(0x88D657)).append(Component.text("Wildnis", NamedTextColor.GRAY)));
                                        } else {
                                            player.sendMessage(HardSMP.getPrefix().append(
                                                    Component.text("§4Möchtest du dieses claim wirklich löschen?\nBenutze erneut ")
                                                            .append(Component.text("§6/claim remove").clickEvent(ClickEvent.suggestCommand("/claim remove")))
                                                            .append(Component.text("§4 um dies zu bestätigen!\nBenutze "))
                                                            .append(Component.text("§6/claim cancel").clickEvent(ClickEvent.suggestCommand("/claim cancel")))
                                                            .append(Component.text("§4 um den Prozess abzubrechen!"))
                                            ));
                                            deletingPlayers.add(uuid);
                                            Bukkit.getAsyncScheduler().runDelayed(HardSMP.getInstance(), x -> {
                                                if (deletingPlayers.contains(uuid)) {
                                                    deletingPlayers.remove(uuid);
                                                    player.sendMessage(HardSMP.getPrefix().append(Component.text("§cLöschen abgebrochen!")));
                                                }
                                            }, 1, TimeUnit.MINUTES);
                                        }
                                    },
                                    () -> player.sendMessage(HardSMP.getPrefix().append(Component.text("§cDu befindest dich nicht auf einem deiner Claims!")))
                            );

                    return true;
                }
                case "info" -> {
                    if (args.length == 1) {
                        ClaimRights rights = ClaimRights.load(player.getUniqueId());
                        player.sendMessage(HardSMP.getPrefix()
                                .append(Component.text("Du kannst noch ", color(0x88D657)))
                                .append(Component.text("§6" + (rights.getTotalClaimSize() - rights.getTotalClaimed())))
                                .append(Component.text(" Blöcke claimen.\nDu hast schon ", color(0x88D657)))
                                .append(Component.text("§6" + rights.getTotalClaimed()))
                                .append(Component.text(" Blöcke geclaimt.", color(0x88D657)))
                        );
                    } else if(args.length == 2) {
                        if (player.hasPermission("hardsmp.command.claim.info.others")) {
                            Player target = (Player) Bukkit.getOfflinePlayer(args[1]);
                            ClaimRights rights = ClaimRights.load(target.getUniqueId());

                            player.sendMessage(HardSMP.getPrefix()
                                    .append(Component.text("§6" + target.getName()))
                                    .append(Component.text(" kann noch ", color(0x88D657)))
                                    .append(Component.text("§6" + (rights.getTotalClaimSize() - rights.getTotalClaimed())))
                                    .append(Component.text(" Blöcke claimen.\n", color(0x88D657)))
                                    .append(Component.text("§6" + target.getName()))
                                    .append(Component.text(" hat schon ", color(0x88D657)))
                                    .append(Component.text("§6" + rights.getTotalClaimed()))
                                    .append(Component.text(" Blöcke geclaimt.", color(0x88D657)))
                            );
                        } else player.sendMessage(HardSMP.getPrefix().append(Component.text("§cBenutzung: /claim [start/cancel/finish/remove/info]!")));
                    } else commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("§cBenutzung: /claim [start/cancel/finish/remove/info]!")));
                }
                default ->
                        commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("§cBenutzung: /claim [start/cancel/finish/remove/info]!")));
            }
        } else commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("§cBenutzung: /claim [start/cancel/finish/remove/info]!")));

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

            event.getPlayer().sendMessage(HardSMP.getPrefix()
                    .append(Component.text("§9Erste "))
                    .append(Component.text("Ecke: " + info.loc1.getX() + ", " + info.loc1.getZ(), color(0x88D657)))
            );

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
            ClaimRights rights = ClaimRights.load(event.getPlayer().getUniqueId());
            if (!event.getPlayer().hasPermission("hardsmp.claim.bypass") && getBlocks(event.getPlayer()) > rights.getTotalClaimSize() - rights.getTotalClaimed()) {
                info.loc2 = old;
                event.getPlayer().sendMessage(HardSMP.getPrefix().append(Component.text("§cDu kannst nicht so viele Blöcke claimen!\nKaufe dir mehr Blöcke im §äShop§c!")));
                return;
            }

            event.getPlayer().sendMessage(HardSMP.getPrefix()
                    .append(Component.text("§9Zweite "))
                    .append(Component.text("Ecke: " + info.loc2.getX() + ", " + info.loc2.getZ(), color(0x88D657)))
            );

            event.getPlayer().getWorld().getEntitiesByClass(Shulker.class).stream().filter(sb -> sb.getScoreboardTags().contains("marker2" + event.getPlayer().getUniqueId())).forEach(Entity::remove);

            mark = event.getPlayer().getWorld().spawn(event.getClickedBlock().getLocation(), Shulker.class);
            mark.addScoreboardTag("marker2" + event.getPlayer().getUniqueId());

            secondTeam.addEntity(mark);

        } else return;

        if (info.loc1 != null && info.loc2 != null)
            actionbarColor.put(event.getPlayer().getUniqueId().toString(), getBlocks(event.getPlayer()) <= ClaimRights.load(event.getPlayer().getUniqueId()).getTotalClaimSize());

        mark.setInvisible(true);
        mark.setAI(false);

        mark.setInvulnerable(true);
        mark.setGlowing(true);
        mark.setGravity(false);
        mark.setLootTable(null);
        mark.spawnAt(event.getClickedBlock().getLocation());

    }

    public final int defaultMaxBlocks = HardSMP.getInstance().getConfig().getInt("claim.maxblocks");

    public int getMaxBlocks(Player player) {
        if (player.hasPermission("hardsmp.claim.bypass")) return Integer.MAX_VALUE;
        else return defaultMaxBlocks;
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {
        if (!claimingPlayers.containsKey(event.getPlayer().getUniqueId())) {
            Claim.allClaims.values().forEach(c -> {
                if (!c.containsPlayer(event.getFrom())) {
                    if (c.containsPlayer(event.getTo())) {
                        String name;
                        try {
                            name = Bukkit.getOfflinePlayer(UUID.fromString(c.getUuid())).getName();
                        } catch (IllegalArgumentException e) {
                            name = c.getUuid();
                        }
                        if (name == null) name = "Unbekannt";
                        event.getPlayer().sendActionBar(Component.text("Du betrittst das Gebiet von ", color(0x88D657)).append(Component.text(name, NamedTextColor.BLUE)));
                    }
                } else {
                    if (!c.containsPlayer(event.getTo()))
                        event.getPlayer().sendActionBar(Component.text("Du betrittst ", color(0x88D657)).append(Component.text("Wildnis", NamedTextColor.GRAY)));
                }
            });
            return;
        }

        int blocks = getBlocks(event.getPlayer());
        if (blocks == 0) return;

        Boolean valid = actionbarColor.getIfPresent(event.getPlayer().getUniqueId().toString());
        event.getPlayer().sendActionBar(Component.text(((valid != null && valid) ? "§a" : "§c") + blocks + "/" + claimingPlayers.get(event.getPlayer().getUniqueId()).maxClaimSize + " Blöcke" + " ausgewählt"));
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

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        double points = PlayerController.getPlayer((OfflinePlayer) event.getPlayer()).getActualPoints();
        ClaimRights rights = ClaimRights.load(event.getPlayer().getUniqueId());

        if(points >= 30000) {
            rights.setClaimCount(5);
        } else if(points >= 20000) {
            rights.setClaimCount(4);
        } else if(points >= 10000) {
            rights.setClaimCount(3);
        } else if(points >= 5000) {
            rights.setClaimCount(2);
        } else if(points >= 500) {
            rights.setClaimCount(1);
        }
        rights.save();
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return Stream.of("start", "cancel", "finish", "remove", "info")
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .toList();
    }
}
