package de.slimecloud.hardsmp.database;

import de.slimecloud.hardsmp.Main;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import java.util.function.Consumer;
import java.util.function.Function;

public class Database {

	public Jdbi jdbi;

	public Database(String host, String user, String password) {
		if (host==null || user==null || password==null) return;

		jdbi = Jdbi.create("jdbc:postgresql://" + host, user, password);
	}

	public void run(Consumer<Handle> handler) {
		if (jdbi==null) {
			Main.getInstance().getLogger().warning("Versuchter Datenbankaufruf nicht möglich: Keine Datenbank konfiguriert");
			return;
		}
		jdbi.useHandle(handler::accept);
	}

	public <T> T handle(Function<Handle, T> handler) {
		if (jdbi==null) {
			Main.getInstance().getLogger().warning("Versuchter Datenbankaufruf nicht möglich: Keine Datenbank konfiguriert");
			return null;
		}
		return jdbi.withHandle(handler::apply);
	}

}
