package dev.rgbmc.syncable.synchronizers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.rgbmc.syncable.SyncableForge;
import dev.rgbmc.syncable.client.synchronizers.Synchronizer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class ExperienceSynchronizer extends Synchronizer {
    @Override
    public void deserialize(UUID playerId, String data) {
        MinecraftServer server = SyncableForge.getServer();
        ServerPlayer player = server.getPlayerList().getPlayer(playerId);
        JsonObject jsonObject = JsonParser.parseString(data).getAsJsonObject();
        server.executeBlocking(
                () -> {
                    player.totalExperience = jsonObject.get("total").getAsInt();
                    player.experienceProgress = jsonObject.get("exp").getAsFloat();
                    player.setExperienceLevels(jsonObject.get("level").getAsInt());
                });
    }

    @Override
    public String serialize(UUID playerId) {
        MinecraftServer server = SyncableForge.getServer();
        ServerPlayer player = server.getPlayerList().getPlayer(playerId);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("total", player.totalExperience);
        jsonObject.addProperty("level", player.experienceLevel);
        jsonObject.addProperty("exp", player.experienceProgress);
        return new Gson().toJson(jsonObject);
    }
}
