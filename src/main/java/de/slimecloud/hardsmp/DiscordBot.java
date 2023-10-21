package de.slimecloud.hardsmp;

import de.mineking.discord.DiscordUtils;
import de.mineking.discord.commands.ContextBase;
import de.mineking.discord.commands.ContextCreator;
import de.slimecloud.hardsmp.verify.DiscordVerifyCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.util.EnumSet;

public class DiscordBot {
    public final DiscordUtils discordUtils;
    public final JDA jdaInstance;

    public DiscordBot() {
        var builder = JDABuilder.createDefault(HardSMP.getInstance().getConfig().getString("discord.token"))
                .setActivity(Activity.playing( "auf " + HardSMP.getInstance().getServer().getIp()))

                .enableIntents(EnumSet.allOf(GatewayIntent.class))
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setEventPassthrough(true);

        discordUtils = setupDiscordUtils(builder);
        jdaInstance = discordUtils.build();
    }

    private DiscordUtils setupDiscordUtils(JDABuilder builder) {
        return new DiscordUtils("", builder)
                .useEventManager(null)
                .useUIManager(null)
                .useCommandManager(new ContextCreator<>(ContextBase.class, event -> new ContextBase()), config -> {
                    config.registerCommand(DiscordVerifyCommand.class);
                });
    }
}
