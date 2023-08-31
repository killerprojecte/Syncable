package dev.rgbmc.syncable.synchronizers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.rgbmc.syncable.SyncableForge;
import dev.rgbmc.syncable.client.synchronizers.Synchronizer;
import dev.rgbmc.syncable.objects.PotionData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.Map;
import java.util.UUID;

public class EffectSynchronizer extends Synchronizer {
    @Override
    public void deserialize(UUID playerId, String data) {
        MinecraftServer server = SyncableForge.getServer();
        ServerPlayer player = server.getPlayerList().getPlayer(playerId);
        JsonObject jsonObject = JsonParser.parseString(data).getAsJsonObject();
        for (Map.Entry<MobEffect, MobEffectInstance> effectEntry :
                player.getActiveEffectsMap().entrySet()) {
            server.executeBlocking(
                    () -> {
                        player.removeEffect(effectEntry.getKey());
                    });
        }
        for (String key : jsonObject.keySet()) {
            MobEffect statusEffect = null;
            for (MobEffect effect : BuiltInRegistries.MOB_EFFECT) {
                if (BuiltInRegistries.MOB_EFFECT.getKey(effect).getPath().toUpperCase().equalsIgnoreCase(key)) {
                    statusEffect = effect;
                    break;
                }
            }
            if (statusEffect == null) continue;
            PotionData potionData = new Gson().fromJson(jsonObject.get(key), PotionData.class);
            MobEffectInstance effect =
                    new MobEffectInstance(
                            statusEffect,
                            potionData.getDuration(),
                            potionData.getAmplifier(),
                            potionData.isAmbient(),
                            potionData.hasParticles(),
                            potionData.hasIcon());
            server.executeBlocking(
                    () -> {
                        player.addEffect(effect);
                    });
        }
    }

    @Override
    public String serialize(UUID playerId) {
        MinecraftServer server = SyncableForge.getServer();
        ServerPlayer player = server.getPlayerList().getPlayer(playerId);
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<MobEffect, MobEffectInstance> effectEntry :
                player.getActiveEffectsMap().entrySet()) {
            String fieldName =
                    BuiltInRegistries.MOB_EFFECT.getKey(effectEntry.getKey()).getPath().toUpperCase();
            PotionData potionData = new PotionData(effectEntry.getValue());
            jsonObject.add(fieldName, new Gson().toJsonTree(potionData));
        }
        return new Gson().toJson(jsonObject);
    }
}
