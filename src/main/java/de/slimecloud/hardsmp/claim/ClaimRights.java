package de.slimecloud.hardsmp.claim;

import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.database.DataClass;
import de.slimecloud.hardsmp.database.Key;
import de.slimecloud.hardsmp.database.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

import static net.kyori.adventure.text.format.TextColor.color;

@Table(name = "claimrights")
@Getter
@Setter
@RequiredArgsConstructor
public class ClaimRights extends DataClass {

    @Key
    private final String uuid;

    private Integer claimCount = 0;
    private Integer totalClaimSize = 0;
    private Integer totalClaimed = 0;

    private Integer bought25 = 0;
    private Integer bought100 = 0;
    private Integer bought500 = 0;
    private Integer bought1000 = 0;
    private Integer bought5000 = 0;

    public static ClaimRights load(UUID uuid) {
        return load(() -> new ClaimRights(uuid.toString()), Map.of("uuid", uuid.toString()))
                .orElse(new ClaimRights(uuid.toString()));
    }

    @SneakyThrows
    public void buy(int blocks, Player player) {
        Field field = getClass().getDeclaredField("bought" + blocks);
        field.setAccessible(true);
        field.set(this, (int) field.get(this) + 1);
        totalClaimSize = (bought25 * 25 + bought100 * 100 + bought500 * 500 + bought1000 * 1000 + bought5000 * 5000);
        this.save();
        player.sendMessage(HardSMP.getPrefix()
                .append(Component.text("Du kannst jetzt ", color(0x88D657)))
                .append(Component.text("§6" + blocks))
                .append(Component.text(" zusätzliche Blöcke claimen,\n", color(0x88D657)))
                .append(Component.text("also insgesamt ", color(0x88D657)))
                .append(Component.text("§6" + totalClaimSize))
                .append(Component.text(" Blöcke!", color(0x88D657)))
        );
    }

    @SneakyThrows
    public int getBought(int blocks) {
        Field field = getClass().getDeclaredField("bought" + blocks);
        return (int) field.get(this);
    }

}
