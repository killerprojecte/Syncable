package dev.rgbmc.syncable.utils;

import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ItemSerializer {
  public static String serialize(ItemStack item) {
    return Base64.getEncoder()
        .encodeToString(NBTItem.convertItemtoNBT(item).toString().getBytes(StandardCharsets.UTF_8));
  }

  public static ItemStack deserialize(String encoded) {
    String item_serialized =
        new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
    return NBTItem.convertNBTtoItem(new NBTContainer(item_serialized));
  }
}
