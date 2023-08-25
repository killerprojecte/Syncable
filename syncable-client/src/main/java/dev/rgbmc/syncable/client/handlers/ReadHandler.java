package dev.rgbmc.syncable.client.handlers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.rgbmc.syncable.client.synchronizers.SynchronizerManager;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class ReadHandler extends CommandHandler {
  public ReadHandler(UUID playerId) {
    super(playerId);
  }

  @Override
  public int getCommandID() {
    return 0;
  }

  @Override
  public boolean interactive() {
    return true;
  }

  @Override
  public JsonObject build() {
    JsonObject jsonObject = super.build();
    jsonObject.addProperty(
        "data",
        Base64.getEncoder()
            .encodeToString(getPlayerId().toString().getBytes(StandardCharsets.UTF_8)));
    return jsonObject;
  }

  @Override
  public void handle(JsonObject jsonObject) {
    if (jsonObject.get("data").getAsString().equalsIgnoreCase("not_registered")) {
      NewProfileHandler.create(getPlayerId());
    } else {
      SynchronizerManager.deserialize(
          getPlayerId(),
          JsonParser.parseString(
                  new String(
                      Base64.getDecoder()
                          .decode(
                              jsonObject
                                  .get("data")
                                  .getAsString()
                                  .getBytes(StandardCharsets.UTF_8)),
                      StandardCharsets.UTF_8))
              .getAsJsonObject());
    }
  }
}
