package dev.dipper.corpseChest.block;

import org.bukkit.inventory.ItemStack;

public class BlockInventory {
    private final ItemStack[] contents;
    private final ItemStack[] armor;
    private final ItemStack offhand;

    public BlockInventory(ItemStack[] contents, ItemStack[] armor, ItemStack offhand) {
        this.contents = contents;
        this.armor = armor;
        this.offhand = offhand;
    }

    public ItemStack[] getContents() {
        return contents;
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public ItemStack getOffhand() {
        return offhand;
    }
}