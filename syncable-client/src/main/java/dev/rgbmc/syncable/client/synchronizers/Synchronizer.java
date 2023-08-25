package dev.rgbmc.syncable.client.synchronizers;

import java.util.UUID;

public abstract class Synchronizer {
  public abstract void deserialize(UUID playerId, String data);

  public abstract String serialize(UUID playerId);
}
