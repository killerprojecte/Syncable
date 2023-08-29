package dev.rgbmc.syncable.client.handlers;

import com.google.gson.JsonObject;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ReadOnlyHandler extends ReadHandler {
    private final CompletableFuture<JsonObject> future;

    public ReadOnlyHandler(CompletableFuture<JsonObject> future, UUID playerId) {
        super(playerId);
        this.future = future;
    }

    @Override
    public void handle(JsonObject jsonObject) {
        future.complete(jsonObject);
    }
}
