package me.rayzr522.ocmfixer;

import me.rayzr522.lib.comphenix.attribute.NbtFactory;
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

        NbtFactory.NbtCompound root = NbtFactory.fromItemTag(item);
        NbtFactory.NbtList attributes = root.getList("AttributeModifiers", false);

        // Dirty check to prevent extra work...
        int origSize = attributes.size();

        attributes.removeIf(tag -> {
            if (tag instanceof NbtFactory.NbtCompound) {
                NbtFactory.NbtCompound compound = (NbtFactory.NbtCompound) tag;
                String attributeName = compound.getString("AttributeName", null);
                return "generic.armor".equals(attributeName) || "generic.armorToughness".equals(attributeName);
            }
            return false;
        });

        // ... so we know if they removed any elements.
        if (attributes.size() != origSize) {
            root.put("AttributeModifiers", attributes);
            NbtFactory.setItemTag(item, root);
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
