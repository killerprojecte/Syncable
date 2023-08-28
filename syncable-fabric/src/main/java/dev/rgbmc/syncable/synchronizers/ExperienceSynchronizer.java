package dev.rgbmc.syncable.synchronizers;

import dev.rgbmc.syncable.SyncableFabric;
import dev.rgbmc.syncable.client.synchronizers.Synchronizer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class ExperienceSynchronizer extends Synchronizer {
  @Override
  public void deserialize(UUID playerId, String data) {
    MinecraftServer server = SyncableFabric.getServer();
    ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerId);
    server.executeSync(
        () -> {
          player.totalExperience = Integer.parseInt(data);
          server.getPlayerManager().sendPlayerStatus(player);
        });
  }

  @Override
  public String serialize(UUID playerId) {
    MinecraftServer server = SyncableFabric.getServer();
    ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerId);
    return String.valueOf(player.totalExperience);
  }
}
