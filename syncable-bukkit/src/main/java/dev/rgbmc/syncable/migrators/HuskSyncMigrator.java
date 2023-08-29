package dev.rgbmc.syncable.migrators;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.rgbmc.syncable.SyncableBukkit;
import dev.rgbmc.syncable.client.migrators.MigrateWriteHandler;
import dev.rgbmc.syncable.client.migrators.Migrator;
import dev.rgbmc.syncable.objects.AdvancementsData;
import dev.rgbmc.syncable.utils.ItemSerializer;
import net.william278.husksync.api.HuskSyncAPI;
import net.william278.husksync.data.AdvancementData;
import net.william278.husksync.data.BukkitInventoryMap;
import net.william278.husksync.data.StatisticsData;
import net.william278.husksync.data.UserData;
import net.william278.husksync.player.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

public class HuskSyncMigrator extends Migrator {
    @Override
    public void migrate() {
        HuskSyncAPI api = HuskSyncAPI.getInstance();
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            JsonObject jsonObject = new JsonObject();
            api.getUser(player.getUniqueId()).thenAcceptAsync(optionalUser -> {
                if (!optionalUser.isPresent()) return;
                User user = optionalUser.get();
                api.getPlayerInventory(user).thenAcceptAsync(optionalBukkitInventoryMap -> {
                    // Migrate Inventory
                    if (optionalBukkitInventoryMap.isPresent()) {
                        JsonObject inventoryJson = new JsonObject();
                        BukkitInventoryMap inventoryMap = optionalBukkitInventoryMap.get();
                        for (int i = 0; i < inventoryMap.getSize(); i++) {
                            Optional<ItemStack> optionalItem = inventoryMap.getItemAt(i);
                            if (optionalItem.isPresent()) {
                                inventoryJson.addProperty(
                                        String.valueOf(i),
                                        ItemSerializer.serialize(optionalItem.get()));
                            } else {
                                inventoryJson.addProperty(
                                        String.valueOf(i),
                                        ItemSerializer.serialize(new ItemStack(Material.AIR)));
                            }
                        }
                        jsonObject.addProperty("inventory", Base64.getEncoder().encodeToString(new Gson().toJson(inventoryJson).getBytes(StandardCharsets.UTF_8)));
                    }
                    api.getPlayerEnderChest(user).thenAcceptAsync(optionalItemStacks -> {
                        // Migrate EnderChest
                        if (optionalItemStacks.isPresent()) {
                            JsonObject enderChestJson = new JsonObject();
                            ItemStack[] itemStacks = optionalItemStacks.get();
                            for (int i = 0; i < itemStacks.length; i++) {
                                enderChestJson.addProperty(
                                        String.valueOf(i),
                                        ItemSerializer.serialize(itemStacks[i]));
                            }
                            jsonObject.addProperty(
                                    "enderChest", Base64.getEncoder().encodeToString(new Gson().toJson(enderChestJson).getBytes(StandardCharsets.UTF_8)));
                        }
                        api.getUserData(user).thenAcceptAsync(optionalUserData -> {
                            if (optionalUserData.isPresent()) {
                                UserData userData = optionalUserData.get();
                                // Migrate Statistics
                                Optional<StatisticsData> optionalStatisticsData = userData.getStatistics();
                                if (optionalStatisticsData.isPresent()) {
                                    StatisticsData statisticsData = optionalStatisticsData.get();
                                    JsonObject statisticObject = new JsonObject();
                                    statisticsData.untypedStatistics.forEach(statisticObject::addProperty);
                                    statisticsData.blockStatistics.forEach((string, stringIntegerMap) -> {
                                        stringIntegerMap.forEach((string1, integer) -> statisticObject.addProperty(string + ":" + string1.toUpperCase(), integer));
                                    });
                                    statisticsData.itemStatistics.forEach((string, stringIntegerMap) -> {
                                        stringIntegerMap.forEach((string1, integer) -> statisticObject.addProperty(string + ":" + string1.toUpperCase(), integer));
                                    });
                                    statisticsData.entityStatistics.forEach((string, stringIntegerMap) -> {
                                        stringIntegerMap.forEach((string1, integer) -> statisticObject.addProperty(string + ":" + EntityType.valueOf(string1).getName().toUpperCase(), integer));
                                    });
                                    jsonObject.addProperty("statistic", Base64.getEncoder().encodeToString(new Gson().toJson(statisticObject).getBytes(StandardCharsets.UTF_8)));
                                }
                                Optional<List<AdvancementData>> optionalAdvancementDataList = userData.getAdvancements();
                                if (optionalAdvancementDataList.isPresent()) {
                                    List<AdvancementData> advancementDataList = optionalAdvancementDataList.get();
                                    List<dev.rgbmc.syncable.objects.AdvancementData> syncableAdvancementList = new ArrayList<>();
                                    advancementDataList.forEach(advancementData -> {
                                        dev.rgbmc.syncable.objects.AdvancementData syncableAdvancement = new dev.rgbmc.syncable.objects.AdvancementData(advancementData.key, advancementData.completedCriteria);
                                        syncableAdvancementList.add(syncableAdvancement);
                                    });
                                    jsonObject.addProperty("advancement", Base64.getEncoder().encodeToString(new Gson().toJson(new AdvancementsData(syncableAdvancementList)).getBytes(StandardCharsets.UTF_8)));
                                }
                                SyncableBukkit.getSyncableClient().sendCommand(new MigrateWriteHandler(player.getUniqueId(), jsonObject));
                                SyncableBukkit.instance.getLogger().info("Migrated player data: " + player.getUniqueId() + " from HuskSync");
                            }
                        });
                    });
                });
            });
        }
    }
}
