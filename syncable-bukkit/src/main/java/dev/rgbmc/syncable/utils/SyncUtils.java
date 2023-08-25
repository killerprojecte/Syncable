package dev.rgbmc.syncable.utils;

import dev.rgbmc.syncable.SyncableBukkit;
import dev.rgbmc.syncable.client.handlers.WriteHandler;
import org.bukkit.entity.Player;

public class SyncUtils {
  public static void write(Player player) {
    SyncableBukkit.getSyncableClient().sendCommand(new WriteHandler(player.getUniqueId()));
  }
}
