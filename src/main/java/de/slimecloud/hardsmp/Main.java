package de.slimecloud.hardsmp;

import de.slimecloud.hardsmp.database.Database;
import lombok.Getter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Main extends JavaPlugin {

    private static Main instance;

    @Getter
    private Database database;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        this.database = new Database(getConfig().getString("database.host"), getConfig().getString("database.user"), getConfig().getString("database.password"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerEvent(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    private PluginCommand registerCommand(String name, CommandExecutor executor) {
        PluginCommand command = getCommand(name);
        if (command!=null) command.setExecutor(executor);
        return command;
    }

    public static Main getInstance() {
        return instance;
    }
}
