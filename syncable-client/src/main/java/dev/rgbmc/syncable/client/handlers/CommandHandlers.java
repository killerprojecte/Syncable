package dev.rgbmc.syncable.client.handlers;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommandHandlers {
  private static final Map<Integer, Class<? extends CommandHandler>> handlers = new HashMap<>();

  static {
    handlers.put(0, ReadHandler.class);
    handlers.put(1, WriteHandler.class);
  }

  public static CommandHandler getHandler(int commandId, UUID playerId) {
    if (handlers.containsKey(commandId)) {
      try {
        return handlers.get(commandId).getDeclaredConstructor(UUID.class).newInstance(playerId);
      } catch (InstantiationException
          | IllegalAccessException
          | InvocationTargetException
          | NoSuchMethodException e) {
        throw new RuntimeException(e);
      }
    } else {
      throw new IllegalArgumentException("Unsupported command id: " + commandId);
    }
  }
}
