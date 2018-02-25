package me.rayzr522.ocmfixer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class OCMFixerPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("read").setExecutor(new ReadCommand(this));
    }

    public File getWorldFolder(String name) {
        return new File(Bukkit.getWorldContainer(), name);
    }

}
