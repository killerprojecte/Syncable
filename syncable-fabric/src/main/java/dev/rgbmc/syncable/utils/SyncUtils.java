package dev.rgbmc.syncable.utils;

import dev.rgbmc.syncable.SyncableFabric;
import dev.rgbmc.syncable.client.handlers.WriteHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class SyncUtils {
  public static void write(ServerPlayerEntity player) {
    SyncableFabric.getSyncableClient().sendCommand(new WriteHandler(player.getUuid()));
  }
}
