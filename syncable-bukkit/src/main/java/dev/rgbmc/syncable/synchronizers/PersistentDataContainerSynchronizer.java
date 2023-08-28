package dev.rgbmc.syncable.synchronizers;

import com.google.gson.Gson;
import dev.rgbmc.syncable.client.synchronizers.Synchronizer;
import dev.rgbmc.syncable.objects.SyncablePersistentData;
import dev.rgbmc.syncable.objects.SyncablePersistentDataContainer;
import dev.rgbmc.syncable.objects.SyncablePersistentDataType;
import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;

public class PersistentDataContainerSynchronizer extends Synchronizer {
  @Override
  public void deserialize(UUID playerId, String data) {
    Player player = Bukkit.getPlayer(playerId);
    SyncablePersistentDataContainer container =
        new Gson().fromJson(data, SyncablePersistentDataContainer.class);
    for (NamespacedKey key : player.getPersistentDataContainer().getKeys()) {
      player.getPersistentDataContainer().remove(key);
    }
    container
        .getPersistentDataMap()
        .keySet()
        .forEach(
            key -> {
              final NamespacedKey namespacedKey = NamespacedKey.fromString(key);
              if (namespacedKey != null) {
                for (SyncablePersistentDataType<?, ?> type : SyncablePersistentDataType.TYPES) {
                  if (type.getType().equals(container.getType(key))) {
                    type.setValue(container, player, namespacedKey);
                    break;
                  }
                }
              }
            });
  }

  @Override
  public String serialize(UUID playerId) {
    Player player = Bukkit.getPlayer(playerId);
    Map<String, SyncablePersistentData<?>> dataMap = new HashMap<>();
    PersistentDataContainer dataContainer = player.getPersistentDataContainer();
    for (NamespacedKey namespacedKey : dataContainer.getKeys()) {
      SyncablePersistentDataType<?, ?> syncableType =
          Arrays.stream(SyncablePersistentDataType.TYPES)
              .filter(dataType -> dataContainer.has(namespacedKey, dataType.getOriginType()))
              .findFirst()
              .map(dataType -> dataType)
              .orElse(null);
      if (syncableType != null) {
        dataMap.put(namespacedKey.toString(), syncableType.getValue(dataContainer, namespacedKey));
      }
    }
    return new Gson().toJson(new SyncablePersistentDataContainer(dataMap));
  }
}
