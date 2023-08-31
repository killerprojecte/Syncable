package dev.rgbmc.syncable.synchronizers;

import com.google.gson.Gson;
import dev.rgbmc.syncable.SyncableForge;
import dev.rgbmc.syncable.client.synchronizers.Synchronizer;
import dev.rgbmc.syncable.objects.AdvancementsData;
import dev.rgbmc.syncable.utils.AdvancementUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class AdvancementSynchronizer extends Synchronizer {
    @Override
    public void deserialize(UUID playerId, String data) {
        MinecraftServer server = SyncableForge.getServer();
        ServerPlayer player = server.getPlayerList().getPlayer(playerId);
        AdvancementsData advancementsData = new Gson().fromJson(data, AdvancementsData.class);
        AdvancementUtils.setAdvancements(player, advancementsData.getAdvancements());
    }

    @Override
    public String serialize(UUID playerId) {
        MinecraftServer server = SyncableForge.getServer();
        ServerPlayer player = server.getPlayerList().getPlayer(playerId);
        AdvancementsData advancementsData =
                new AdvancementsData(AdvancementUtils.getAdvancements(player));
        return new Gson().toJson(advancementsData);
    }
}
