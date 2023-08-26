package dev.rgbmc.syncable.objects;

import com.google.gson.annotations.SerializedName;

public class SyncablePersistentData<T> {
  @SerializedName("type")
  private String type;

  @SerializedName("value")
  private T value;

  public SyncablePersistentData(SyncablePersistentDataType.TagType type, T value) {
    this.type = type.name();
    this.value = value;
  }

  public String getType() {
    return type;
  }

  public T getValue() {
    return value;
  }
}
