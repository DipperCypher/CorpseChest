package dev.dipper.corpseChest.listener;

import dev.dipper.corpseChest.CorpseChest;
import dev.dipper.corpseChest.block.BlockInventory;
import dev.dipper.corpseChest.block.BlockData;
import dev.dipper.corpseChest.block.BlockKey;
import dev.dipper.corpseChest.manager.CorpseManager;
import dev.dipper.corpseChest.menu.ChestMenu;
import dev.nexisMenu.gui.GuiManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

public class CorpseListener implements Listener {
    private final CorpseChest plugin;
    private final CorpseManager corpseS;
    private final GuiManager guiM;

    public CorpseListener(CorpseChest plugin, CorpseManager corpseS, GuiManager guiM) {
        this.plugin = plugin;
        this.corpseS = corpseS;
        this.guiM = guiM;
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
        ChestMenu menu = new ChestMenu(plugin, corpseS, data);
        guiM.openMenuandLoad(player, menu);
        player.playSound(player, Sound.ENTITY_SKELETON_DEATH, 1, 1);
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
}