package me.rayzr522.ocmfixer;

import me.ialistannen.mininbt.NBTWrappers;
import me.ialistannen.mininbt.NBTWrappers.NBTTagCompound;
import me.ialistannen.mininbt.NBTWrappers.NBTTagDouble;
import me.ialistannen.mininbt.NBTWrappers.NBTTagInt;
import me.ialistannen.mininbt.NBTWrappers.NBTTagList;
import me.ialistannen.mininbt.StreamNBTUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Objects;
import java.util.Optional;

public class FixWorldCommand implements CommandExecutor {

    private OCMFixerPlugin plugin;

    FixWorldCommand(OCMFixerPlugin plugin) {
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
        for (File file : Objects.requireNonNull(folder.listFiles())) {

            state.reset();

            log("Attempting conversion of file '" + file.getName() + "'");

            try {
                NBTTagCompound nbt = read(file);
                assert nbt != null;

                NBTTagList list = (NBTTagList) nbt.get("Attributes");
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
                    nbt.set("Attributes", list);
                    log("Saving modified data...");

                    StreamNBTUtil.writeToStream(nbt, new FileOutputStream(file));

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

    private void resetAttribute(ScrewYouLambdas state, NBTTagList list, String attributeName, double defaultValue) {
        findByName(list, attributeName).ifPresent(attribute -> {
            if (attribute.hasKey("Modifiers")) {
                log("Removing modifiers for %s", attributeName);
                attribute.remove("Modifiers");

                state.markDirty();
            }

            if (getNumber(attribute) == defaultValue) {
                return;
            }

            log("Resetting %s to default value (%f)", attributeName, defaultValue);

            list.remove(attribute);
            attribute.setDouble("Base", defaultValue);
            list.add(attribute);

            state.markDirty();
        });
    }

    private double getNumber(NBTTagCompound tag) {
        Object value = tag.get("Base");
        if (value instanceof NBTTagDouble) {
            return ((NBTTagDouble) value).getAsDouble();
        } else if (value instanceof NBTTagInt) {
            return ((NBTTagInt) value).getAsInt();
        } else {
            return 0.0;
        }
    }

    private Optional<NBTTagCompound> findByName(NBTTagList list, String name) {
        return list.getList().stream()
                .filter(NBTTagCompound.class::isInstance)
                .map(NBTTagCompound.class::cast)
                .filter(it -> name.equals(it.getString("Name")))
                .findFirst();
    }

    private void tell(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&8\u00bb %s", message)));
    }

    private void log(String msg, Object... args) {
        plugin.getLogger().info(String.format(msg, args));
    }

    private NBTTagCompound read(File file) {
        try {
            return StreamNBTUtil.fromStream(new FileInputStream(file));
        } catch (Exception e) {
            System.err.println("Failed to read NBT file: " + file.getPath());
            e.printStackTrace();
        }
        return null;
    }

    private class ScrewYouLambdas {
        private boolean dirty = false;

        void markDirty() {
            dirty = true;
        }

        boolean isDirty() {
            return dirty;
        }

        void reset() {
            dirty = false;
        }
    }

}
