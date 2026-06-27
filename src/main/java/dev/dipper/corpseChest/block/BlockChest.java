package dev.dipper.corpseChest.block;

import java.util.UUID;

public class BlockChest {
    private final UUID owner;
    private final BlockInventory inventory;

    public BlockChest(UUID owner, BlockInventory inventory) {
        this.owner = owner;
        this.inventory = inventory;
    }

    public UUID getOwner() {
        return owner;
    }

    public BlockInventory getInventory() {
        return inventory;
    }
}