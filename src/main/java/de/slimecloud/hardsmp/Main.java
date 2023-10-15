package de.slimecloud.hardsmp;

import de.cyklon.spigotutils.item.ItemBuilder;
import de.slimecloud.hardsmp.commands.SpawnShopNPCCommand;
import de.slimecloud.hardsmp.database.Database;
import de.slimecloud.hardsmp.item.ItemManager;
import de.slimecloud.hardsmp.shop.SlimeHandler;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    public final NamespacedKey TEAM_KEY = new NamespacedKey(this, "team");
    public final NamespacedKey SHOP_KEY = new NamespacedKey(this, "shop");

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

        registerEvent(new SlimeHandler());

        itemManager.registerItem("chest-key", () -> new ItemBuilder(Material.IRON_HOE).addItemFlags(ItemFlag.HIDE_ATTRIBUTES).setDisplayName("Chest Key").build());

        SlimeHandler.setupOffers(getConfig());
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
