
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
                resetAttribute(state, list, "generic.attackSpeed", 4.0);
                resetAttribute(state, list, "generic.attackDamage", 1.0);
                resetAttribute(state, list, "generic.movementSpeed", 0.100000001490116);
                resetAttribute(state, list, "generic.maxHealth", 20.0);
                resetAttribute(state, list, "generic.armor", 0.0);
                resetAttribute(state, list, "generic.armorToughness", 0.0);
                resetAttribute(state, list, "generic.knockbackResistance", 0.0);
                resetAttribute(state, list, "generic.luck", 0.0);

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

    private void resetAttribute(ScrewYouLambdas state, NbtList list, String attributeName, double defaultValue) {
        findByName(list, attributeName).ifPresent(attribute -> {
            if (getNumber(attribute, "Base") == defaultValue) {
                return;
            }

            log("Resetting %s to default value (%f)", attributeName, defaultValue);

            list.remove(attribute);
            attribute.putPath("Base", defaultValue);
            list.add(attribute);

            state.markDirty();
        });
    }

    private double getNumber(NbtCompound tag, String name) {
        Object value = tag.getOrDefault(name, 0.0);
        if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof Integer) {
            return (Integer) value;
        } else {
            return 0.0;
        }
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

    private void log(String msg, Object... args) {
        plugin.getLogger().info(String.format(msg, args));
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
