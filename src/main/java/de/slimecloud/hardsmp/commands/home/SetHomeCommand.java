package de.slimecloud.hardsmp.commands.home;

import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.claim.Claim;
import de.slimecloud.hardsmp.claim.ClaimRights;
import de.slimecloud.hardsmp.commands.EmptyTabCompleter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetHomeCommand implements CommandExecutor, EmptyTabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cCommand kann nur von einem Spieler ausgeführt werden!");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(HardSMP.getPrefix().append(
                    Component.text("Bitte gib einen Namen für dein Home an!", NamedTextColor.RED)
            ));
            return true;
        }

        if (!player.hasPermission("hardsmp.command.home.multiple") && HomeData.load(player.getUniqueId()).toArray().length >= ClaimRights.load(player.getUniqueId()).getClaimCount()) {
            player.sendMessage(HardSMP.getPrefix().append(
                    Component.text("Du hast die maximale Anzahl an Homes erreicht!")
            ));
            return true;
        }

        if(Claim.allClaims.values().stream()
                .filter(c -> c.getUuid().equals(player.getUniqueId().toString()) && c.containsPlayer(player.getLocation()))
                .findAny().isEmpty()
        ) {
            player.sendMessage(HardSMP.getPrefix().append(Component.text("Du kannst homes nur in deinem eigenen geclaimten Gebiet setzten!", NamedTextColor.RED)));
            return true;
        }

        new HomeData(player.getLocation(), player.getWorld(), args[0], player.getUniqueId()).save();

        player.sendMessage(HardSMP.getPrefix().append(
                Component.text("Dein Home ", TextColor.color(0x88d657))
                        .append(Component.text(args[0], TextColor.color(0xF6ED82)))
                        .append(Component.text(" wurde gesetzt!", TextColor.color(0x88d657)))
                        .appendNewline()
                        .append(Component.text("Du kannst dich mit "))
                        .append(Component.text("§6/home " + args[0]).clickEvent(ClickEvent.suggestCommand("/home " + args[0])))
                        .append(Component.text(" dorthin teleportieren oder das Home mit "))
                        .append(Component.text("§6/removehome " + args[0]).clickEvent(ClickEvent.suggestCommand("/removehome " + args[0])))
                        .append(Component.text(" wieder entfernen"))
        ));

        return true;
    }
}
