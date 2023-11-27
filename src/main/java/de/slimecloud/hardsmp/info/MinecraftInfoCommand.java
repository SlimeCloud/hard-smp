package de.slimecloud.hardsmp.info;

import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.player.PlayerController;
import de.slimecloud.hardsmp.verify.Verification;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MinecraftInfoCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2) return false;

        JDA jda = HardSMP.getInstance().getDiscordBot().jdaInstance;

        User user;
        OfflinePlayer player;

        switch (args[0]) {
            case "discord" -> {
                try {
                    var name = String.join(" ", args).split(" ", 2)[1];
                    user = jda.getGuildById(HardSMP.getInstance().getConfig().getLong("discord.guild")).getMembersByEffectiveName(name, true).get(0).getUser();
                } catch (NullPointerException | IndexOutOfBoundsException ignored) {
                    sender.sendMessage(Component.text("Nutzer nicht gefunden!").color(NamedTextColor.RED));
                    return true;
                }

                Verification verification = Verification.load(user);

                if (!verification.isVerified()) {
                    sender.sendMessage(Component.text("Nutzer nicht gefunden oder nicht verifiziert!").color(NamedTextColor.RED));
                    return true;
                }

                player = Bukkit.getOfflinePlayer(UUID.fromString(verification.getMinecraftID()));
            }

            case "minecraft" -> {
                player = Bukkit.getOfflinePlayer(args[1]);

                Verification verification = Verification.load(player.getUniqueId().toString());

                if (!verification.isVerified()) {
                    sender.sendMessage(Component.text("Spieler nicht gefunden oder nicht verifiziert!").color(NamedTextColor.RED));
                    return true;
                }

                user = jda.retrieveUserById(verification.getDiscordID()).complete();
            }

            default -> {
                return false;
            }
        }

        if (player.getName() == null) {
            sender.sendMessage(Component.text("Spieler nicht gefunden!").color(NamedTextColor.RED));
            return true;
        }

        sender.sendMessage(Component.text("---- Informationen zu ").color(TextColor.color(0x88D657)).append(Component.text(user.getEffectiveName()).color(TextColor.color(0xF6ED82)).decorate(TextDecoration.BOLD)).decoration(TextDecoration.BOLD, false).append(Component.text(" ----")).appendNewline()
                .append(Component.text("Minecraft Name: ").color(TextColor.color(0x88D657)).append(Component.text(player.getName()).color(TextColor.color(0xF6ED82)))).appendNewline()
                .append(Component.text("Minecraft UUID: ").color(TextColor.color(0x88D657)).append(Component.text(player.getUniqueId().toString()).color(TextColor.color(0xF6ED82)))).appendNewline()
                .append(Component.text("Discord Name: ").color(TextColor.color(0x88D657)).append(Component.text(user.getEffectiveName()).color(TextColor.color(0xF6ED82)))).appendNewline()
                .append(Component.text("Punkte: ").color(TextColor.color(0x88D657)).append(Component.text((int) PlayerController.getPlayer(player).getActualPoints()).color(TextColor.color(0xF6ED82)))).appendNewline()
        );

        return true;
    }

    @Override
    @SneakyThrows
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("discord", "minecraft").stream()
                    .filter(s -> s.startsWith(args[0]))
                    .toList();
        } else if (args.length == 2) {
            switch (args[0]) {
                case "discord" -> {
                    return HardSMP.getInstance().getDiscordBot().jdaInstance.getGuildById(HardSMP.getInstance().getConfig().getLong("discord.guild")).getMembers().stream()
                            .map(Member::getEffectiveName)
                            .filter(u -> u.contains(args[1]))
                            .toList();
                }

                case "minecraft" -> {
                    return Arrays.stream(Bukkit.getOfflinePlayers())
                            .map(OfflinePlayer::getName)
                            .filter(Objects::nonNull)
                            .filter(p -> p.contains(args[1]))
                            .toList();
                }
            }
        }

        return Collections.emptyList();
    }
}
