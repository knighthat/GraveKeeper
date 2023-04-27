package me.knighthat.gravekeeper;

import me.knighthat.gravekeeper.event.EventController;
import org.bukkit.plugin.java.JavaPlugin;

public final class GraveKeeper extends JavaPlugin {

    @Override
    public void onEnable() {

        // Register event handler to Server
        getServer().getPluginManager().registerEvents(new EventController(), this);


    }
}
