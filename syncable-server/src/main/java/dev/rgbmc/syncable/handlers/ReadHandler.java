package dev.rgbmc.syncable.handlers;

import com.google.gson.JsonObject;
import dev.rgbmc.syncable.SyncableServer;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class ReadHandler extends CommandHandler {
    private final UUID playerId;

    public ReadHandler(JsonObject jsonObject) {
        super(jsonObject);
        String decoded = new String(Base64.getDecoder().decode(jsonObject.get("data").getAsString()), StandardCharsets.UTF_8);
        this.playerId = UUID.fromString(decoded);
    }

    public UUID getPlayerId() {
        return playerId;
    }

    @Override
    public String handle() throws Exception {
        if (SyncableServer.getInstance().getDatabase().hasRegistered(getPlayerId())) {
            return Base64.getEncoder().encodeToString(SyncableServer.getInstance().getDatabase().getUserData(getPlayerId()).getEncodedData());
        } else {
            return Base64.getEncoder().encodeToString("not_registered".getBytes(StandardCharsets.UTF_8));
        }
    }

    @Override
    public boolean callable() {
        return true;
    }
}
