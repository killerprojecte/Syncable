package dev.rgbmc.syncable.synchronizers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.rgbmc.syncable.SyncableBukkit;
import dev.rgbmc.syncable.client.synchronizers.Synchronizer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ExperienceSynchronizer extends Synchronizer {
    @Override
    public void deserialize(UUID playerId, String data) {
        Player player = Bukkit.getPlayer(playerId);
        JsonObject jsonObject = JsonParser.parseString(data).getAsJsonObject();
        SyncableBukkit.getScheduler()
                .runTask(
                        () -> {
                            player.setTotalExperience(jsonObject.get("total").getAsInt());
                            player.setLevel(jsonObject.get("level").getAsInt());
                            player.setExp(jsonObject.get("exp").getAsFloat());
                        });
    }

    @Override
    public String serialize(UUID playerId) {
        Player player = Bukkit.getPlayer(playerId);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("total", player.getTotalExperience());
        jsonObject.addProperty("level", player.getLevel());
        jsonObject.addProperty("exp", player.getExp());
        return new Gson().toJson(jsonObject);
    }
}
