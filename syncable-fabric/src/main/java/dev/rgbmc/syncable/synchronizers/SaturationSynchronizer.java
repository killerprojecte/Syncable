package dev.rgbmc.syncable.synchronizers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.rgbmc.syncable.SyncableFabric;
import dev.rgbmc.syncable.client.synchronizers.Synchronizer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class SaturationSynchronizer extends Synchronizer {
    @Override
    public void deserialize(UUID playerId, String data) {
        MinecraftServer server = SyncableFabric.getServer();
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerId);
        JsonObject jsonObject = JsonParser.parseString(data).getAsJsonObject();
        player.getHungerManager().setSaturationLevel(jsonObject.get("saturation").getAsFloat());
        player.getHungerManager().setExhaustion(jsonObject.get("exhaustion").getAsFloat());
    }

    @Override
    public String serialize(UUID playerId) {
        MinecraftServer server = SyncableFabric.getServer();
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerId);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("saturation", player.getHungerManager().getSaturationLevel());
        jsonObject.addProperty("exhaustion", player.getHungerManager().getExhaustion());
        return new Gson().toJson(jsonObject);
    }
}
