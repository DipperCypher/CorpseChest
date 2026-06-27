package dev.dipper.corpseChest;

import dev.dipper.corpseChest.corpse.CorpseListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class CorpseChest extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new CorpseListener(this), this);
    }
}
