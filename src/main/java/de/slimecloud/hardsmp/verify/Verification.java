package de.slimecloud.hardsmp.verify;

import de.slimecloud.hardsmp.database.DataClass;
import de.slimecloud.hardsmp.database.Key;
import de.slimecloud.hardsmp.database.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.UserSnowflake;

import java.util.Map;

@Table(name = "verification")
@Getter
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

    public static Verification load(UserSnowflake user) {
        return DataClass.load(() -> new Verification(null), Map.of("discordID", user.getIdLong()))
                .orElseGet(() -> new Verification(null));
    }

    public Verification setDiscordId(long id) {
        verified = true;
        discordID = id;

        return this;
    }
    public Verification setVerified(Boolean value) {
        verified = value;

        return this;
    }
}
