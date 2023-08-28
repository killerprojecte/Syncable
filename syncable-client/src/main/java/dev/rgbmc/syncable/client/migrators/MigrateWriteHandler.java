package dev.rgbmc.syncable.client.migrators;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.rgbmc.syncable.client.handlers.WriteHandler;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class MigrateWriteHandler extends WriteHandler {
  private final JsonObject jsonObject;

  public MigrateWriteHandler(UUID playerId, JsonObject jsonObject) {
    super(playerId);
    this.jsonObject = jsonObject;
  }

  @Override
  public JsonObject build() {
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("commandId", getCommandID());
    jsonObject.addProperty("messageId", getMessageId().toString());
    jsonObject.addProperty("player", getPlayerId().toString());
    jsonObject.addProperty(
        "data",
        Base64.getEncoder()
            .encodeToString(new Gson().toJson(this.jsonObject).getBytes(StandardCharsets.UTF_8)));
    return jsonObject;
  }
}
