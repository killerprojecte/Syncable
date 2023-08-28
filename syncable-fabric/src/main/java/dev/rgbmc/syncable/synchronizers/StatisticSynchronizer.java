package dev.rgbmc.syncable.synchronizers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.rgbmc.syncable.SyncableFabric;
import dev.rgbmc.syncable.client.synchronizers.Synchronizer;
import dev.rgbmc.syncable.objects.BukkitStatistic;
import dev.rgbmc.syncable.objects.StatMapping;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.StatHandler;

public class StatisticSynchronizer extends Synchronizer {
  private static final Map<String, Item> itemMap =
      new HashMap<>() {
        {
          for (Block block : Registries.BLOCK) {
            put(Registries.BLOCK.getId(block).getPath().toUpperCase(), block.asItem());
          }
          for (Item item : Registries.ITEM) {
            put(Registries.ITEM.getId(item).getPath().toUpperCase(), item);
          }
        }
      };
  private static final Map<String, EntityType<?>> entityTypes =
      new HashMap<>() {
        {
          for (EntityType<?> entityType : Registries.ENTITY_TYPE) {
            put(EntityType.getId(entityType).getPath().toUpperCase(), entityType);
          }
        }
      };

  @Override
  public void deserialize(UUID playerId, String data) {
    MinecraftServer server = SyncableFabric.getServer();
    ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerId);
    JsonObject jsonObject = JsonParser.parseString(data).getAsJsonObject();
    for (String str : jsonObject.keySet()) {
      String[] param = str.split(":");
      BukkitStatistic statistic = BukkitStatistic.valueOf(param[0].toUpperCase());
      StatHandler statHandler = player.getStatHandler();
      switch (statistic.getType()) {
        case ENTITY -> {
          statHandler.setStat(
              player,
              StatMapping.getEntityStatistic(statistic, entityTypes.get(param[1])),
              jsonObject.get(str).getAsInt());
        }
        case BLOCK, ITEM -> {
          statHandler.setStat(
              player,
              StatMapping.getMaterialStatistic(statistic, itemMap.get(param[1])),
              jsonObject.get(str).getAsInt());
        }
        default -> statHandler.setStat(
            player, StatMapping.getNMSStatistic(statistic), jsonObject.get(str).getAsInt());
      }
    }
  }

  @Override
  public String serialize(UUID playerId) {
    MinecraftServer server = SyncableFabric.getServer();
    ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerId);
    JsonObject jsonObject = new JsonObject();
    StatHandler statHandler = player.getStatHandler();
    for (BukkitStatistic statistic : BukkitStatistic.values()) {
      switch (statistic.getType()) {
        case ENTITY -> {
          for (Map.Entry<String, EntityType<?>> entityType : entityTypes.entrySet()) {
            jsonObject.addProperty(
                statistic.name() + ":" + entityType.getKey(),
                statHandler.getStat(
                    StatMapping.getEntityStatistic(statistic, entityType.getValue())));
          }
        }
        case BLOCK, ITEM -> {
          for (Map.Entry<String, Item> material : itemMap.entrySet()) {
            jsonObject.addProperty(
                statistic.name() + ":" + material.getKey(),
                statHandler.getStat(
                    StatMapping.getMaterialStatistic(statistic, material.getValue())));
          }
        }
        default -> {
          jsonObject.addProperty(
              statistic.name(), statHandler.getStat(StatMapping.getNMSStatistic(statistic)));
        }
      }
    }
    return new Gson().toJson(jsonObject);
  }
}
