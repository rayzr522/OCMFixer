package me.rayzr522.ocmfixer;

import me.ialistannen.mininbt.ItemNBTUtil;
import me.ialistannen.mininbt.NBTWrappers.NBTTagCompound;
import me.ialistannen.mininbt.NBTWrappers.NBTTagList;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ArmorFixListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        cleanInventory(e.getPlayer().getInventory());
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        cleanInventory(e.getInventory());
    }

    private void cleanInventory(Inventory inv) {
        if (inv == null) {
            return;
        }


        ItemStack[] items = inv.getContents();
        inv.setContents(cleanItems(items));
    }

    private ItemStack[] cleanItems(ItemStack[] items) {
        ItemStack[] output = new ItemStack[items.length];

        for (int i = 0; i < items.length; i++) {
            output[i] = removeAttributes(items[i]);
        }

        return output;
    }

    private ItemStack removeAttributes(ItemStack item) {
        if (item == null) {
            return null;
        }

        if (!isArmor(item)) {
            return item;
        }

        NBTTagCompound root = ItemNBTUtil.getTag(item);
        NBTTagList attributes = (NBTTagList) root.get("AttributeModifiers");

        if (attributes == null) {
            return item;
        }

        // Dirty check to prevent extra work...
        int origSize = attributes.size();

        attributes.getRawList().removeIf(tag -> {
            if (tag instanceof NBTTagCompound) {
                NBTTagCompound compound = (NBTTagCompound) tag;
                String attributeName = compound.getString("AttributeName");
                return "generic.armor".equals(attributeName) || "generic.armorToughness".equals(attributeName);
            }
            return false;
        });

        // ... so we know if they removed any elements.
        if (attributes.size() != origSize) {
            if (attributes.size() == 0) {
                root.remove("AttributeModifiers");
            } else {
                root.set("AttributeModifiers", attributes);
            }
            return ItemNBTUtil.setNBTTag(root, item);
        }

        return item;
    }

    private boolean isArmor(ItemStack itemStack) {
        String type = itemStack.getType().name();

        return type.endsWith("_HELMET")
                || type.endsWith("_CHESTPLATE")
                || type.endsWith("_LEGGINGS")
                || type.endsWith("_BOOTS");
    }
}
