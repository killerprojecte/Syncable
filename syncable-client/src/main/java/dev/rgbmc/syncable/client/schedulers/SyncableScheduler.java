package dev.rgbmc.syncable.client.schedulers;

public abstract class SyncableScheduler {
    public abstract void runTask(Runnable runnable);
}
