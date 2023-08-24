package dev.rgbmc.syncable.handlers;

import com.google.gson.JsonObject;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class CommandHandlers {
    private static Map<Integer, Class<? extends CommandHandler>> handlers = new HashMap<>();

    static {
        handlers.put(0, ReadHandler.class);
        handlers.put(1, WriteHandler.class);
    }

    public static CommandHandler getHandler(int commandId, JsonObject jsonObject) {
        if (handlers.containsKey(commandId)) {
            try {
                return handlers.get(commandId).getDeclaredConstructor(JsonObject.class).newInstance(jsonObject);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalArgumentException("Unsupported command id: " + commandId);
        }
    }
}
