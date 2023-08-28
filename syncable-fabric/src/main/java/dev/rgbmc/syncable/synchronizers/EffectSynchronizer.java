package dev.rgbmc.syncable.synchronizers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.rgbmc.syncable.SyncableFabric;
import dev.rgbmc.syncable.client.synchronizers.Synchronizer;
import dev.rgbmc.syncable.objects.PotionData;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

public class EffectSynchronizer extends Synchronizer {
  @Override
  public void deserialize(UUID playerId, String data) {
    MinecraftServer server = SyncableFabric.getServer();
    ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerId);
    JsonObject jsonObject = JsonParser.parseString(data).getAsJsonObject();
    for (Map.Entry<StatusEffect, StatusEffectInstance> effectEntry :
        player.getActiveStatusEffects().entrySet()) {
      server.executeSync(
          () -> {
            player.removeStatusEffect(effectEntry.getKey());
          });
    }
    for (String key : jsonObject.keySet()) {
      StatusEffect statusEffect = null;
      for (Field field : StatusEffects.class.getDeclaredFields()) {
        if (field.getName().equalsIgnoreCase(key)) {
          try {
            statusEffect = (StatusEffect) field.get(null);
          } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
          }
        }
      }
      if (statusEffect == null) continue;
      PotionData potionData = new Gson().fromJson(jsonObject.get(key), PotionData.class);
      StatusEffectInstance effect =
          new StatusEffectInstance(
              statusEffect,
              potionData.getDuration(),
              potionData.getAmplifier(),
              potionData.isAmbient(),
              potionData.hasParticles(),
              potionData.hasIcon());
      server.executeSync(
          () -> {
            player.addStatusEffect(effect);
          });
    }
  }

  @Override
  public String serialize(UUID playerId) {
    MinecraftServer server = SyncableFabric.getServer();
    ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerId);
    JsonObject jsonObject = new JsonObject();
    for (Map.Entry<StatusEffect, StatusEffectInstance> effectEntry :
        player.getActiveStatusEffects().entrySet()) {
      String fieldName = null;
      for (Field field : StatusEffects.class.getDeclaredFields()) {
        try {
          if (field.get(null).equals(effectEntry.getKey())) {
            fieldName = field.getName();
          }
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
      PotionData potionData = new PotionData(effectEntry.getValue());
      jsonObject.add(fieldName, new Gson().toJsonTree(potionData));
    }
    return new Gson().toJson(jsonObject);
  }
}
