package dev.rgbmc.syncable.handlers;

import com.google.gson.JsonObject;
import dev.rgbmc.syncable.SyncableServer;
import dev.rgbmc.syncable.data.UserData;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class WriteHandler extends CommandHandler {
    public UUID playerId;
    public String data;

    public WriteHandler(JsonObject jsonObject) {
        super(jsonObject);
        String decoded = new String(Base64.getDecoder().decode(jsonObject.get("player").getAsString()), StandardCharsets.UTF_8);
        this.playerId = UUID.fromString(decoded);
        this.data = new String(Base64.getDecoder().decode(jsonObject.get("data").getAsString()), StandardCharsets.UTF_8);
        CompletableFuture.runAsync(() -> {
            try {
                SyncableServer.getInstance().getDatabase().createOrUpdate(new UserData(playerId, data));
            } catch (SQLException e) {
                SyncableServer.getInstance().getLogger().warning("Error encountered when saving player data (UUID: " + playerId.toString() + ")");
                throw new RuntimeException(e);
            }
        });
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getData() {
        return data;
    }

    @Override
    public String handle() throws Exception {
        throw new IllegalAccessException("This not a callable command");
    }

    @Override
    public boolean callable() {
        return false;
    }
}
