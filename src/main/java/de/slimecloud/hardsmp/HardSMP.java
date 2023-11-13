package de.slimecloud.hardsmp;

import de.cyklon.spigotutils.adventure.Formatter;
import de.cyklon.spigotutils.item.ItemBuilder;
import de.cyklon.spigotutils.ui.scoreboard.ScoreboardUI;
import de.slimecloud.hardsmp.advancement.AdvancementHandler;
import de.slimecloud.hardsmp.commands.FormattingCommand;
import de.slimecloud.hardsmp.commands.PointCommand;
import de.slimecloud.hardsmp.commands.SpawnShopNPCCommand;
import de.slimecloud.hardsmp.commands.TeamChatCommand;
import de.slimecloud.hardsmp.database.Database;
import de.slimecloud.hardsmp.item.ItemManager;
import de.slimecloud.hardsmp.player.data.PointsListener;
import de.slimecloud.hardsmp.shop.SlimeHandler;
import de.slimecloud.hardsmp.subevent.replika.Replika;
import de.slimecloud.hardsmp.subevent.replika.commands.BuildSchematicCommand;
import de.slimecloud.hardsmp.subevent.replika.commands.RegisterSchematicCommand;
import de.slimecloud.hardsmp.ui.Chat;
import de.slimecloud.hardsmp.ui.Tablist;
import de.slimecloud.hardsmp.ui.scoreboard.ScoreboardManager;
import de.slimecloud.hardsmp.verify.MinecraftVerificationListener;
import lombok.AccessLevel;
import lombok.ConfigurationKeys;
import lombok.Getter;
import lombok.SneakyThrows;
import me.lucko.spark.api.Spark;
import me.lucko.spark.api.SparkProvider;
import net.dv8tion.jda.api.JDA;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.luckperms.api.LuckPerms;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationOptions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class HardSMP extends JavaPlugin {

    public final NamespacedKey SHOP_KEY = new NamespacedKey(this, "shop");

    @Getter
    private static HardSMP instance;

    @Getter
    private Database database;

    @Getter
    private ItemManager itemManager;

    @Getter
    private LuckPerms luckPerms;

    @Getter
    private Spark spark;

    @Getter
    private DiscordBot discordBot;

    @Getter
    private SubEvent subEvents;

    @Override
    public void onEnable() {
        instance = this;
        this.luckPerms = getServer().getServicesManager().load(LuckPerms.class);
        this.spark = SparkProvider.get();

        saveDefaultConfig();

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


        this.database = new Database(getConfig().getString("database.host"), getConfig().getString("database.user"), getConfig().getString("database.password"));

        this.itemManager = new ItemManager();

        getLogger().info("initialize Commands");
        registerCommand("spawn-shop-npc", new SpawnShopNPCCommand());
        registerCommand("point", new PointCommand());
        registerCommand("formatting", new FormattingCommand());

        getLogger().info("initialize Custom Items");
        itemManager.registerItem("chest-key", () -> new ItemBuilder(Material.IRON_HOE).addItemFlags(ItemFlag.HIDE_ATTRIBUTES).setDisplayName(ChatColor.RESET + "Chest Key").build());

        getLogger().info("initialize Shop Orders");
        SlimeHandler.setupOffers(getConfig());

        //Events
        registerEvent(new MinecraftVerificationListener());
        registerEvent(new SlimeHandler());
        registerEvent(new PointsListener());

        //UI
        getLogger().info("initialize UI");
        registerEvent(new ScoreboardManager(this));
        registerEvent(new Tablist(this));
        registerEvent(new Chat(getConfig()));

        getLogger().info("initialize Custom Formatting's");
        ConfigurationSection formattings = getConfig().getConfigurationSection("ui.custom-formatting");
        for (String format : formattings.getKeys(false)) {
            Formatter.registerCustomFormatting(format.charAt(0), TextColor.fromHexString(formattings.getString(format)));
        }

        AdvancementHandler.register(this, this::registerEvent);

        getLogger().info("initialize Sub Events");
        this.subEvents = new SubEvent(this);

        getLogger().info("initialize Discord Bot");
        try {
            this.discordBot = new DiscordBot();
        } catch (Exception e) {
            getLogger().warning("Failed to init Discord bot: %s".formatted(e));
        }
    }

    @Override
    public void onDisable() {
        ScoreboardUI.getScoreboards().forEach(ScoreboardUI::delete);

        this.discordBot.jdaInstance.shutdownNow();
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
        if (command != null) command.setExecutor(executor);
        return command;
    }

    public class SubEvent {

        @Getter
        private final Replika replika;

        public SubEvent(Plugin plugin) {
            this.replika = new Replika(plugin);

            registerCommand("register-schematic", new RegisterSchematicCommand());
            registerCommand("build-schematic", new BuildSchematicCommand());
        }

    }

}
