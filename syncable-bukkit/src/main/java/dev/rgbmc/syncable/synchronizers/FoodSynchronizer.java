package dev.rgbmc.syncable.synchronizers;

import dev.rgbmc.syncable.SyncableBukkit;
import dev.rgbmc.syncable.client.synchronizers.Synchronizer;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FoodSynchronizer extends Synchronizer {
  @Override
  public void deserialize(UUID playerId, String data) {
    Player player = Bukkit.getPlayer(playerId);
    SyncableBukkit.getScheduler()
        .runTask(
            () -> {
              player.setFoodLevel(Integer.parseInt(data));
            });
  }

  @Override
  public String serialize(UUID playerId) {
    Player player = Bukkit.getPlayer(playerId);
    return String.valueOf(player.getFoodLevel());
  }
}
