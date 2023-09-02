package dev.rgbmc.syncable.synchronizers;

import dev.rgbmc.syncable.SyncableForge;
import dev.rgbmc.syncable.client.synchronizers.Synchronizer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class FlySynchronizer extends Synchronizer {
    @Override
    public void deserialize(UUID playerId, String data) {
        MinecraftServer server = SyncableForge.getServer();
        ServerPlayer player = server.getPlayerList().getPlayer(playerId);
        player.getAbilities().flying = Boolean.parseBoolean(data);
        player.onUpdateAbilities();
    }

    @Override
    public String serialize(UUID playerId) {
        MinecraftServer server = SyncableForge.getServer();
        ServerPlayer player = server.getPlayerList().getPlayer(playerId);
        return String.valueOf(player.getAbilities().flying);
    }
}
