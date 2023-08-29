package dev.rgbmc.syncable.schedulers;

import dev.rgbmc.syncable.client.schedulers.SyncableScheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class FoliaScheduler extends SyncableScheduler {
    private final Plugin plugin;

    public FoliaScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void runTask(Runnable runnable) {
        Bukkit.getGlobalRegionScheduler().run(plugin, scheduledTask -> runnable.run());
    }
}
