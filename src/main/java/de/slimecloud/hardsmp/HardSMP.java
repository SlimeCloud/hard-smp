package de.slimecloud.hardsmp;

import de.cyklon.spigotutils.adventure.Formatter;
import de.cyklon.spigotutils.item.ItemBuilder;
import de.cyklon.spigotutils.ui.scoreboard.ScoreboardUI;
import de.slimecloud.hardsmp.advancement.AdvancementHandler;
import de.slimecloud.hardsmp.claim.ClaimCommand;
import de.slimecloud.hardsmp.claim.ClaimInfo;
import de.slimecloud.hardsmp.claim.ClaimProtectionHandler;
import de.slimecloud.hardsmp.commands.*;
import de.slimecloud.hardsmp.commands.info.MinecraftInfoCommand;
import de.slimecloud.hardsmp.commands.SpawnCommand;
import de.slimecloud.hardsmp.database.Database;
import de.slimecloud.hardsmp.item.*;
import de.slimecloud.hardsmp.listener.DeathPointHandler;
import de.slimecloud.hardsmp.listener.PunishmentListener;
import de.slimecloud.hardsmp.player.data.PointsListener;
import de.slimecloud.hardsmp.shop.SlimeHandler;
import de.slimecloud.hardsmp.shop.claimshop.ClaimShopHandler;
import de.slimecloud.hardsmp.ui.*;
import de.slimecloud.hardsmp.ui.scoreboard.ScoreboardManager;
import de.slimecloud.hardsmp.verify.MinecraftVerificationListener;
import lombok.Getter;
import me.lucko.spark.api.Spark;
import me.lucko.spark.api.SparkProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Shulker;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class HardSMP extends JavaPlugin {

    public final NamespacedKey SHOP_KEY = new NamespacedKey(this, "shop");
    public final NamespacedKey CLAIM_SHOP_KEY = new NamespacedKey(this, "claimshop");

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
    private ClaimShopHandler claimShopHandler;

    @Getter
    private ChestKey chestKey;

    @Getter
    private LockPick lockPick;

    @Getter
    private PlotBuyer plotBuyer25;
    @Getter
    private PlotBuyer plotBuyer100;
    @Getter
    private PlotBuyer plotBuyer500;
    @Getter
    private PlotBuyer plotBuyer1000;
    @Getter
    private PlotBuyer plotBuyer5000;

    @Getter
    private Chat chat;

    @Override
    public void onEnable() {
        new Placeholders().register();

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

        ConfigurationSection formattings = getConfig().getConfigurationSection("ui.custom-formatting");
        for (String format : formattings.getKeys(false)) {
            Formatter.registerCustomFormatting(format.charAt(0), TextColor.fromHexString(formattings.getString(format)));
            getLogger().info("registered \"" + format + "\" as color code for " + formattings.getString(format));
        }

        ClaimCommand claim = new ClaimCommand();
        claimShopHandler = new ClaimShopHandler();
        RulesCommand rules = new RulesCommand();
        KeyChainCommand keyChain;

        //commands
        registerCommand("spawn-shop-npc", new SpawnShopNPCCommand());
        registerCommand("point", new PointCommand());
        registerCommand("formatting", new FormattingCommand());
        registerCommand("verify", new VerifyCommand());
        registerCommand("unverify", new UnverifyCommand());
        registerCommand("help", new HelpCommand());
        registerCommand("rules", rules);
        registerCommand("teamchat", new TeamChatCommand());
        registerCommand("enderchest", new EnderchestCommand());
        registerCommand("keys", keyChain = new KeyChainCommand(this));
        registerCommand("bug", new BugCommand());
        registerCommand("feedback", new FeedbackCommand());
        registerCommand("leaderboard", new LeaderboardCommand());
        registerCommand("msg", new MsgCommand());
        registerCommand("reply", new ReplyCommand());
        registerCommand("claim", claim);
        registerCommand("hatitem", new HatItemCommand());
        registerCommand("spawn", new SpawnCommand());

        registerCommand("info", new MinecraftInfoCommand());
        //Events
        registerEvent(new MinecraftVerificationListener());
        registerEvent(new SlimeHandler());
        registerEvent(claimShopHandler);
        registerEvent(new PointsListener());
        registerEvent(rules);
        registerEvent(keyChain);
        registerEvent(new DeathPointHandler());
        registerEvent(claim);
        registerEvent(new ClaimProtectionHandler());
        registerEvent(new PunishmentListener(this));

        //UI
        registerEvent(new ScoreboardManager(this));
        registerEvent(new Tablist(this));
        registerEvent(chat = new Chat(getConfig()));
        registerEvent(new JoinMessage());
        registerEvent(new AdvancementMessage());

        //Custom Items
        registerEvent(chestKey = new ChestKey(this));
        registerEvent(lockPick = new LockPick(chestKey));

        ConfigurationSection section = getConfig().getConfigurationSection("claimshop.offers");
        if (section == null) getLogger().warning("Could not initialize claimshop, config misconfigured!");
        else {
            registerEvent(plotBuyer25 = new PlotBuyer(section.getInt("plot-buyer-25.blocks"), section.getInt("plot-buyer-25.price.required-points"), section.getInt("plot-buyer-25.quantity"), section.getInt("plot-buyer-25.index")));
            registerEvent(plotBuyer100 = new PlotBuyer(section.getInt("plot-buyer-100.blocks"), section.getInt("plot-buyer-100.price.required-points"), section.getInt("plot-buyer-100.quantity"), section.getInt("plot-buyer-100.index")));
            registerEvent(plotBuyer500 = new PlotBuyer(section.getInt("plot-buyer-500.blocks"), section.getInt("plot-buyer-500.price.required-points"), section.getInt("plot-buyer-500.quantity"), section.getInt("plot-buyer-500.index")));
            registerEvent(plotBuyer1000 = new PlotBuyer(section.getInt("plot-buyer-1000.blocks"), section.getInt("plot-buyer-1000.price.required-points"), section.getInt("plot-buyer-1000.quantity"), section.getInt("plot-buyer-1000.index")));
            registerEvent(plotBuyer5000 = new PlotBuyer(section.getInt("plot-buyer-5000.blocks"), section.getInt("plot-buyer-5000.price.required-points"), section.getInt("plot-buyer-5000.quantity"), section.getInt("plot-buyer-5000.index")));
        }

        CustomItem.getItems().forEach(i -> itemManager.registerItem(i.getName(), i::getItem));
        itemManager.registerItem("mending-Infinity-bow", () -> new ItemBuilder(Material.BOW).addEnchantment(Enchantment.ARROW_INFINITE, 1).addEnchantment(Enchantment.MENDING, 1).build());

        SlimeHandler.setupOffers(getConfig());
        claimShopHandler.addItemsToShop();

        AdvancementHandler.register(this, this::registerEvent);

        try {
            this.discordBot = new DiscordBot();
        } catch (Exception e) {
            getLogger().warning("Failed to init Discord bot: %s".formatted(e));
        }
    }

    @Override
    public void onDisable() {
        ScoreboardUI.getScoreboards().forEach(ScoreboardUI::delete);

        Bukkit.getWorlds().forEach(world -> world.getEntitiesByClass(Shulker.class).stream()
                .filter(shulker -> shulker.getScoreboardTags().stream().anyMatch(s -> s.contains("marker1") || s.contains("marker2")))
                .forEach(Shulker::remove)
        );

        ClaimCommand.claimingPlayers.values().forEach(ClaimInfo::stopTasks);
        ClaimCommand.claimingPlayers.clear();

        this.discordBot.shutdown();
    }

    public static TextComponent getPrefix() {
        return Component.empty().append(Component.text("[", NamedTextColor.DARK_GRAY)
                .append(Component.text("Hard", TextColor.color(0x88D657)))
                .append(Component.text("SMP", TextColor.color(0xF6ED82)))
                .append(Component.text("] ", NamedTextColor.DARK_GRAY))
        ).color(NamedTextColor.GRAY);
    }

    private void registerEvent(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    private PluginCommand registerCommand(String name, CommandExecutor executor) {
        PluginCommand command = getCommand(name);
        if (command != null) command.setExecutor(executor);
        return command;
    }

}
