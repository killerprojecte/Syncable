package dev.rgbmc.syncable.synchronizers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.rgbmc.syncable.SyncableForge;
import dev.rgbmc.syncable.client.synchronizers.Synchronizer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class SaturationSynchronizer extends Synchronizer {
    @Override
    public void deserialize(UUID playerId, String data) {
        MinecraftServer server = SyncableForge.getServer();
        ServerPlayer player = server.getPlayerList().getPlayer(playerId);
        JsonObject jsonObject = JsonParser.parseString(data).getAsJsonObject();
        player.getFoodData().setSaturation(jsonObject.get("saturation").getAsFloat());
        player.getFoodData().setExhaustion(jsonObject.get("exhaustion").getAsFloat());
    }

    @Override
    public String serialize(UUID playerId) {
        MinecraftServer server = SyncableForge.getServer();
        ServerPlayer player = server.getPlayerList().getPlayer(playerId);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("saturation", player.getFoodData().getSaturationLevel());
        jsonObject.addProperty("exhaustion", player.getFoodData().getExhaustionLevel());
        return new Gson().toJson(jsonObject);
    }
}
