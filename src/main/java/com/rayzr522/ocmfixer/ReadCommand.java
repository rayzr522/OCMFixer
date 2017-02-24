
package com.rayzr522.ocmfixer;

import java.io.File;
import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.comphenix.attribute.NbtFactory;
import com.comphenix.attribute.NbtFactory.NbtCompound;
import com.comphenix.attribute.NbtFactory.NbtList;
import com.comphenix.attribute.NbtFactory.StreamOptions;
import com.google.common.io.Files;

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

        if (!folder.exists() || !folder.isDirectory()) {
            tell(p, "&cThat world could not be found, or it does not contain a playerdata folder.");
            return true;
        }

        // Because I can't set variables from inside of a lambda. YAY!
        ScrewYouLambdas state = new ScrewYouLambdas();

        log("Beginning conversion process on world " + args[0]);
        for (File file : folder.listFiles()) {

            state.reset();

            log("Attempting conversion of file '" + file.getName() + "'");

            try {
                NbtCompound nbt = NbtReader.read(file);

                NbtList list = nbt.getList("Attributes", false);
                if (list == null) {
                    continue;
                }
                log("Found attributes list");

                // Reset attack speed
                findByName(list, "generic.attackSpeed").ifPresent(attribute -> {
                    if (attribute.getDouble("Base", 0.0) == 4.0) {
                        return;
                    }

                    log("Resetting attack speed to default (4.0)");

                    list.remove(attribute);
                    attribute.putPath("Base", 4.0);
                    list.add(attribute);

                    state.markDirty();
                });

                // Reset attack damage
                findByName(list, "generic.attackDamage").ifPresent(attribute -> {
                    if (attribute.getDouble("Base", 0.0) == 2.0) {
                        return;
                    }

                    log("Resetting attack damage to default (2.0)");

                    list.remove(attribute);
                    attribute.putPath("Base", 2.0);
                    list.add(attribute);

                    state.markDirty();
                });

                if (state.isDirty()) {
                    nbt.putPath("Attributes", list);
                    log("Saving modified data...");

                    NbtFactory.saveStream(nbt, Files.asByteSink(file), StreamOptions.GZIP_COMPRESSION);

                    log("Done. Data reset for '" + file.getName() + "'");
                } else {
                    log("'" + file.getName() + "' had nothing to fix");
                }

            } catch (Exception e) {
                e.printStackTrace();
                tell(p, "&cFailed to load file: &a" + file.getName());
            }

        }

        tell(p, "&aPlayer data files have been fixed for '" + args[0] + "'");

        return true;
    }

    private Optional<NbtCompound> findByName(NbtList list, String name) {
        return list.stream()
                .filter(it -> it instanceof NbtCompound)
                .map(it -> (NbtCompound) it)
                .filter(it -> it.getString("Name", "ERR").equals(name))
                .findFirst();
    }

    private void tell(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&8\u00bb %s", message)));
    }

    private void log(String msg) {
        plugin.getLogger().info(msg);
    }

    private class ScrewYouLambdas {
        private boolean dirty = false;

        public void markDirty() {
            dirty = true;
        }

        public boolean isDirty() {
            return dirty;
        }

        public void reset() {
            dirty = false;
        }
    }

}
