package dev.rgbmc.syncable.synchronizers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.rgbmc.syncable.client.synchronizers.Synchronizer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SaturationSynchronizer extends Synchronizer {
    @Override
    public void deserialize(UUID playerId, String data) {
        Player player = Bukkit.getPlayer(playerId);
        JsonObject jsonObject = JsonParser.parseString(data).getAsJsonObject();
        player.setSaturation(jsonObject.get("saturation").getAsFloat());
        player.setExhaustion(jsonObject.get("exhaustion").getAsFloat());
    }

    @Override
    public String serialize(UUID playerId) {
        Player player = Bukkit.getPlayer(playerId);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("saturation", player.getSaturation());
        jsonObject.addProperty("exhaustion", player.getExhaustion());
        return new Gson().toJson(jsonObject);
    }
}
