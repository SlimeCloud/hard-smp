package de.slimecloud.hardsmp.verify;

import de.slimecloud.hardsmp.database.DataClass;
import de.slimecloud.hardsmp.database.Key;
import de.slimecloud.hardsmp.database.Table;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Table(name = "verification")
@RequiredArgsConstructor
@Setter
public class VerifyData extends DataClass {

    @Key
    private final String minecraftID;
    private long discordID;
    private boolean verified = false;

    public static VerifyData load(String minecraftID) {
        return DataClass.load(
                () -> new VerifyData(minecraftID), Map.of("minecraftID", minecraftID)).orElseGet(
                        () -> new VerifyData(minecraftID)
        );
    }

}
