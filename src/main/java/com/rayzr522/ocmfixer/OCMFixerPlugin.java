
package com.rayzr522.ocmfixer;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

public class OCMFixerPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("read").setExecutor(new ReadCommand(this));
    }

    /**
     * @param name the name of the world folder
     * @return
     */
    public File getWorldFolder(String name) {
        return new File(getDataFolder().getParentFile().getParentFile(), name);
    }

}
