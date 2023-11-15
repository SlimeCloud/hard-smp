package de.slimecloud.hardsmp;

import de.mineking.discordutils.console.DiscordOutputStream;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.function.Consumer;

public class DiscordAppender extends AbstractAppender {
	public final static Map<Level, String> levelColor = Map.of(
			Level.INFO, "\033[34m",
			Level.WARN, "\033[33m",
			Level.ERROR, "\033[1;31m"
	);

	public final static SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	private final PrintStream upstream;

	protected DiscordAppender(String name, Consumer<MessageCreateData> handler) {
		super(name, null, null);

		upstream = new PrintStream(new DiscordOutputStream(handler, 5));

		start();
	}

	@Override
	public void append(LogEvent event) {
		upstream.print(dateFormat.format(new Date(event.getTimeMillis())));

		upstream.printf(" \033[1;36m%-20s\033[0m ", event.getLoggerName().substring(event.getLoggerName().length() <= 20 ? 0 : event.getLoggerName().length() - 20));
		upstream.printf("%s%-5s\033[0m ", levelColor.getOrDefault(event.getLevel(), ""), event.getLevel().name());

		upstream.println(event.getMessage().getFormattedMessage());
		if(event.getThrown() != null) upstream.println(event.getThrown());
	}

	@Override
	public void stop() {
		upstream.close();
	}
}
