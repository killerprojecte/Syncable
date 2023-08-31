package dev.rgbmc.syncable.client.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class CustomWriteHandler extends WriteHandler {
    private final JsonObject jsonObject;

    public CustomWriteHandler(UUID playerId, JsonObject jsonObject) {
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
                        .encodeToString(
                                new Gson()
                                        .toJson(jsonObject)
                                        .getBytes(StandardCharsets.UTF_8)));
        return jsonObject;
    }
}
