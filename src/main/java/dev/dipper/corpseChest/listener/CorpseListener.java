package dev.dipper.corpseChest.listener;

import dev.dipper.corpseChest.block.BlockInventory;
import dev.dipper.corpseChest.block.BlockData;
import dev.dipper.corpseChest.block.BlockKey;
import dev.dipper.corpseChest.manager.CorpseManager;
import org.bukkit.*;
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

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CorpseListener implements Listener {
    private CorpseManager corpseS;
    private final Set<UUID> activeCorpseView = new HashSet<>();

    public CorpseListener(CorpseManager corpseS) {
        this.corpseS = corpseS;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().isEmpty()) return;

        BlockInventory full = corpseS.fullSave(player);
        Location loc = player.getLocation().getBlock().getLocation();
        BlockKey key = corpseS.key(loc);

        UUID uuid = UUID.randomUUID();
        BlockData data = new BlockData(
                uuid,
                player.getUniqueId(),
                key,
                System.currentTimeMillis(),
                full
        );

        corpseS.add(data, key);
        event.getDrops().clear();

        player.sendMessage(ChatColor.AQUA + "You died! Your items were stored in a corpse at your death location: "
                + ChatColor.WHITE + "X: " + loc.getBlockX() + " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ());

        loc.getBlock().setType(corpseS.getChestBlock());
        loc.add(0, 1, 0);

        if (loc.getBlock().getType() != Material.AIR) return;
        loc.getBlock().setType(corpseS.getChestBlock());
    }

    @EventHandler
    public void onChestOpen(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        Location loc = block.getLocation();
        BlockKey key = corpseS.key(loc);
        BlockData data = corpseS.get(key);

        if (data == null) return;
        if (player.getGameMode() != GameMode.SURVIVAL) return;
        if (!data.getOwner().equals(player.getUniqueId())) return;

        event.setCancelled(true);
        Inventory inv = Bukkit.createInventory(
                null,
                54,
                "Corpse: " + data.getUuid()
        );

        ItemStack[] items = data.getInventory().getContents();
        ItemStack[] gui = new ItemStack[54];

        for (int i = 0; i < items.length && i < 54; i++) {
            gui[i] = items[i];
        }

        inv.setContents(gui);
        player.openInventory(inv);
        activeCorpseView.add(player.getUniqueId());
        player.playSound(player, Sound.ENTITY_SKELETON_DEATH, 1, 1);
    }

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if (!activeCorpseView.contains(player.getUniqueId())) return;
        activeCorpseView.remove(player.getUniqueId());

        String title = event.getView().getTitle();
        UUID titleUUID = getTitle(title);
        if (titleUUID == null) return;

        BlockData data =  corpseS.getFromUUID(titleUUID);
        if (data == null) return;

        Location dropLoc = player.getLocation();

        for (ItemStack item : event.getInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR) continue;
            dropLoc.getWorld().dropItemNaturally(dropLoc, item);
        }

        event.getInventory().clear();
        BlockKey key = data.getKey();

        World world = Bukkit.getWorld(key.world());
        if (world == null) return;

        Location location = new Location(
                world,
                key.x(),
                key.y(),
                key.z()
        );

       location.getBlock().setType(Material.AIR);
       location.add(0, 1, 0);

       if (location.getBlock().getType() != corpseS.getChestBlock()) return;
       location.getBlock().setType(Material.AIR);
        corpseS.remove(data, key);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        BlockKey key = corpseS.key(block.getLocation());

        if (player.getGameMode() == GameMode.CREATIVE) return;
        if (block.getType() != corpseS.getChestBlock()) return;

        UUID uuid = corpseS.getChestlookup().get(key);
        if (uuid == null) return;
        event.setCancelled(true);
    }

    private UUID getTitle(String title) {
        if (title == null) return null;

        if (!title.startsWith("Corpse: ")) return null;

        try {
            return UUID.fromString(title.replace("Corpse: ", ""));
        } catch (Exception e) {
            return null;
        }
    }
}