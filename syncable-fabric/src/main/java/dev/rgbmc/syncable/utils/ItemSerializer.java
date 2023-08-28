package dev.rgbmc.syncable.utils;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;

public class ItemSerializer {
  public static String serialize(ItemStack item) {
    NbtCompound compound = item.writeNbt(new NbtCompound());
    return Base64.getEncoder().encodeToString(compound.toString().getBytes(StandardCharsets.UTF_8));
  }

  public static ItemStack deserialize(String encoded) {
    String item_serialized =
        new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
    try {
      return ItemStack.fromNbt(StringNbtReader.parse(item_serialized));
    } catch (CommandSyntaxException e) {
      throw new RuntimeException(e);
    }
  }
}
