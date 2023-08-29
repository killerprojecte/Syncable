package dev.rgbmc.syncable.synchronizers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.rgbmc.syncable.SyncableFabric;
import dev.rgbmc.syncable.client.synchronizers.Synchronizer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class ExperienceSynchronizer extends Synchronizer {
    @Override
    public void deserialize(UUID playerId, String data) {
        MinecraftServer server = SyncableFabric.getServer();
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerId);
        JsonObject jsonObject = JsonParser.parseString(data).getAsJsonObject();
        server.executeSync(
                () -> {
                    player.totalExperience = jsonObject.get("total").getAsInt();
                    player.experienceProgress = jsonObject.get("exp").getAsFloat();
                    player.setExperienceLevel(jsonObject.get("level").getAsInt());
                });
    }

    @Override
    public String serialize(UUID playerId) {
        MinecraftServer server = SyncableFabric.getServer();
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerId);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("total", player.totalExperience);
        jsonObject.addProperty("level", player.experienceLevel);
        jsonObject.addProperty("exp", player.experienceProgress);
        return new Gson().toJson(jsonObject);
    }
}
