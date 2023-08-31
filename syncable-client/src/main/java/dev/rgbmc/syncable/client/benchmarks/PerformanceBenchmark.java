package dev.rgbmc.syncable.client.benchmarks;

import com.google.gson.JsonObject;
import dev.rgbmc.syncable.client.SyncableClient;
import dev.rgbmc.syncable.client.handlers.CustomWriteHandler;
import dev.rgbmc.syncable.client.handlers.ReadOnlyHandler;
import dev.rgbmc.syncable.client.handlers.RemoveHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class PerformanceBenchmark {
    private static final List<UUID> list = new ArrayList<>();

    public static void dirtyWrite1() {
        long time = System.currentTimeMillis();
        UUID uuid = UUID.randomUUID();
        list.add(uuid);
        SyncableClient.getInstance().sendCommand(new CustomWriteHandler(uuid, genDirtyJson()));
        SyncableClient.getInstance().getLogger().info("Test 1: " + (System.currentTimeMillis() - time) + "ms");
    }

    public static void dirtyWrite8() {
        long time = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        for (int i = 0; i < 10000; i++) {
            executorService.execute(() -> {
                UUID uuid = UUID.randomUUID();
                list.add(uuid);
                SyncableClient.getInstance().sendCommand(new CustomWriteHandler(uuid, genDirtyJson()));
            });
        }
        SyncableClient.getInstance().getLogger().info("Test 2: " + (System.currentTimeMillis() - time) + "ms");
    }

    public static void dirtyRead1() {
        long time = System.currentTimeMillis();
        UUID uuid = list.get(0);
        CompletableFuture<JsonObject> completableFuture = new CompletableFuture<>();
        completableFuture.thenAcceptAsync(jsonObject -> {
            SyncableClient.getInstance().getLogger().info("Test 3: " + (System.currentTimeMillis() - time) + "ms");
        });
        SyncableClient.getInstance().sendCommand(new ReadOnlyHandler(completableFuture, uuid));
        SyncableClient.getInstance().sendCommand(new RemoveHandler(list.get(0)));
        list.remove(0);
    }

    public static void dirtyRead8() {
        long time = System.currentTimeMillis();
        AtomicLong ms = new AtomicLong(0L);
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        for (UUID uuid : new ArrayList<>(list)) {
            executorService.execute(() -> {
                CompletableFuture<JsonObject> completableFuture = new CompletableFuture<>();
                completableFuture.thenAcceptAsync(jsonObject -> {
                    ms.set(System.currentTimeMillis() - time);
                });
                SyncableClient.getInstance().sendCommand(new ReadOnlyHandler(completableFuture, uuid));
            });
        }
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        SyncableClient.getInstance().getLogger().info("Test 4: " + ms + "ms");
    }

    public static void clear() {
        long time = System.currentTimeMillis();
        for (UUID uuid : list) {
            SyncableClient.getInstance().sendCommand(new RemoveHandler(uuid));
        }
        SyncableClient.getInstance().getLogger().info("Test 5: " + (System.currentTimeMillis() - time) + "ms");
        list.clear();
    }

    private static JsonObject genDirtyJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(UUID.randomUUID().toString(), "Benchmark");
        return jsonObject;
    }

    public static void main(String[] args) throws Exception {
        SyncableClient syncableClient = new SyncableClient(UUID.randomUUID(), "localhost:55541");
        dirtyWrite1();
        Thread.sleep(5000);
        dirtyWrite8();
        Thread.sleep(5000);
        dirtyRead1();
        Thread.sleep(5000);
        dirtyRead8();
        Thread.sleep(5000);
        clear();
    }
}
