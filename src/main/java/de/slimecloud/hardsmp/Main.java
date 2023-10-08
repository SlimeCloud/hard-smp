package de.slimecloud.hardsmp;

import de.slimecloud.hardsmp.commands.SpawnShopNPCCommand;
import de.slimecloud.hardsmp.database.Database;
import de.slimecloud.hardsmp.item.ItemManager;
import de.slimecloud.hardsmp.shop.ShopNPC;
import dev.sergiferry.playernpc.api.NPCLib;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    public final NamespacedKey TEAM_KEY = new NamespacedKey(this, "team");

    private static Main instance;

    @Getter
    private Database database;

    @Getter
    private ItemManager itemManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        this.database = new Database(getConfig().getString("database.host"), getConfig().getString("database.user"), getConfig().getString("database.password"));

        this.itemManager = new ItemManager();

        registerCommand("spawn-shop-npc", new SpawnShopNPCCommand());

        itemManager.registerItem("chest-key", () -> null);

        NPCLib.getInstance().registerPlugin(this);

        ShopNPC.init();
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
