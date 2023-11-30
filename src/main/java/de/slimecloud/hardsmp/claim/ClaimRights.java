package de.slimecloud.hardsmp.claim;

import de.slimecloud.hardsmp.database.DataClass;
import de.slimecloud.hardsmp.database.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Table(name = "claimrights")
@Getter
@Setter
@RequiredArgsConstructor
public class ClaimRights extends DataClass {

    private final String uuid;

    private int claimCount = 0;
    private int totalClaimSize = 0;

    public static ClaimRights load(UUID uuid) {
        return load(() -> new ClaimRights(uuid.toString()), Map.of("uuid", uuid.toString()))
                .orElse(new ClaimRights(uuid.toString()));
    }

}
