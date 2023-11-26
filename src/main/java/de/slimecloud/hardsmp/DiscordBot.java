package de.slimecloud.hardsmp;

import de.mineking.discordutils.DiscordUtils;
import de.slimecloud.hardsmp.info.DiscordInfoCommand;
import de.slimecloud.hardsmp.verify.DiscordVerifyCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class DiscordBot extends ListenerAdapter {
    public final DiscordUtils<DiscordBot> discordUtils;
    public final JDA jdaInstance;

    private DiscordAppender consoleMirror;

    public DiscordBot() {
        jdaInstance = JDABuilder.createDefault(HardSMP.getInstance().getConfig().getString("discord.token"))
                .setActivity(Activity.playing("auf " + HardSMP.getInstance().getServer().getIp()))

                .enableIntents(EnumSet.allOf(GatewayIntent.class))
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setEventPassthrough(true)

                .addEventListeners(this)
                .build();

        discordUtils = setupDiscordUtils();
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        if (HardSMP.getInstance().getConfig().contains("discord.console-channel")) {
            ((Logger) LogManager.getRootLogger()).addAppender(consoleMirror = new DiscordAppender(
                    "discord",
                    jdaInstance,
                    HardSMP.getInstance().getConfig().getLong("discord.console-channel")
            ));
        } else consoleMirror = null;
    }

    public void shutdown() {
        jdaInstance.shutdownNow();
        if (consoleMirror != null) {
            ((Logger) LogManager.getRootLogger()).removeAppender(consoleMirror);
            consoleMirror.stop();
        }
    }

    private DiscordUtils<DiscordBot> setupDiscordUtils() {
        return DiscordUtils.create(jdaInstance, this)
                .useCommandManager(
                        e -> () -> e,
                        e -> () -> e,
                        cmdMan -> {
                            cmdMan.registerCommand(DiscordInfoCommand.class);
                            cmdMan.registerCommand(DiscordInfoCommand.UserInfoCommand.class);

                            cmdMan.registerCommand(DiscordVerifyCommand.class);

                            cmdMan.updateCommands();
                        }
                ).build();
    }
}
