package de.slimecloud.hardsmp.claim;

import de.slimecloud.hardsmp.database.DataClass;
import de.slimecloud.hardsmp.database.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

@Table(name = "claimrights")
@Getter
@Setter
@RequiredArgsConstructor
public class ClaimRights extends DataClass {

    private final String uuid;

    private int claimCount;
    private int totalClaimSize;

    public static ClaimRights load(UUID uuid) {
        Supplier <ClaimRights> s = () -> new ClaimRights(uuid.toString());
        return load(s, Map.of("uuid", uuid.toString())).orElse(null);
    }

}
