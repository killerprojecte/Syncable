package dev.rgbmc.syncable.server.handlers;

import com.google.gson.JsonObject;
import dev.rgbmc.syncable.server.SyncableServer;

import java.sql.SQLException;
import java.util.UUID;

public class RemoveHandler extends CommandHandler {
    private final UUID playerId;

    public RemoveHandler(JsonObject jsonObject) {
        super(jsonObject);
        this.playerId = UUID.fromString(jsonObject.get("player").getAsString());
        try {
            SyncableServer.getInstance().getDatabase().remove(playerId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String handle() throws Exception {
        throw new IllegalAccessException("This not a callable command");
    }

    @Override
    public boolean callable() {
        return false;
    }

    @Override
    public UUID getPlayerID() {
        return playerId;
    }
}
