package de.slimecloud.hardsmp.ui;

import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.player.PlayerController;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Placeholders extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "hardsmp";
    }

    @Override
    public @NotNull String getAuthor() {
        return "hardsmp";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @NotNull List<String> getPlaceholders() {
        return List.of("%points%");
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if(params.equalsIgnoreCase("points")) return "" + (int) PlayerController.getPlayer(player).getActualPoints();
        else if(params.equalsIgnoreCase("name")) return LegacyComponentSerializer.legacySection().serialize(Chat.getName(player));

        return null;
    }
}
