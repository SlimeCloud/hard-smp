package de.slimecloud.hardsmp;

import de.mineking.discordutils.console.DiscordOutputStream;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

public class DiscordAppender extends AbstractAppender {
    public final static Map<Level, String> levelColor = Map.of(
            Level.INFO, "\033[34m",
            Level.WARN, "\033[33m",
            Level.ERROR, "\033[1;31m"
    );

    public final static SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private final JDA jda;
    private final long channel;
    private final PrintStream upstream;

    private Instant lastError = Instant.now();

    protected DiscordAppender(String name, JDA jda, long channel) {
        super(name, null, null);

        this.jda = jda;
        this.channel = channel;

        upstream = new PrintStream(new DiscordOutputStream(message ->
                jda.getChannelById(MessageChannel.class, channel)
                        .sendMessage(message)
                        .queue(),
                5
        ));

        start();
    }

    @Override
    public void append(LogEvent event) {
        if (event.getLevel() == Level.ERROR && lastError.plus(Duration.ofMinutes(1)).isBefore(Instant.now()))
            jda.getChannelById(MessageChannel.class, channel)
                    .sendMessage("<@&" + HardSMP.getInstance().getConfig().getLong("discord.netrunner-role") + ">")
                    .queue();

        lastError = Instant.now();

        upstream.print(dateFormat.format(new Date(event.getTimeMillis())));

        upstream.printf(" \033[1;36m%-20s\033[0m ", event.getLoggerName().substring(event.getLoggerName().length() <= 20 ? 0 : event.getLoggerName().length() - 20));
        upstream.printf("%s%-5s\033[0m ", levelColor.getOrDefault(event.getLevel(), ""), event.getLevel().name());

        upstream.println(event.getMessage().getFormattedMessage());
        if (event.getThrown() != null) upstream.println(event.getThrown());
    }

    @Override
    public void stop() {
        upstream.close();
    }
}
