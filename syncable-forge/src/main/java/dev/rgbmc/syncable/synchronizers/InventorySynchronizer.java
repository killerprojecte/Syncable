package dev.rgbmc.syncable.synchronizers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.rgbmc.syncable.SyncableForge;
import dev.rgbmc.syncable.client.synchronizers.Synchronizer;
import dev.rgbmc.syncable.utils.ItemSerializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class InventorySynchronizer extends Synchronizer {
    @Override
    public void deserialize(UUID playerId, String data) {
        MinecraftServer server = SyncableForge.getServer();
        ServerPlayer player = server.getPlayerList().getPlayer(playerId);
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            inventory.setItem(i, ItemStack.EMPTY);
        }
        JsonObject jsonObject = JsonParser.parseString(data).getAsJsonObject();
        for (String key : jsonObject.keySet()) {
            inventory.setItem(
                    Integer.parseInt(key), ItemSerializer.deserialize(jsonObject.get(key).getAsString()));
        }
    }

    @Override
    public String serialize(UUID playerId) {
        JsonObject jsonObject = new JsonObject();
        MinecraftServer server = SyncableForge.getServer();
        ServerPlayer player = server.getPlayerList().getPlayer(playerId);
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            jsonObject.addProperty(String.valueOf(i), ItemSerializer.serialize(inventory.getItem(i)));
        }
        return new Gson().toJson(jsonObject);
    }
}
