package de.slimecloud.hardsmp.commands;

import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.shop.invshop.InvShopHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.format.TextColor.color;

public class ArenaCommand implements CommandExecutor, EmptyTabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cBefehl kann nur als Spieler ausgeführt werden!");
            return true;
        }

        if (args.length == 0) {
            if (InvShopHandler.arenaShopActive) {
                InvShopHandler.arenaShopActive = false;
                player.sendMessage(HardSMP.getPrefix().append(Component.text("Arena Shop deaktiviert!", color(0x88D657))));
            } else {
                InvShopHandler.arenaShopActive = true;
                player.sendMessage(HardSMP.getPrefix().append(Component.text("Arena Shop aktiviert!", color(0x88D657))));
            }
        } else player.sendMessage(HardSMP.getPrefix().append(Component.text("§cBenutzung: /arena")));
        return true;
    }
}
