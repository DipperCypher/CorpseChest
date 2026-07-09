package dev.dipper.corpseChest.block;

import java.util.UUID;

public class BlockData {
    private UUID uuid;
    private UUID owner;
    private BlockKey key;
    private long creatTime;
    private BlockInventory inventory;

    public BlockData(UUID uuid, UUID owner, BlockKey key, long creatTime, BlockInventory inventory) {
        this.uuid = uuid;
        this.owner = owner;
        this.key = key;
        this.creatTime = creatTime;
        this.inventory = inventory;
    }

    public UUID getUuid() {
        return uuid;
    }

    public UUID getOwner() {
        return owner;
    }

    public BlockKey getKey() {
        return key;
    }

    public long getCreatTime() {
        return creatTime;
    }

    public BlockInventory getInventory() {
        return inventory;
    }
}