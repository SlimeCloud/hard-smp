package de.slimecloud.hardsmp.commands;

import de.slimecloud.hardsmp.HardSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class UnverifyCommand implements CommandExecutor, EmptyTabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(strings.length == 1) {
            Player player = Bukkit.getPlayer(strings[0]);
            Group group = HardSMP.getInstance().getLuckPerms().getGroupManager().getGroup("verified");

            if(player == null) {
                commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("Dieser Spieler ist nicht registriert!", NamedTextColor.RED)));

                return false;
            }

            if(group == null) {
                HardSMP.getInstance().getLogger().warning("Group 'verified' not found!");
                commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("Es ist ein Fehler aufgetreten, bitte wende dich an das Team!", NamedTextColor.RED)));

                return false;
            }

            HardSMP.getInstance().getLuckPerms().getUserManager().modifyUser(player.getUniqueId(), (User user) -> {
                user.data().clear(NodeType.INHERITANCE::matches);
                Node node = InheritanceNode.builder(group).build();
                user.data().remove(node);
            });

            player.sendMessage(HardSMP.getPrefix()
                    .append(Component.text("Du wurdest", TextColor.color(0x88d657)))
                    .append(Component.text(" Unverifiziert", NamedTextColor.RED, TextDecoration.BOLD))
                    .append(Component.text("!", TextColor.color(0x88d657))));

            commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("Erfolgreich Unverifiziert!", TextColor.color(0x88d657))));

            return true;
        }
        else if(strings.length == 0) {
            commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("Zu wenig Argumente!", NamedTextColor.RED)));
        }
        else {
            commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("Zu viele Argumente!", NamedTextColor.RED)));
        }
        return false;
    }
}
