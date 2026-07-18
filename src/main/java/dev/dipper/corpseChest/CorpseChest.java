package dev.dipper.corpseChest;

import dev.dipper.corpseChest.command.InvViewCommand;
import dev.dipper.corpseChest.command.ViewCommand;
import dev.dipper.corpseChest.listener.CorpseListener;
import dev.dipper.corpseChest.manager.CorpseManager;
import dev.nexisMenu.gui.GuiListener;
import dev.nexisMenu.gui.GuiManager;
import dev.nexisMenu.menu.PaginatedMenu;
import org.bukkit.plugin.java.JavaPlugin;

public final class CorpseChest extends JavaPlugin {
    private GuiManager guiManager;

    @Override
    public void onEnable() {
        guiManager = new GuiManager();
        CorpseManager corpseManager = new CorpseManager(this);

        getServer().getPluginManager().registerEvents(new CorpseListener(this, corpseManager, guiManager), this);
        getServer().getPluginManager().registerEvents(new GuiListener(guiManager), this);

        getCommand("view").setExecutor(new ViewCommand(corpseManager, guiManager, this));
        getCommand("invview").setExecutor(new InvViewCommand(guiManager, this));
    }

    @Override
    public void onDisable() {
        if (guiManager != null) guiManager.closeAll();
        PaginatedMenu.shutdownSearchExecutor();
    }
}
