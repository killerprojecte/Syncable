package dev.rgbmc.syncable.client.synchronizers;

import com.google.gson.JsonObject;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SynchronizerManager {
  private static final Map<String, Synchronizer> synchronizers = new HashMap<>();

  public static Map<String, Synchronizer> getSynchronizers() {
    return synchronizers;
  }

  public static void register(String id, Synchronizer synchronizer) {
    synchronizers.put(id, synchronizer);
  }

  public static void deserialize(UUID playerId, JsonObject jsonObject) {
    for (String key : jsonObject.keySet()) {
      if (!synchronizers.containsKey(key)) continue;
      synchronizers
          .get(key)
          .deserialize(
              playerId,
              new String(
                  Base64.getDecoder().decode(jsonObject.get(key).getAsString()),
                  StandardCharsets.UTF_8));
    }
  }

  public static JsonObject serialize(UUID playerId) {
    JsonObject jsonObject = new JsonObject();
    for (String key : synchronizers.keySet()) {
      jsonObject.addProperty(
          key,
          Base64.getEncoder()
              .encodeToString(
                  synchronizers.get(key).serialize(playerId).getBytes(StandardCharsets.UTF_8)));
    }
    return jsonObject;
  }
}
