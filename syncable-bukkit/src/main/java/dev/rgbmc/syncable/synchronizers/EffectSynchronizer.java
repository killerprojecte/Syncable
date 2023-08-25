package dev.rgbmc.syncable.synchronizers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.rgbmc.syncable.client.synchronizers.Synchronizer;
import dev.rgbmc.syncable.objects.PotionData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class EffectSynchronizer extends Synchronizer {
    @Override
    public void deserialize(UUID playerId, String data) {
        Player player = Bukkit.getPlayer(playerId);
        JsonObject jsonObject = JsonParser.parseString(data).getAsJsonObject();
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        for (String key : jsonObject.keySet()) {
            PotionEffectType type = PotionEffectType.getByName(key.toUpperCase());
            PotionData potionData = new Gson().fromJson(jsonObject.get(key), PotionData.class)
            PotionEffect effect = new PotionEffect(type, potionData.getDuration(), potionData.getAmplifier(), potionData.isAmbient(), potionData.hasParticles(), potionData.hasIcon());
            player.addPotionEffect(effect);
        }
    }

    @Override
    public String serialize(UUID playerId) {
        Player player = Bukkit.getPlayer(playerId);
        JsonObject jsonObject = new JsonObject();
        for (PotionEffect effect : player.getActivePotionEffects()) {
            PotionData potionData = new PotionData(effect);
            jsonObject.add(effect.getType().getName(), new Gson().toJsonTree(potionData));
        }
        return new Gson().toJson(jsonObject);
    }
}
