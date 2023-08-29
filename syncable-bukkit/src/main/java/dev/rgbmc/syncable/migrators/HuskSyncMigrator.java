package dev.rgbmc.syncable.migrators;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.rgbmc.syncable.SyncableBukkit;
import dev.rgbmc.syncable.client.migrators.MigrateWriteHandler;
import dev.rgbmc.syncable.client.migrators.Migrator;
import dev.rgbmc.syncable.utils.ItemSerializer;
import net.william278.husksync.BukkitHuskSync;
import net.william278.husksync.api.HuskSyncAPI;
import net.william278.husksync.config.Settings;
import net.william278.husksync.data.BukkitInventoryMap;
import net.william278.husksync.database.MySqlDatabase;
import net.william278.husksync.player.User;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class HuskSyncMigrator extends Migrator {

  private Collection<User> getAllHuskSyncUser() {
    MySqlDatabase database = (MySqlDatabase) BukkitHuskSync.getInstance().getDatabase();

    Class<?> clazz = database.getClass();
    try {
      Method method = clazz.getDeclaredMethod("getConnection");
      Connection connection = (Connection) method.invoke(database);
      String table = BukkitHuskSync.getInstance().getSettings().getTableName(Settings.TableName.USERS);

      List<User> userList = new ArrayList<>();

      try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table)) {
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
          UUID uuid = UUID.fromString(resultSet.getString("uuid"));
          String name = resultSet.getString("username");
          userList.add(new User(uuid, name));
        }
        resultSet.close();
        return userList;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void migrate() {
    HuskSyncAPI api = HuskSyncAPI.getInstance();

    for (User user : getAllHuskSyncUser()) {
      JsonObject jsonObject = new JsonObject();

      api.getPlayerInventory(user)
          .thenAcceptAsync(
              optionalBukkitInventoryMap -> {
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
                  jsonObject.addProperty("inventory", new Gson().toJson(inventoryJson));
                }
                api.getPlayerEnderChest(user)
                    .thenAcceptAsync(
                        optionalItemStacks -> {
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
                                "enderChest", new Gson().toJson(enderChestJson));
                          }
                          SyncableBukkit.getSyncableClient()
                              .sendCommand(new MigrateWriteHandler(user.uuid, jsonObject));
                        });
              });
    }

  }
}
