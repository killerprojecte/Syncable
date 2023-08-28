package dev.rgbmc.syncable.objects;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class SyncablePersistentDataContainer {
  @SerializedName("persistent_datas")
  private Map<String, SyncablePersistentData<?>> persistentDataMap;

  public SyncablePersistentDataContainer(Map<String, SyncablePersistentData<?>> persistentDataMap) {
    this.persistentDataMap = persistentDataMap;
  }

  public Map<String, SyncablePersistentData<?>> getPersistentDataMap() {
    return persistentDataMap;
  }

  public <T> T getValue(String tag, Class<T> type) {
    if (!persistentDataMap.containsKey(tag)) {
      return null;
    }
    final boolean canCast = type.isAssignableFrom(persistentDataMap.get(tag).getValue().getClass());
    if (!canCast) {
      return null;
    }
    return type.cast(persistentDataMap.get(tag).getValue());
  }

  public SyncablePersistentDataType.TagType getType(String type) {
    if (persistentDataMap.containsKey(type)) {
      return SyncablePersistentDataType.TagType.valueOf(persistentDataMap.get(type).getType());
    }
    return null;
  }
}
