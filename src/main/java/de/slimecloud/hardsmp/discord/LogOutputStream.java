package de.slimecloud.hardsmp.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;

public class LogOutputStream extends OutputStream {

	private final StringBuilder sb = new StringBuilder();


	@Override
	public void write(int b) throws IOException {
		sb.append(new String(new byte[] {(byte) b}));
	}

	@Override
	public void write(@NotNull byte[] b) throws IOException {
		super.write(b);
	}

	@Override
	public void write(@NotNull byte[] b, int off, int len) throws IOException {
		super.write(b, off, len);
	}

	@Override
	public void flush() {
		DiscordBot.log(new EmbedBuilder()
				.setDescription(sb)
				.setColor(new Color(20, 150, 20))
				.build());
		sb.setLength(0);
	}
}
