package dev.rgbmc.syncable.client.handlers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.rgbmc.syncable.client.synchronizers.SynchronizerManager;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ReadHandler extends CommandHandler {
    private CompletableFuture<Boolean> callWhenFinishLoading;
    private boolean shouldCall = false;

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

    public void callItWhenFinishLoading(CompletableFuture<Boolean> future) {
        this.callWhenFinishLoading = future;
        this.shouldCall = true;
    }

    @Override
    public void handle(JsonObject jsonObject) {
        boolean isNew;
        if (jsonObject.get("data").getAsString().equalsIgnoreCase("not_registered")) {
            isNew = true;
            NewProfileHandler.create(getPlayerId());
        } else {
            isNew = false;
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
        if (shouldCall) {
            callWhenFinishLoading.complete(isNew);
        }
    }
}
