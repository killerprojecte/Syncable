package dev.rgbmc.syncable.synchronizers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.rgbmc.syncable.client.synchronizers.Synchronizer;
import dev.rgbmc.syncable.utils.ItemSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;

public class InventorySynchronizer extends Synchronizer {
  @Override
  public void deserialize(UUID playerId, String data) {
    Player player = Bukkit.getPlayer(playerId);
    PlayerInventory inventory = player.getInventory();
    for (int i = 0; i < inventory.getSize(); i++) {
      inventory.setItem(i, new ItemStack(Material.AIR));
    }
    JsonObject jsonObject = JsonParser.parseString(data).getAsJsonObject();
    for (String key : jsonObject.keySet()) {
      inventory.setItem(
          Integer.parseInt(key), ItemSerializer.deserialize(jsonObject.get(key).getAsString()));
    }
  }

  @Override
  public String serialize(UUID playerId) {
    Player player = Bukkit.getPlayer(playerId);
    JsonObject jsonObject = new JsonObject();
    PlayerInventory inventory = player.getInventory();
    for (int i = 0; i < inventory.getSize(); i++) {
      jsonObject.addProperty(String.valueOf(i), ItemSerializer.serialize(inventory.getItem(i)));
    }
    return new Gson().toJson(jsonObject);
  }
}
