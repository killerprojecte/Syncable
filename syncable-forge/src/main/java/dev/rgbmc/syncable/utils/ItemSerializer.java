package dev.rgbmc.syncable.utils;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ItemSerializer {
    public static String serialize(ItemStack item) {
        CompoundTag compound = item.save(new CompoundTag());
        return Base64.getEncoder().encodeToString(compound.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static ItemStack deserialize(String encoded) {
        String item_serialized =
                new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
        try {
            return ItemStack.of(TagParser.parseTag(item_serialized));
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
