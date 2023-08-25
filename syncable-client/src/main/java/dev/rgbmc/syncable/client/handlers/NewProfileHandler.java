package dev.rgbmc.syncable.client.handlers;

import dev.rgbmc.syncable.client.SyncableClient;

import java.util.UUID;

public class NewProfileHandler {

  public static void create(UUID playerId) {
    SyncableClient.getInstance().sendCommand(new WriteHandler(playerId));
  }
}
