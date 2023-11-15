package de.slimecloud.hardsmp;

import de.mineking.discordutils.DiscordUtils;
import de.mineking.discordutils.commands.context.ContextBase;
import de.slimecloud.hardsmp.verify.DiscordVerifyCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import java.util.EnumSet;

public class DiscordBot extends ListenerAdapter {
    public final DiscordUtils<DiscordBot> discordUtils;
    public final JDA jdaInstance;

    private final DiscordAppender consoleMirror;

    public DiscordBot() {
        jdaInstance = JDABuilder.createDefault(HardSMP.getInstance().getConfig().getString("discord.token"))
                .setActivity(Activity.playing("auf " + HardSMP.getInstance().getServer().getIp()))

                .enableIntents(EnumSet.allOf(GatewayIntent.class))
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setEventPassthrough(true)

                .addEventListeners(this)
                .build();

        discordUtils = setupDiscordUtils();

        if(HardSMP.getInstance().getConfig().contains("discord.console-channel")) {
            ((Logger) LogManager.getRootLogger()).addAppender(consoleMirror = new DiscordAppender("discord", message ->
                    HardSMP.getInstance().getDiscordBot().jdaInstance.getChannelById(MessageChannel.class, HardSMP.getInstance().getConfig().getLong("discord.console-channel"))
                            .sendMessage(message)
                            .queue()
            ));
        } else consoleMirror = null;
    }

    public void shutdown() {
        jdaInstance.shutdownNow();
        if(consoleMirror != null) {
            ((Logger) LogManager.getRootLogger()).removeAppender(consoleMirror);
            consoleMirror.stop();
        }
    }

    private DiscordUtils<DiscordBot> setupDiscordUtils() {
        return new DiscordUtils<>(jdaInstance, this)
                .useCommandManager(
                        e -> new ContextBase<>(e) {},
                        e -> new ContextBase<>(e) {},
                        cmdMan -> {
                            cmdMan.updateCommands();
                            cmdMan.registerCommand(DiscordVerifyCommand.class);
                        }
                );
    }
}
