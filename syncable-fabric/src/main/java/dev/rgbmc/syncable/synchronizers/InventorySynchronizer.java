package dev.rgbmc.syncable.synchronizers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.rgbmc.syncable.SyncableFabric;
import dev.rgbmc.syncable.client.synchronizers.Synchronizer;
import dev.rgbmc.syncable.utils.ItemSerializer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class InventorySynchronizer extends Synchronizer {
  @Override
  public void deserialize(UUID playerId, String data) {
    MinecraftServer server = SyncableFabric.getServer();
    ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerId);
    PlayerInventory inventory = player.getInventory();
    for (int i = 0; i < inventory.size(); i++) {
      inventory.setStack(i, ItemStack.EMPTY);
    }
    JsonObject jsonObject = JsonParser.parseString(data).getAsJsonObject();
    for (String key : jsonObject.keySet()) {
      inventory.setStack(
          Integer.parseInt(key), ItemSerializer.deserialize(jsonObject.get(key).getAsString()));
    }
  }

  @Override
  public String serialize(UUID playerId) {
    JsonObject jsonObject = new JsonObject();
    MinecraftServer server = SyncableFabric.getServer();
    ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerId);
    PlayerInventory inventory = player.getInventory();
    for (int i = 0; i < inventory.size(); i++) {
      jsonObject.addProperty(String.valueOf(i), ItemSerializer.serialize(inventory.getStack(i)));
    }
    return new Gson().toJson(jsonObject);
  }
}
