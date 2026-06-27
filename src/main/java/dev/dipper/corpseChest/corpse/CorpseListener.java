package dev.dipper.corpseChest.corpse;

import dev.dipper.corpseChest.CorpseChest;
import dev.dipper.corpseChest.block.BlockInventory;
import dev.dipper.corpseChest.block.BlockKey;
import dev.dipper.corpseChest.block.BlockChest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CorpseListener implements Listener {
    private CorpseChest plugin;
    private final Map<BlockKey, BlockChest> deathChest = new HashMap<>();
    private final Map<UUID, BlockKey> openChest = new HashMap<>();
    private final Material chestBlock = Material.RESPAWN_ANCHOR;

    public CorpseListener(CorpseChest plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        BlockInventory full = fullSave(player);

        Location loc = player.getLocation().getBlock().getLocation();
        loc.getBlock().setType(chestBlock);

        BlockKey key = key(loc);
        BlockChest death = new BlockChest(player.getUniqueId(), full);

        deathChest.put(key, death);
        event.getDrops().clear();

        player.sendMessage(ChatColor.AQUA + "You died at: " + key + " Your items are safe until retrieve.");

        plugin.getLogger().info("CHEST CREATED AT: " + key);
        plugin.getLogger().info("Player: " + player.getName());
        plugin.getLogger().info("UUID: " + player.getUniqueId());

        plugin.getLogger().info("Location: "
                + loc.getWorld().getName() + " "
                + loc.getBlockX() + ", "
                + loc.getBlockY() + ", "
                + loc.getBlockZ());

        plugin.getLogger().info("Items saved: " + full.getContents().length);
        plugin.getLogger().info("Armor slots: " + full.getArmor().length);
        plugin.getLogger().info("Offhand saved: " + (full.getOffhand() != null));

        plugin.getLogger().info("Corpse placed successfully");
    }

    @EventHandler
    public void onChestOpen(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (block.getType() != chestBlock) return;
        BlockKey key = key(block.getLocation());

        BlockChest chest = deathChest.get(key);
        if (chest == null) return;

        if (!chest.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are not the owner of this chest");
            plugin.getLogger().info("Ownership check FAILED");
            return;
        }

        event.setCancelled(true);
        Inventory stuff = Bukkit.createInventory(
                null,
                54,
                "Corpse of " + player.getName()
        );

        BlockInventory inv = chest.getInventory();
        ItemStack[] items = inv.getContents();
        ItemStack[] gui = new ItemStack[54];

        for (int i = 0; i < items.length && i < 54; i++) {
            gui[i] = items[i];
        }

        plugin.getLogger().info("Inventory contents size: " + inv.getContents().length);
        plugin.getLogger().info("Opening corpse GUI, at: " + key);

        stuff.setContents(gui);
        openChest.put(player.getUniqueId(), key);

        deathChest.remove(key, chest);
        player.openInventory(stuff);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (block.getType() != chestBlock) return;
        BlockKey key = key(block.getLocation());

        if (deathChest.containsKey(key)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        BlockKey key = openChest.remove(player.getUniqueId());
        if (key == null) return;

        Location loc = new Location(
                Bukkit.getWorld(key.world()),
                key.x(),
                key.y(),
                key.z()
        );

        loc.getBlock().setType(Material.AIR);
        plugin.getLogger().info("Removed corpse at " + key);
    }

    private BlockInventory fullSave(Player player) {
        return new BlockInventory(
                player.getInventory().getContents(),
                player.getInventory().getArmorContents(),
                player.getInventory().getItemInOffHand()
        );
    }

    private BlockKey key(Location loc) {
        return new BlockKey(
                loc.getWorld().getName(),
                loc.getBlockX(),
                loc.getBlockY(),
                loc.getBlockZ()
        );
    }
}