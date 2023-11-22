package de.slimecloud.hardsmp.commands;

import de.cyklon.spigotutils.adventure.Formatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class FormattingCommand implements CommandExecutor, EmptyTabCompleter {

    private final static Component FORMATTINGS;

    static {
        final Component[] component = {Component.text("Diese Formattierungen kannst du im Chat verwenden:").color(TextColor.color(0x86D356)) .appendNewline()};
        Formatter.getColorFormattings().forEach((k, v) -> {
            component[0] = component[0]
                    .append(Component.text("&" + k + " -> §r")
                            .append(Formatter.parseText("§" + k + " Beispiel Text"))).appendNewline();
        });
        Formatter.getCustomFormattings().forEach((k, v) -> {
            component[0] = component[0]
                    .append(Component.text("&" + k + " -> §r")
                            .append(Formatter.parseText("§" + k + " Beispiel Text"))).appendNewline();
        });
        Formatter.getDecorationFormattings().forEach((k, v) -> {
            component[0] = component[0]
                    .append(Component.text("&" + k + " -> §r")
                            .append(Formatter.parseText("§" + k + " Beispiel Text"))).appendNewline();
        });
        FORMATTINGS = component[0]
                .append(Component.text("&r -> ")
                        .append(Formatter.parseText("Reset (Setzt die Formatierung zurück)")));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage(FORMATTINGS);
        return true;
    }
}
