package dev.rgbmc.syncable.synchronizers;

import dev.rgbmc.syncable.SyncableForge;
import dev.rgbmc.syncable.client.synchronizers.Synchronizer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class MaxHealthSynchronizer extends Synchronizer {
    @Override
    public void deserialize(UUID playerId, String data) {
        MinecraftServer server = SyncableForge.getServer();
        ServerPlayer player = server.getPlayerList().getPlayer(playerId);
        server.executeBlocking(
                () -> {
                    player
                            .getAttribute(Attributes.MAX_HEALTH)
                            .setBaseValue(Double.parseDouble(data));
                });
    }

    @Override
    public String serialize(UUID playerId) {
        MinecraftServer server = SyncableForge.getServer();
        ServerPlayer player = server.getPlayerList().getPlayer(playerId);
        return String.valueOf((double) player.getMaxHealth());
    }
}
