package dev.dipper.corpseChest.command;

import dev.dipper.corpseChest.CorpseChest;
import dev.dipper.corpseChest.menu.InvessMenu;
import dev.nexisMenu.gui.GuiManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InvViewCommand implements CommandExecutor {
    private final GuiManager guiM;
    private final CorpseChest plugin;

    public InvViewCommand(GuiManager guiM, CorpseChest plugin) {
        this.guiM = guiM;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command Command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only player can run this command.");
            return true;
        }

        if (!player.hasPermission("corpsechest.invsee")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /invsee <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(target.getName() + " is not online");
            return true;
        }

        if (target == player) {
            player.sendMessage(ChatColor.RED + "Can't open your own inventory.");
            return true;
        }

        if (target.getInventory().isEmpty()) {
            player.sendMessage(ChatColor.RED + target.getName() + " Inventory is empty.");
            return true;
        }

        guiM.openMenuandLoad(player, new InvessMenu(plugin, target));
        return true;
    }
}
