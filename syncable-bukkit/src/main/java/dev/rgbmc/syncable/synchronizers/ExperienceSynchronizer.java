package dev.rgbmc.syncable.synchronizers;

import dev.rgbmc.syncable.SyncableBukkit;
import dev.rgbmc.syncable.client.synchronizers.Synchronizer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ExperienceSynchronizer extends Synchronizer {
  @Override
  public void deserialize(UUID playerId, String data) {
    Player player = Bukkit.getPlayer(playerId);
    SyncableBukkit.getScheduler()
        .runTask(
            () -> {
              player.setTotalExperience(Integer.parseInt(data));
            });
  }

  @Override
  public String serialize(UUID playerId) {
    Player player = Bukkit.getPlayer(playerId);
    return String.valueOf(player.getTotalExperience());
  }
}
