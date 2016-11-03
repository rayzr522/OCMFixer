
package com.rayzr522.ocmfixer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.comphenix.attribute.NbtFactory;
import com.comphenix.attribute.NbtFactory.NbtCompound;
import com.comphenix.attribute.NbtFactory.NbtList;
import com.comphenix.attribute.NbtFactory.StreamOptions;
import com.google.common.io.OutputSupplier;

public class ReadCommand implements CommandExecutor {

    private OCMFixerPlugin plugin;

    public ReadCommand(OCMFixerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender p, Command command, String cmd, String[] args) {

        if (args.length < 1) {
            return false;
        }

        File folder = new File(plugin.getWorldFolder(args[0]), "playerdata");

        if (!folder.exists()) {
            p.sendMessage(ChatColor.RED + "That world could not be found");
            return true;
        }

        log("Beginning conversion process on world " + args[0]);
        for (File file : folder.listFiles()) {

            log("Attempting conversion of file `" + file.getName() + "`");
            try {
                NbtCompound nbt = NbtReader.read(file);

                NbtList list = nbt.getList("Attributes", false);
                if (list == null) {
                    continue;
                }
                log("Found attributes list");

                NbtCompound attribute = list.stream()
                        .filter(it -> it instanceof NbtCompound)
                        .map(it -> (NbtCompound) it)
                        .filter(it -> it.getString("Name", "ERR").equals("generic.attackSpeed"))
                        .findFirst().orElse(null);
                if (attribute == null) {
                    continue;
                }
                log("Found attack speed attribute");

                list.remove(attribute);

                attribute.putPath("Base", 4);

                list.add(attribute);

                nbt.putPath("Attributes", list);

                log("Set attack speed to old value");

                NbtFactory.saveStream(nbt, new OutputSupplier<OutputStream>() {
                    @Override
                    public OutputStream getOutput() throws IOException {
                        return new FileOutputStream(file);
                    }
                }, StreamOptions.GZIP_COMPRESSION);

            } catch (Exception e) {
                e.printStackTrace();
                p.sendMessage(ChatColor.RED + "Failed to load file: " + ChatColor.GREEN + file.getName());
            }

        }

        p.sendMessage(ChatColor.GREEN + "PlayerData files modified");

        return true;

    }

    private void log(String msg) {
        plugin.getLogger().info(msg);
    }

}
