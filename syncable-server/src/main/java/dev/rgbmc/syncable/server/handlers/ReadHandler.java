package dev.rgbmc.syncable.server.handlers;

import com.google.gson.JsonObject;
import dev.rgbmc.syncable.server.SyncableServer;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class ReadHandler extends CommandHandler {
  private final UUID playerId;

  public ReadHandler(JsonObject jsonObject) {
    super(jsonObject);
    String decoded =
        new String(
            Base64.getDecoder().decode(jsonObject.get("data").getAsString()),
            StandardCharsets.UTF_8);
    this.playerId = UUID.fromString(decoded);
  }

  @Override
  public UUID getPlayerID() {
    return playerId;
  }

  @Override
  public String handle() throws Exception {
    if (SyncableServer.getInstance().getDatabase().hasRegistered(getPlayerID())) {
      return Base64.getEncoder()
          .encodeToString(
              SyncableServer.getInstance()
                  .getDatabase()
                  .getUserData(getPlayerID())
                  .getData()
                  .getBytes(StandardCharsets.UTF_8));
    } else {
      return "not_registered";
    }
  }

  @Override
  public boolean callable() {
    return true;
  }
}
