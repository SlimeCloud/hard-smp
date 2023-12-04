package de.slimecloud.hardsmp.item;

import de.cyklon.spigotutils.adventure.Formatter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;

public class PlotBuyer extends CustomItem implements Listener {

    public PlotBuyer(int blockAmount, int pointsRequired, int quantity, int index) {
        super("plot-buyer-" + blockAmount, Material.IRON_HOE, index);

        builder.setDisplayName(ChatColor.RESET + "§6Grundstück " + blockAmount + " Blöcke")
                .setLore(Formatter.parseText("§äKaufe dir ein Grundstück, dass du mit §ö/claim§ä überall sichern kannst!"),
                        Formatter.parseText("§äDieses Grundstück kannst du insgesamt §ö" + quantity + "§ä mal kaufen."))
                        //ToDo pls add an indicator how often the player bought this extension already
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        add();
    }

}
