package de.slimecloud.hardsmp.discord;

import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.verify.DiscordVerifyCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.luckperms.api.LuckPerms;

import java.util.EnumSet;

public class DiscordBot {

    public static JDA jdaInstance;
    private static boolean discordLoggingEnabled = true;

    public DiscordBot() {
        jdaInstance = JDABuilder.createDefault(HardSMP.getInstance().getConfig().getString("discord.token"))
                .setActivity(Activity.playing( "auf " + HardSMP.getInstance().getServer().getIp()))

                .enableIntents(EnumSet.allOf(GatewayIntent.class))
                .setEventPassthrough(true)
                .setMemberCachePolicy(MemberCachePolicy.ALL)

                //Commands
                .addEventListeners(new DiscordVerifyCommand(HardSMP.getInstance(), HardSMP.getInstance().getServer().getServicesManager().load(LuckPerms.class)))


                .build();

        registerDiscordCommands();
    }

    private void registerDiscordCommands() {
        jdaInstance.updateCommands().addCommands(
                Commands.slash("verify", "Verifier deinen Minecraft Account")
                        .addOption(OptionType.STRING, "code", "Gebe den Code ein der dir im Minecraft Chat angezeigt wird", true)
        ).queue();
    }

    public static void log(MessageEmbed embed) {
        if (!discordLoggingEnabled) return;
        TextChannel channel = jdaInstance.getTextChannelById(HardSMP.getInstance().getConfig().getLong("discord.log-channel"));
        if (channel==null) {
            discordLoggingEnabled = false;
            HardSMP.getInstance().getLogger().warning("disabling discord logging because log channel was not found");
            return;
        }
        channel.sendMessageEmbeds(embed).queue();
    }
}
