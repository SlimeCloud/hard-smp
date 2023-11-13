package de.slimecloud.hardsmp.subevent.replika;

import de.slimecloud.hardsmp.build.Build;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Replika {

    @Getter
    private final File directory;
    @Getter
    private final Set<Build> schematics;


    public Replika(Plugin plugin) {
        this.directory = new File(plugin.getDataFolder(), "replika/schematics");
        this.schematics = new HashSet<>();
        directory.mkdirs();
        if (directory.listFiles()!=null) {
            Arrays.stream(directory.listFiles())
                    .filter(f -> f.getName().endsWith(".build"))
                    .forEach(f -> {
                        try {
                            schematics.add(Build.load(f));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    public void registerSchematic(Build build) {
        schematics.add(build);
    }

    public File getFile(String name) {
        return new File(directory, name + ".build");
    }


}
