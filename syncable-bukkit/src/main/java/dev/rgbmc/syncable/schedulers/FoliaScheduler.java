package dev.rgbmc.syncable.schedulers;

import dev.rgbmc.syncable.SyncableBukkit;
import org.bukkit.Bukkit;

public class FoliaScheduler extends SyncableScheduler {
  @Override
  public void runTask(Runnable runnable) {
    Bukkit.getGlobalRegionScheduler().run(SyncableBukkit.instance, scheduledTask -> runnable.run());
  }
}
