package dev.rgbmc.syncable.client.handlers;

import com.google.gson.JsonObject;
import java.util.UUID;

public abstract class CommandHandler {
  private final UUID messageId;
  private final UUID playerId;

  public CommandHandler(UUID playerId) {
    this.playerId = playerId;
    messageId = UUID.randomUUID();
  }

  public UUID getMessageId() {
    return messageId;
  }

  public UUID getPlayerId() {
    return playerId;
  }

  public abstract int getCommandID();

  public abstract boolean interactive();

  public JsonObject build() {
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("commandId", getCommandID());
    jsonObject.addProperty("messageId", getMessageId().toString());
    return jsonObject;
  }

  public void handle(JsonObject jsonObject) {
    throw new RuntimeException("Not a interactive handler");
  }
}
