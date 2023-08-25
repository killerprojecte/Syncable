package dev.rgbmc.syncable.client.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.rgbmc.syncable.client.synchronizers.SynchronizerManager;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class WriteHandler extends CommandHandler {
  public WriteHandler(UUID playerId) {
    super(playerId);
  }

  @Override
  public int getCommandID() {
    return 1;
  }

  @Override
  public JsonObject build() {
    JsonObject jsonObject = super.build();
    jsonObject.addProperty("player", getPlayerId().toString());
    jsonObject.addProperty(
        "data",
        Base64.getEncoder()
            .encodeToString(
                new Gson()
                    .toJson(SynchronizerManager.serialize(getPlayerId()))
                    .getBytes(StandardCharsets.UTF_8)));
    return jsonObject;
  }

  @Override
  public boolean interactive() {
    return false;
  }
}
