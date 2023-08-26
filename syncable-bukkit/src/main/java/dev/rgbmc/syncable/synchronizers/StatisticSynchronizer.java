package dev.rgbmc.syncable.synchronizers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.rgbmc.syncable.SyncableBukkit;
import dev.rgbmc.syncable.client.synchronizers.Synchronizer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.UUID;

public class StatisticSynchronizer extends Synchronizer {
  @Override
  public void deserialize(UUID playerId, String data) {
    Player player = Bukkit.getPlayer(playerId);
    JsonObject jsonObject = JsonParser.parseString(data).getAsJsonObject();
    for (String str : jsonObject.keySet()) {
      String[] param = str.split(":");
      Statistic statistic = Statistic.valueOf(param[0].toUpperCase());
      switch (statistic.getType()) {
        case ENTITY -> player.setStatistic(
            statistic, EntityType.valueOf(param[1]), jsonObject.get(str).getAsInt());
        case BLOCK, ITEM -> player.setStatistic(
            statistic, Material.valueOf(param[1]), jsonObject.get(str).getAsInt());
        default -> player.setStatistic(statistic, jsonObject.get(str).getAsInt());
      }
    }
  }

  @Override
  public String serialize(UUID playerId) {
    Player player = Bukkit.getPlayer(playerId);
    JsonObject jsonObject = new JsonObject();
    for (Statistic statistic : Statistic.values()) {
      switch (statistic.getType()) {
        case ENTITY -> {
          for (EntityType entityType : EntityType.values()) {
            if (entityType.equals(EntityType.UNKNOWN)) continue;
            jsonObject.addProperty(
                statistic.name() + ":" + entityType.name(),
                player.getStatistic(statistic, entityType));
          }
        }
        case BLOCK, ITEM -> {
          for (Material material : Material.values()) {
            jsonObject.addProperty(
                statistic.name() + ":" + material.name(), player.getStatistic(statistic, material));
          }
        }
        default -> {
          jsonObject.addProperty(statistic.name(), player.getStatistic(statistic));
        }
      }
    }
    return new Gson().toJson(jsonObject);
  }
}
