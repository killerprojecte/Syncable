package dev.rgbmc.syncable.synchronizers;

import com.google.gson.Gson;
import dev.rgbmc.syncable.SyncableFabric;
import dev.rgbmc.syncable.client.synchronizers.Synchronizer;
import dev.rgbmc.syncable.objects.AdvancementsData;
import dev.rgbmc.syncable.utils.AdvancementUtils;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class AdvancementSynchronizer extends Synchronizer {
  @Override
  public void deserialize(UUID playerId, String data) {
    MinecraftServer server = SyncableFabric.getServer();
    ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerId);
    AdvancementsData advancementsData = new Gson().fromJson(data, AdvancementsData.class);
    AdvancementUtils.setAdvancements(player, advancementsData.getAdvancements());
  }

  @Override
  public String serialize(UUID playerId) {
    MinecraftServer server = SyncableFabric.getServer();
    ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerId);
    AdvancementsData advancementsData =
        new AdvancementsData(AdvancementUtils.getAdvancements(player));
    return new Gson().toJson(advancementsData);
  }
}
