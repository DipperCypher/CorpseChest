package dev.dipper.corpseChest.block;

import java.util.UUID;

public class BlockData {
    private UUID uuid;
    private String name;
    private BlockKey key;
    private long creatTime;
    private BlockInventory inventory;

    public BlockData(UUID uuid, String name, BlockKey key, long creatTime, BlockInventory inventory) {
        this.uuid = uuid;
        this.name = name;
        this.key = key;
        this.creatTime = creatTime;
        this.inventory = inventory;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
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