package de.slimecloud.hardsmp.verify;

import de.slimecloud.hardsmp.database.DataClass;
import de.slimecloud.hardsmp.database.Key;
import de.slimecloud.hardsmp.database.Table;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Table(name = "verification")
@RequiredArgsConstructor
@Setter
public class VerifyData extends DataClass {

    @Key
    private final UUID minecraftID;
    private long discordID;
    private boolean verified;

}
