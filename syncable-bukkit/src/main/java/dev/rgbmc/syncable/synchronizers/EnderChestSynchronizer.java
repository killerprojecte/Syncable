package dev.rgbmc.syncable.synchronizers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.rgbmc.syncable.client.synchronizers.Synchronizer;
import dev.rgbmc.syncable.utils.ItemSerializer;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EnderChestSynchronizer extends Synchronizer {
  @Override
  public void deserialize(UUID playerId, String data) {
    Player player = Bukkit.getPlayer(playerId);
    Inventory enderChest = player.getEnderChest();
    for (int i = 0; i < enderChest.getSize(); i++) {
      enderChest.setItem(i, new ItemStack(Material.AIR));
    }
    JsonObject jsonObject = JsonParser.parseString(data).getAsJsonObject();
    for (String key : jsonObject.keySet()) {
      enderChest.setItem(
          Integer.parseInt(key), ItemSerializer.deserialize(jsonObject.get(key).getAsString()));
    }
  }

  @Override
  public String serialize(UUID playerId) {
    Player player = Bukkit.getPlayer(playerId);
    JsonObject jsonObject = new JsonObject();
    Inventory enderChest = player.getEnderChest();
    for (int i = 0; i < enderChest.getSize(); i++) {
      jsonObject.addProperty(String.valueOf(i), ItemSerializer.serialize(enderChest.getItem(i)));
    }
    return new Gson().toJson(jsonObject);
  }
}
