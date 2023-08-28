package dev.rgbmc.syncable.server.handlers;

import com.google.gson.JsonObject;
import java.util.UUID;

public abstract class CommandHandler {
  private final UUID messageId;

  public CommandHandler(JsonObject jsonObject) {
    this.messageId = UUID.fromString(jsonObject.get("messageId").getAsString());
  }

  public UUID getMessageId() {
    return messageId;
  }

  public abstract String handle() throws Exception;

  public abstract boolean callable();

  public abstract UUID getPlayerID();
}
