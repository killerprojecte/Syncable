package dev.rgbmc.syncable.synchronizers;

import com.google.gson.Gson;
import dev.rgbmc.syncable.client.synchronizers.Synchronizer;
import dev.rgbmc.syncable.objects.AdvancementsData;
import dev.rgbmc.syncable.utils.AdvancementUtils;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AdvancementSynchronizer extends Synchronizer {
  @Override
  public void deserialize(UUID playerId, String data) {
    Player player = Bukkit.getPlayer(playerId);
    AdvancementsData advancementsData = new Gson().fromJson(data, AdvancementsData.class);
    AdvancementUtils.setAdvancements(player, advancementsData.getAdvancements());
  }

  @Override
  public String serialize(UUID playerId) {
    Player player = Bukkit.getPlayer(playerId);
    AdvancementsData advancementsData =
        new AdvancementsData(AdvancementUtils.getAdvancements(player));
    return new Gson().toJson(advancementsData);
  }
}
