package dev.rgbmc.syncable.objects;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class SyncablePersistentDataType<T, Z> {

  public static final SyncablePersistentDataType<?, ?>[] TYPES =
      new SyncablePersistentDataType<?, ?>[] {
        new SyncablePersistentDataType<>(TagType.BYTE, PersistentDataType.BYTE),
        new SyncablePersistentDataType<>(TagType.SHORT, PersistentDataType.SHORT),
        new SyncablePersistentDataType<>(TagType.INTEGER, PersistentDataType.INTEGER),
        new SyncablePersistentDataType<>(TagType.LONG, PersistentDataType.LONG),
        new SyncablePersistentDataType<>(TagType.FLOAT, PersistentDataType.FLOAT),
        new SyncablePersistentDataType<>(TagType.DOUBLE, PersistentDataType.DOUBLE),
        new SyncablePersistentDataType<>(TagType.STRING, PersistentDataType.STRING),
        new SyncablePersistentDataType<>(TagType.BYTE_ARRAY, PersistentDataType.BYTE_ARRAY),
        new SyncablePersistentDataType<>(TagType.INTEGER_ARRAY, PersistentDataType.INTEGER_ARRAY),
        new SyncablePersistentDataType<>(TagType.LONG_ARRAY, PersistentDataType.LONG_ARRAY),
        new SyncablePersistentDataType<>(
            TagType.TAG_CONTAINER_ARRAY, PersistentDataType.TAG_CONTAINER_ARRAY),
        new SyncablePersistentDataType<>(TagType.TAG_CONTAINER, PersistentDataType.TAG_CONTAINER)
      };
  private final TagType type;
  private final PersistentDataType<T, Z> originType;

  public SyncablePersistentDataType(TagType type, PersistentDataType<T, Z> originType) {
    this.type = type;
    this.originType = originType;
  }

  public PersistentDataType<T, Z> getOriginType() {
    return originType;
  }

  public TagType getType() {
    return type;
  }

  public SyncablePersistentData<Z> getValue(PersistentDataContainer container, NamespacedKey key) {
    return new SyncablePersistentData<>(type, container.get(key, originType));
  }

  public void setValue(
      SyncablePersistentDataContainer container, Player player, NamespacedKey key) {
    Z data = container.getValue(key.toString(), originType.getComplexType());
    if (data != null) {
      player.getPersistentDataContainer().set(key, originType, data);
    }
  }

  public enum TagType {
    BYTE,
    SHORT,
    INTEGER,
    LONG,
    FLOAT,
    DOUBLE,
    STRING,
    BYTE_ARRAY,
    INTEGER_ARRAY,
    LONG_ARRAY,
    TAG_CONTAINER_ARRAY,
    TAG_CONTAINER;
  }
}
