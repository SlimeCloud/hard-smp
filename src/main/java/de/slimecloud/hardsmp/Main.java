package de.slimecloud.hardsmp;

import de.slimecloud.hardsmp.database.Database;
import de.slimecloud.hardsmp.verify.DiscordVerifyCommand;
import de.slimecloud.hardsmp.verify.Verify;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.luckperms.api.LuckPerms;
import lombok.NonNull;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumSet;

public final class Main extends JavaPlugin {


    public final NamespacedKey TEAM_KEY = new NamespacedKey(this, "team");

    @Getter
    private static Main instance;

    @Getter
    private Database database;

    private LuckPerms luckPerms;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        this.database = new Database(getConfig().getString("database.host"), getConfig().getString("database.user"), getConfig().getString("database.password"));

        this.luckPerms = getServer().getServicesManager().load(LuckPerms.class);

        //Events
        registerEvent(new Verify());

        new DiscordBot();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static TextComponent getPrefix() {
        return Component.text("[", NamedTextColor.DARK_GRAY)
                .append(Component.text("HardSMP", TextColor.color(0x55cfc4)))
                .append(Component.text("] ", NamedTextColor.DARK_GRAY));
    }

    private void registerEvent(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    private PluginCommand registerCommand(String name, CommandExecutor executor) {
        PluginCommand command = getCommand(name);
        if (command!=null) command.setExecutor(executor);
        return command;
    }

}
