package dev.rgbmc.syncable.schedulers;

public abstract class SyncableScheduler {
  public abstract void runTask(Runnable runnable);
}
