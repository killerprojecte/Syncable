package dev.rgbmc.syncable.client.handlers;

import com.google.gson.JsonObject;

import java.util.UUID;

public class RemoveHandler extends CommandHandler {
    public RemoveHandler(UUID playerId) {
        super(playerId);
    }

    @Override
    public int getCommandID() {
        return 2;
    }

    @Override
    public boolean interactive() {
        return false;
    }

    @Override
    public JsonObject build() {
        JsonObject jsonObject = super.build();
        jsonObject.addProperty("player", getPlayerId().toString());
        return jsonObject;
    }
}
