package de.slimecloud.hardsmp.commands;

import de.cyklon.spigotutils.adventure.Formatter;
import de.slimecloud.hardsmp.HardSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class HatItemCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player sendPlayer)) {
            sender.sendMessage("Der Command kann nur als Spieler ausgeführt werden!");
            return true;
        }
        Player targetPlayer = sendPlayer;
        if (args.length > 0) {
            if (!sendPlayer.hasPermission("hardsmp.command.hatitem.other")) {
                sendPlayer.sendMessage(HardSMP.getPrefix().append(Component.text("Du hast keine Berechtigung anderen Spielern ein Item zuu setzen!", NamedTextColor.RED)));
            }
            targetPlayer = Bukkit.getPlayer(args[0]);
            if (targetPlayer == null) {
                sender.sendMessage(Formatter.parseText(HardSMP.getInstance().getConfig().getString("hatitem.playerNotFoundErrorMessage", "§cSpieler wurde nicht gefunden!")));
                return true;
            }
        }
        if (!(targetPlayer.getInventory().getItem(EquipmentSlot.HEAD).getType() == Material.AIR)) {
            sendPlayer.sendMessage(HardSMP.getPrefix().append(Component.text("§cDer Helm slot ist bereits belegt", NamedTextColor.RED)));
            return true;
        }
        ItemStack item = sendPlayer.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            sendPlayer.sendMessage(HardSMP.getPrefix().append(Component.text("Nimm ein Material in die Hand welches du auf den Kopf setzen willst!", NamedTextColor.RED)));
            return true;
        }
        targetPlayer.getInventory().setHelmet(item);
        sendPlayer.getInventory().remove(item);
        targetPlayer.sendMessage(HardSMP.getPrefix().append(Component.text("Dir wurde " + item.getType().toString().toLowerCase().replace("_", " ") + " auf den Kopf gesetzt!", TextColor.color(0x88d657))));

        return true;
    }
}
