package de.slimecloud.hardsmp.verify;

import de.slimecloud.hardsmp.database.DataClass;
import de.slimecloud.hardsmp.database.Key;
import de.slimecloud.hardsmp.database.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Table(name = "verification")
@RequiredArgsConstructor
public class Verification extends DataClass {
    @Key
    private final String minecraftID;

    @Getter
    private long discordID;

    private boolean verified = false;

    public static Verification load(String minecraftID) {
        return DataClass.load(() -> new Verification(minecraftID), Map.of("minecraftID", minecraftID))
                .orElseGet(() -> new Verification(minecraftID));
    }

    public Verification setDiscordId(long id) {
        verified = true;
        discordID = id;

        return this;
    }
}
