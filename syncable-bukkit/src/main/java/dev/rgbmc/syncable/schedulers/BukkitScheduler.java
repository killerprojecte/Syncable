package dev.rgbmc.syncable.schedulers;

import dev.rgbmc.syncable.SyncableBukkit;
import dev.rgbmc.syncable.client.schedulers.SyncableScheduler;
import org.bukkit.Bukkit;

public class BukkitScheduler extends SyncableScheduler {
    @Override
    public void runTask(Runnable runnable) {
        Bukkit.getScheduler().runTask(SyncableBukkit.instance, runnable);
    }
}
