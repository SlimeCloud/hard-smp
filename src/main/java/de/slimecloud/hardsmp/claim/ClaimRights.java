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

    public transient Map<Integer, Integer> bought;

    private final String uuid;

    private int claimCount = 0;
    private int totalClaimSize = 0;
    private int totalClaimed = 0;

    private int bought25 = 0;
    private int bought100 = 0;
    private int bought500 = 0;
    private int bought1000 = 0;
    private int bought5000 = 0;

    public static ClaimRights load(UUID uuid) {
        return load(() -> new ClaimRights(uuid.toString()), Map.of("uuid", uuid.toString()))
                .orElse(new ClaimRights(uuid.toString()));
    }

    public void buy(int blocks) {
        bought.put(blocks, bought.get(blocks) == null ? 0 : bought.get(blocks) + 1);
        refreshMap();
        totalClaimSize = bought25 * 25 + bought100 * 100 + bought500 * 500 + bought1000 * 1000 + bought5000 * 5000;
        save();
    }

    private void refreshMap() {
        bought25 = bought.get(25);
        bought100 = bought.get(100);
        bought500 = bought.get(500);
        bought1000 = bought.get(1000);
        bought5000 = bought.get(5000);
    }

}
