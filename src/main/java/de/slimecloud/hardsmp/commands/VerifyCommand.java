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

public class VerifyCommand implements CommandExecutor, EmptyTabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length == 1) {
            Player player = Bukkit.getPlayer(args[0]);
            Group group = HardSMP.getInstance().getLuckPerms().getGroupManager().getGroup("verified");

            if(player == null) {
                commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("Dieser Spieler ist nicht registriert!", NamedTextColor.RED)));

                return true;
            }

            if(group == null) {
                HardSMP.getInstance().getLogger().warning("Group 'verified' not found!");
                commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("Es ist ein Fehler aufgetreten, bitte wende dich an das Team!", NamedTextColor.RED)));

                return true;
            }

            HardSMP.getInstance().getLuckPerms().getUserManager().modifyUser(player.getUniqueId(), (User user) -> {
                user.data().clear(NodeType.INHERITANCE::matches);
                Node node = InheritanceNode.builder(group).build();
                user.data().add(node);
            });

            player.sendMessage(HardSMP.getPrefix()
                    .append(Component.text("Du wurdest erfolgreich", TextColor.color(0x88d657)))
                    .append(Component.text(" Verifiziert", TextColor.color(0x55cfc4), TextDecoration.BOLD))
                    .append(Component.text("!", TextColor.color(0x88d657))));

            commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("Erfolgreich verifiziert!", TextColor.color(0x88d657))));

        }
        else {
            commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("Benutzung: /verify [name]!", NamedTextColor.RED)));
        }
        return true;
    }
}
