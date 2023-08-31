package dev.rgbmc.syncable.utils;

import dev.rgbmc.syncable.SyncableForge;
import dev.rgbmc.syncable.client.handlers.WriteHandler;
import net.minecraft.server.level.ServerPlayer;

public class SyncUtils {
    public static void write(ServerPlayer player) {
        SyncableForge.getSyncableClient().sendCommand(new WriteHandler(player.getUUID()));
    }
}
