package de.slimecloud.hardsmp;

import de.mineking.discord.DiscordUtils;
import de.mineking.discord.commands.ContextBase;
import de.mineking.discord.commands.ContextCreator;
import de.slimecloud.hardsmp.verify.DiscordVerifyCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.EnumSet;

public class DiscordBot extends ListenerAdapter {
    public final DiscordUtils discordUtils;
    public final JDA jdaInstance;

    public DiscordBot() {
        JDABuilder builder = JDABuilder.createDefault(HardSMP.getInstance().getConfig().getString("discord.token"))
                .setActivity(Activity.playing("auf " + HardSMP.getInstance().getServer().getIp()))

                .enableIntents(EnumSet.allOf(GatewayIntent.class))
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setEventPassthrough(true)

                .addEventListeners(this);

        discordUtils = setupDiscordUtils(builder);
        jdaInstance = discordUtils.build();
    }

    private DiscordUtils setupDiscordUtils(JDABuilder builder) {
        return new DiscordUtils("", builder)
                .useEventManager(null)
                .useUIManager(null)
                .useCommandManager(new ContextCreator<>(ContextBase.class, event -> new ContextBase()), config -> {
                    config.registerCommand(DiscordVerifyCommand.class);
                })
                .useCommandCache(null);
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        discordUtils.getCommandCache().updateGlobalCommands(null);
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        discordUtils.getCommandCache().updateGuildCommands(event.getGuild(), Collections.emptyMap(), null);
    }
}
