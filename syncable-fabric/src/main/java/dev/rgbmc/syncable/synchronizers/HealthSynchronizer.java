package dev.rgbmc.syncable.synchronizers;

import dev.rgbmc.syncable.SyncableFabric;
import dev.rgbmc.syncable.client.synchronizers.Synchronizer;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class HealthSynchronizer extends Synchronizer {
  @Override
  public void deserialize(UUID playerId, String data) {
    MinecraftServer server = SyncableFabric.getServer();
    ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerId);
    server.executeSync(
        () -> {
          player.setHealth((float) Double.parseDouble(data));
        });
  }

  @Override
  public String serialize(UUID playerId) {
    MinecraftServer server = SyncableFabric.getServer();
    ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerId);
    return String.valueOf((double) player.getHealth());
  }
}
