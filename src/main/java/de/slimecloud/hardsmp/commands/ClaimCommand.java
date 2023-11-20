package de.slimecloud.hardsmp.commands;

import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.player.data.Claim;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class ClaimCommand implements CommandExecutor, TabCompleter, Listener {

    public final Map<String, ClaimInfo> claimingPlayers = new HashMap<>();
    public static class ClaimInfo {
        public final ScheduledTask task;
        public Integer x1, z1, x2, z2;

        public ClaimInfo(ScheduledTask task) {
            this.task = task;
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1) {
            String uuid = ((Player) commandSender).getUniqueId().toString();

            switch (args[0]) {
                case "start" -> {
                    if (claimingPlayers.containsKey(uuid)) {
                        commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("Du bist schon im Claim-Modus!", NamedTextColor.RED)));
                        return true;
                    }
                    claimingPlayers.put(
                            uuid,
                            new ClaimInfo(Bukkit.getAsyncScheduler().runDelayed(HardSMP.getInstance(), x -> {
                                claimingPlayers.remove(uuid);
                                commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("Du hast zu lange gebraucht!\nClaim-Modus beendet!", NamedTextColor.RED)));
                            }, 5, TimeUnit.MINUTES))
                    );
                    commandSender.sendMessage(HardSMP.getPrefix()
                            .append(Component.text("Claim-Modus erfolgreich gestartet!\n" + "Wähle zwei Ecken mit ", NamedTextColor.GREEN))
                            .append(Component.keybind("break"))
                            .append(Component.text( " und ", NamedTextColor.GREEN))
                            .append(Component.keybind("use"))
                            .append(Component.text("!", NamedTextColor.GREEN))
                    );
                }
                case "finish" -> {
                    ClaimInfo task = claimingPlayers.get(uuid);
                    if (task != null) {
                        if (task.x1 != null && task.x2 != null && task.z1 != null && task.z2 != null) {
                            claimingPlayers.remove(uuid);
                            task.task.cancel();
                            new Claim(uuid, task.x1, task.z1, task.x2, task.z2).save();
                            commandSender.sendMessage(HardSMP.getPrefix().append(Component.text(
                                    "Der Bereich von (" + task.x1 + ", " + task.z1 + ") bis (" + task.x2 + ", " + task.z2 + ")\nwurde erfolgreich geclaimt!",
                                    NamedTextColor.GREEN
                            )));
                        } else {
                            commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("Du hast nicht alle Ecken gesetzt!", NamedTextColor.RED)));
                        }
                    } else {
                        commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("Du bist nicht im Claim-Modus!", NamedTextColor.RED)));
                    }
                }
                case "cancel" -> {
                    ClaimInfo task = claimingPlayers.remove(uuid);
                    if (task != null) {
                        task.task.cancel();
                        commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("Claim-Modus erfolgreich beendet!", NamedTextColor.GREEN)));
                    } else {
                        commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("Du bist nicht im Claim-Modus!", NamedTextColor.RED)));
                    }
                }
                default ->
                        commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("Benutzung: /claim [start/finish]!", NamedTextColor.RED)));
            }
        } else {
            commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("Benutzung: /claim [start/finish]!", NamedTextColor.RED)));
        }

        return true;
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        ClaimInfo info = claimingPlayers.get(event.getPlayer().getUniqueId().toString());
        if(info == null) return;
        if(event.getClickedBlock() == null) return;

        if (event.getAction().isLeftClick()) {
            info.x1 = event.getClickedBlock().getX();
            info.z1 = event.getClickedBlock().getZ();
            event.getPlayer().sendMessage(HardSMP.getPrefix().append(
                    Component.text("Erste Ecke: " + info.x1 + ", " + info.z1, NamedTextColor.GREEN)
            ));
        } else if (event.getAction().isRightClick()) {
            info.x2 = event.getClickedBlock().getX();
            info.z2 = event.getClickedBlock().getZ();
            event.getPlayer().sendMessage(HardSMP.getPrefix().append(
                    Component.text("Zweite Ecke: " + info.x2 + ", " + info.z2, NamedTextColor.GREEN)
            ));
        }

    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return Stream.of("start", "cancel", "finish")
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .toList();
    }
}
