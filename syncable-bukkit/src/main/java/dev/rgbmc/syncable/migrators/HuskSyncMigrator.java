package dev.rgbmc.syncable.migrators;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.rgbmc.syncable.SyncableBukkit;
import dev.rgbmc.syncable.client.migrators.MigrateWriteHandler;
import dev.rgbmc.syncable.client.migrators.Migrator;
import dev.rgbmc.syncable.utils.ItemSerializer;
import java.util.Optional;
import net.william278.husksync.api.HuskSyncAPI;
import net.william278.husksync.data.BukkitInventoryMap;
import net.william278.husksync.player.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public class HuskSyncMigrator extends Migrator {
  @Override
  public void migrate() {
    HuskSyncAPI api = HuskSyncAPI.getInstance();
    for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
      JsonObject jsonObject = new JsonObject();
      api.getUser(player.getUniqueId())
          .thenAcceptAsync(
              optionalUser -> {
                if (!optionalUser.isPresent()) return;
                User user = optionalUser.get();
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
                                        .sendCommand(
                                            new MigrateWriteHandler(
                                                player.getUniqueId(), jsonObject));
                                  });
                        });
              });
    }
  }
}
