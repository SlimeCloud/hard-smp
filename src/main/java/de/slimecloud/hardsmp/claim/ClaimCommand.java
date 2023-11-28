package de.slimecloud.hardsmp.claim;

import de.slimecloud.hardsmp.HardSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(commandSender instanceof Player player)) return true;

        if (args.length == 1) {
            UUID uuid = ((Player) commandSender).getUniqueId();

            switch (args[0].toLowerCase()) {
                case "start" -> {
                    if (claimingPlayers.containsKey(uuid)) {
                        commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("§cDu bist schon im Claim-Modus!")));
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
                            if (Claim.loadAll(Claim::new, Collections.emptyMap()).stream()
                                    .filter(c -> !c.getUuid().equals(player.getUniqueId().toString()))
                                    .anyMatch(c -> c.contains(task.loc1) || c.contains(task.loc2) || c.contains(new Location(player.getWorld(), task.loc1.getX(), 0, task.loc2.getZ())) || c.contains(new Location(player.getWorld(), task.loc2.getX(), 0, task.loc1.getZ())))
                            ) {
                                player.sendMessage(Component.text("§cDein Gebiet überschneidet sich mit einem anderen Claim!\nBitte suche dir ein anderes Grundstück!"));
                                return true;
                            }

                            claimingPlayers.remove(uuid);
                            task.stopTasks();
                            new Claim(uuid.toString(), (int) task.loc1.getX(), (int) task.loc1.getZ(), (int) task.loc2.getX(), (int) task.loc2.getZ()).save();

                            player.getWorld().getEntitiesByClass(Shulker.class).stream().filter(sb -> sb.getScoreboardTags().contains("marker1" + uuid) || sb.getScoreboardTags().contains("marker2" + uuid)).forEach(Entity::remove);

                            commandSender.sendMessage(HardSMP.getPrefix().append(Component.text(
                                    "§aDer Bereich von (" + (int) task.loc1.getX() + ", " + (int) task.loc1.getZ() + ") bis (" + (int) task.loc2.getX() + ", " + task.loc2.getZ() + ")\nwurde erfolgreich geclaimt!"
                            )));
                        } else commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("§cDu hast nicht alle Ecken gesetzt!")));
                    } else commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("§cDu bist nicht im Claim-Modus!")));
                }
                case "cancel" -> {
                    ClaimInfo task = claimingPlayers.remove(uuid);
                    if (task != null) {
                        task.stopTasks();

                        player.getWorld().getEntitiesByClass(Shulker.class).stream().filter(sb -> sb.getScoreboardTags().contains("marker1" + uuid) || sb.getScoreboardTags().contains("marker2" + uuid)).forEach(Entity::remove);

                        commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("§aClaim-Modus erfolgreich beendet!")));
                    } else commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("§cDu bist nicht im Claim-Modus!")));
                }
                default ->
                        commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("§cBenutzung: /claim [start/cancel/finish]!")));
            }
        } else commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("§cBenutzung: /claim [start/cancel/finish]!")));

        return true;
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        ClaimInfo info = claimingPlayers.get(event.getPlayer().getUniqueId());

        if (info == null) return;
        if (event.getClickedBlock() == null) return;

        Shulker mark;

        if (event.getAction().isLeftClick()) {
            if (info.loc1 != null && event.getClickedBlock().getLocation().getX() == info.loc1.getX() && event.getClickedBlock().getLocation().getZ() == info.loc1.getZ()) return;
            if (info.loc2 != null && event.getClickedBlock().getLocation().getX() == info.loc2.getX() && event.getClickedBlock().getLocation().getZ() == info.loc2.getZ()) return;

            var old = info.loc1;
            info.loc1 = event.getClickedBlock().getLocation();

            if (getBlocks(event.getPlayer()) > maxBlocks) {
                info.loc1 = old;
                event.getPlayer().sendMessage(Component.text("§cUngültige Auswahl"));
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
                event.getPlayer().sendMessage(Component.text("§cUngültige Auswahl!"));
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

        mark.setInvisible(true);
        mark.setAI(false);

        mark.setInvulnerable(true);
        mark.setGlowing(true);
        mark.setGravity(false);
        mark.setLootTable(null);
        mark.spawnAt(event.getClickedBlock().getLocation());

        if (info.loc1 != null && info.loc2 != null) {
            int blocks = (int) ((Math.abs(info.loc1.getX() - info.loc2.getX()) + 1) * (Math.abs(info.loc1.getZ() - info.loc2.getZ()) + 1));

            event.getPlayer().sendActionBar(Component.text( "§a" + blocks + (blocks == 1 ? " Block" : " Blöcke") + " ausgewählt"));
        }

    }

    public final int maxBlocks = HardSMP.getInstance().getConfig().getInt("claim.maxblocks");

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {
        if (!claimingPlayers.containsKey(event.getPlayer().getUniqueId())) return;

        int blocks = getBlocks(event.getPlayer());
        if(blocks == 0) return;

        event.getPlayer().sendActionBar(Component.text(blocks > maxBlocks ? "§c" : "§a" + blocks + (blocks == 1 ? " Block" : " Blöcke") + " ausgewählt"));
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
        return Stream.of("start", "cancel", "finish")
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .toList();
    }
}
