package dev.dipper.corpseChest;

import dev.dipper.corpseChest.listener.CorpseListener;
import dev.dipper.corpseChest.manager.CorpseManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class CorpseChest extends JavaPlugin {

    @Override
    public void onEnable() {
        CorpseManager corpseManager = new CorpseManager(this);
        getServer().getPluginManager().registerEvents(new CorpseListener(corpseManager), this);
    }
}
