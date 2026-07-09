package dev.dipper.corpseChest.manager;

import dev.dipper.corpseChest.CorpseChest;
import dev.dipper.corpseChest.block.BlockData;
import dev.dipper.corpseChest.block.BlockInventory;
import dev.dipper.corpseChest.block.BlockKey;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class CorpseManager {
    private final Map<UUID, BlockData> deathchest = new HashMap<>();
    private final Map<BlockKey, UUID> chestlookup = new HashMap<>();
    private final Material chestBlock = Material.ANDESITE_WALL;
    private File file;
    private FileConfiguration config;
    private CorpseChest plugin;

    public CorpseManager(CorpseChest plugin) {
        this.plugin = plugin;
        loadConfig();
        loadDeathChest();
    }

    private List<ItemStack> safeList(String path) {
        List<ItemStack> list = (List<ItemStack>) config.getList(path);
        return list == null ? new ArrayList<>() : list;
    }

    public void loadDeathChest() {
        if (!file.exists()) return;
        deathchest.clear();

        if (!config.contains("corpses")) return;
        for (String uuidString : config.getConfigurationSection("corpses").getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidString);
                String path = "corpses." + uuidString;
                UUID ownerUUID = UUID.fromString(config.getString(path + ".owner"));

                String world = config.getString(path + ".world");
                int x = config.getInt(path + ".x");
                int y = config.getInt(path + ".y");
                int z = config.getInt(path + ".z");
                long time = config.getLong(path + ".time");

                List<ItemStack> contentsList = safeList(path + ".contents");
                List<ItemStack> armorList = safeList(path + ".armor");
                ItemStack offhand = config.getItemStack(path + ".offhand");

                ItemStack[] contents = contentsList.toArray(new ItemStack[0]);
                ItemStack[] armor = armorList.toArray(new ItemStack[0]);
                BlockInventory inventory = new BlockInventory(contents, armor, offhand);

                BlockKey key = new BlockKey(world, x, y, z);
                BlockData data = new BlockData(
                        uuid,
                        ownerUUID,
                        key,
                        time,
                        inventory
                );

                deathchest.put(uuid, data);
                chestlookup.put(key, uuid);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load corpse: " + uuidString);
                e.printStackTrace();
            }
        }

        plugin.getLogger().info("Loaded " + deathchest.size() + " corpse(s).");
    }

    public void loadConfig() {
        file = new File(plugin.getDataFolder(), "corpse_data.yml");
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();

        if (!file.exists()) {
            try {
                file.createNewFile();
                plugin.getLogger().info("Successfull created: " + file);
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to create: " + file);
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public void saveConfig() {
        try {
            for (Map.Entry<UUID, BlockData> entry : deathchest.entrySet()) {
                String path = "corpses." + entry.getKey();
                BlockData data = entry.getValue();

                config.set(path + ".owner", data.getOwner().toString());
                config.set(path + ".world", data.getKey().world());
                config.set(path + ".x", data.getKey().x());
                config.set(path + ".y", data.getKey().y());
                config.set(path + ".z", data.getKey().z());
                config.set(path + ".time", data.getCreatTime());
                config.set(path + ".contents", data.getInventory().getContents());
            }

            config.save(file);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to save: " + file);
            e.printStackTrace();
        }
    }

    public void add(BlockData data, BlockKey key) {
        UUID uuid = data.getUuid();
        deathchest.put(uuid, data);
        chestlookup.put(key, uuid);
        saveConfig();
    }

    public void remove(BlockData data, BlockKey key) {
        UUID uuid = data.getUuid();
        deathchest.remove(uuid);
        chestlookup.remove(key);
        config.set("corpses." + uuid.toString(), null);
        saveConfig();
    }

    public BlockData get(BlockKey key) {
        UUID uuid = chestlookup.get(key);
        return uuid == null ? null : deathchest.get(uuid);
    }

    public BlockInventory fullSave(Player player) {
        return new BlockInventory(
                player.getInventory().getContents(),
                player.getInventory().getArmorContents(),
                player.getInventory().getItemInOffHand()
        );
    }

    public BlockKey key(Location location) {
        return new BlockKey(
                location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
        );
    }

    public Material getChestBlock() {
        return chestBlock;
    }

    public Map<BlockKey, UUID> getChestlookup() {
        return chestlookup;
    }
}
