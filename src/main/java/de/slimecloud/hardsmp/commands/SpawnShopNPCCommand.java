package de.slimecloud.hardsmp.commands;

import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.shop.SlimeNPC;
import de.slimecloud.hardsmp.shop.invshop.ArenaShopNPC;
import de.slimecloud.hardsmp.shop.invshop.ClaimShopNPC;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public class SpawnShopNPCCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (commandSender instanceof Player player) {
            if (args.length == 0)
                new SlimeNPC(player.getLocation());
            else if (args.length == 1) {
                switch (args[0]) {
                    case "general" -> new SlimeNPC(player.getLocation());
                    case "claimshop" -> new ClaimShopNPC(player.getLocation());
                    case "arenashop" -> new ArenaShopNPC(player.getLocation());
                    default ->
                            player.sendMessage(HardSMP.getPrefix().append(Component.text("§cBenutzung: /spawn-shop-npc [general/claimshop/arenashop]")));
                }
            } else
                player.sendMessage(HardSMP.getPrefix().append(Component.text("§cBenutzung: /spawn-shop-npc [general/claimshop/arenashop]")));
            return true;
        } else
            commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("§cDas kannst du nicht tun!")));
        return true;
    }

    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return Stream.of("general", "claimshop", "arenashop")
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .toList();
    }

}
