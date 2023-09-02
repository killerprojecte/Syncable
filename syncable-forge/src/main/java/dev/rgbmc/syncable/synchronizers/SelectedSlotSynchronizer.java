package dev.rgbmc.syncable.synchronizers;

import dev.rgbmc.syncable.SyncableForge;
import dev.rgbmc.syncable.client.synchronizers.Synchronizer;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class SelectedSlotSynchronizer extends Synchronizer {
    @Override
    public void deserialize(UUID playerId, String data) {
        MinecraftServer server = SyncableForge.getServer();
        ServerPlayer player = server.getPlayerList().getPlayer(playerId);
        player.getInventory().selected = Integer.parseInt(data);
        player.connection.send(new ClientboundSetCarriedItemPacket(player.getInventory().selected));
    }

    @Override
    public String serialize(UUID playerId) {
        MinecraftServer server = SyncableForge.getServer();
        ServerPlayer player = server.getPlayerList().getPlayer(playerId);
        return String.valueOf(player.getInventory().selected);
    }
}
