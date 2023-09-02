package dev.rgbmc.syncable.synchronizers;

import dev.rgbmc.syncable.SyncableForge;
import dev.rgbmc.syncable.client.synchronizers.Synchronizer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

import java.util.UUID;

public class GameModeSynchronizer extends Synchronizer {
    @Override
    public void deserialize(UUID playerId, String data) {
        MinecraftServer server = SyncableForge.getServer();
        ServerPlayer player = server.getPlayerList().getPlayer(playerId);
        player.setGameMode(GameType.byId(Integer.parseInt(data)));
    }

    @Override
    public String serialize(UUID playerId) {
        MinecraftServer server = SyncableForge.getServer();
        ServerPlayer player = server.getPlayerList().getPlayer(playerId);
        return String.valueOf(player.gameMode.getGameModeForPlayer().getId());
    }
}
