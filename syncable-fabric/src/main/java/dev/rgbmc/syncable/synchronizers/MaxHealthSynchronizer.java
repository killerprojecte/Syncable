package dev.rgbmc.syncable.synchronizers;

import dev.rgbmc.syncable.SyncableFabric;
import dev.rgbmc.syncable.client.synchronizers.Synchronizer;
import java.util.UUID;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class MaxHealthSynchronizer extends Synchronizer {
  @Override
  public void deserialize(UUID playerId, String data) {
    MinecraftServer server = SyncableFabric.getServer();
    ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerId);
    server.executeSync(
        () -> {
          player
              .getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)
              .setBaseValue(Double.parseDouble(data));
        });
  }

  @Override
  public String serialize(UUID playerId) {
    MinecraftServer server = SyncableFabric.getServer();
    ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerId);
    return String.valueOf((double) player.getMaxHealth());
  }
}
