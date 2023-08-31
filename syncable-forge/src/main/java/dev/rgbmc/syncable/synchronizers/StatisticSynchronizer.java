package dev.rgbmc.syncable.synchronizers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.rgbmc.syncable.SyncableForge;
import dev.rgbmc.syncable.client.synchronizers.Synchronizer;
import dev.rgbmc.syncable.objects.BukkitStatistic;
import dev.rgbmc.syncable.objects.StatMapping;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StatisticSynchronizer extends Synchronizer {
    private static final Map<String, Item> itemMap =
            new HashMap<>() {
                {
                    for (Block block : BuiltInRegistries.BLOCK) {
                        put(BuiltInRegistries.BLOCK.getKey(block).getPath().toUpperCase(), block.asItem());
                    }
                    for (Item item : BuiltInRegistries.ITEM) {
                        put(BuiltInRegistries.ITEM.getKey(item).getPath().toUpperCase(), item);
                    }
                }
            };
    private static final Map<String, EntityType<?>> entityTypes =
            new HashMap<>() {
                {
                    for (EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
                        put(EntityType.getKey(entityType).getPath().toUpperCase(), entityType);
                    }
                }
            };

    @Override
    public void deserialize(UUID playerId, String data) {
        MinecraftServer server = SyncableForge.getServer();
        ServerPlayer player = server.getPlayerList().getPlayer(playerId);
        JsonObject jsonObject = JsonParser.parseString(data).getAsJsonObject();
        for (String str : jsonObject.keySet()) {
            String[] param = str.split(":");
            BukkitStatistic statistic = BukkitStatistic.valueOf(param[0].toUpperCase());
            ServerStatsCounter statHandler = player.getStats();
            switch (statistic.getType()) {
                case ENTITY -> {
                    statHandler.setValue(
                            player,
                            StatMapping.getEntityStatistic(statistic, entityTypes.get(param[1])),
                            jsonObject.get(str).getAsInt());
                }
                case BLOCK, ITEM -> {
                    statHandler.setValue(
                            player,
                            StatMapping.getMaterialStatistic(statistic, itemMap.get(param[1])),
                            jsonObject.get(str).getAsInt());
                }
                default -> statHandler.setValue(
                        player, StatMapping.getNMSStatistic(statistic), jsonObject.get(str).getAsInt());
            }
        }
    }

    @Override
    public String serialize(UUID playerId) {
        MinecraftServer server = SyncableForge.getServer();
        ServerPlayer player = server.getPlayerList().getPlayer(playerId);
        JsonObject jsonObject = new JsonObject();
        ServerStatsCounter statHandler = player.getStats();
        for (BukkitStatistic statistic : BukkitStatistic.values()) {
            switch (statistic.getType()) {
                case ENTITY -> {
                    for (Map.Entry<String, EntityType<?>> entityType : entityTypes.entrySet()) {
                        jsonObject.addProperty(
                                statistic.name() + ":" + entityType.getKey(),
                                statHandler.getValue(
                                        StatMapping.getEntityStatistic(statistic, entityType.getValue())));
                    }
                }
                case BLOCK, ITEM -> {
                    for (Map.Entry<String, Item> material : itemMap.entrySet()) {
                        jsonObject.addProperty(
                                statistic.name() + ":" + material.getKey(),
                                statHandler.getValue(
                                        StatMapping.getMaterialStatistic(statistic, material.getValue())));
                    }
                }
                default -> {
                    jsonObject.addProperty(
                            statistic.name(), statHandler.getValue(StatMapping.getNMSStatistic(statistic)));
                }
            }
        }
        return new Gson().toJson(jsonObject);
    }
}
