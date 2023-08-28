package dev.rgbmc.syncable.synchronizers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.rgbmc.syncable.SyncableFabric;
import dev.rgbmc.syncable.client.synchronizers.Synchronizer;
import dev.rgbmc.syncable.utils.ItemSerializer;
import java.util.UUID;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class EnderChestSynchronizer extends Synchronizer {
  @Override
  public void deserialize(UUID playerId, String data) {
    MinecraftServer server = SyncableFabric.getServer();
    ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerId);
    EnderChestInventory enderChest = player.getEnderChestInventory();
    for (int i = 0; i < enderChest.size(); i++) {
      enderChest.setStack(i, ItemStack.EMPTY);
    }
    JsonObject jsonObject = JsonParser.parseString(data).getAsJsonObject();
    for (String key : jsonObject.keySet()) {
      enderChest.setStack(
          Integer.parseInt(key), ItemSerializer.deserialize(jsonObject.get(key).getAsString()));
    }
  }

  @Override
  public String serialize(UUID playerId) {
    MinecraftServer server = SyncableFabric.getServer();
    ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerId);
    JsonObject jsonObject = new JsonObject();
    EnderChestInventory enderChest = player.getEnderChestInventory();
    for (int i = 0; i < enderChest.size(); i++) {
      jsonObject.addProperty(String.valueOf(i), ItemSerializer.serialize(enderChest.getStack(i)));
    }
    return new Gson().toJson(jsonObject);
  }
}
