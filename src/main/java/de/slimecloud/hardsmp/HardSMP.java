package de.slimecloud.hardsmp;

import de.slimecloud.hardsmp.database.Database;
import de.slimecloud.hardsmp.discord.DiscordBot;
import de.slimecloud.hardsmp.discord.DiscordHandler;
import de.slimecloud.hardsmp.discord.LogOutputStream;
import de.slimecloud.hardsmp.verify.Verify;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.luckperms.api.LuckPerms;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.PrintStream;
import java.util.logging.FileHandler;

public final class HardSMP extends JavaPlugin {


    public final NamespacedKey TEAM_KEY = new NamespacedKey(this, "team");

    @Getter
    private static HardSMP instance;

    @Getter
    private Database database;

    private LuckPerms luckPerms;

    @Override
    public void onEnable() {
        instance = this;
        this.luckPerms = getServer().getServicesManager().load(LuckPerms.class);

        saveDefaultConfig();

        this.database = new Database(getConfig().getString("database.host"), getConfig().getString("database.user"), getConfig().getString("database.password"));

        //Events
        registerEvent(new Verify(this, this.luckPerms));

        new DiscordBot();

        PrintStream stream = new PrintStream(new LogOutputStream());
        System.setErr(stream);
        System.setOut(stream);
        getLogger().addHandler(new DiscordHandler());
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
