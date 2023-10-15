package de.slimecloud.hardsmp.discord;

import java.util.logging.StreamHandler;

public class DiscordHandler extends StreamHandler {

	public DiscordHandler() {
		setOutputStream(new LogOutputStream());
	}

}
