package dev.rgbmc.syncable.tasks;

import dev.rgbmc.syncable.SyncableForge;
import dev.rgbmc.syncable.utils.SyncUtils;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.TimerTask;

public class AutoSaveTimer extends TimerTask {
    @Override
    public void run() {
        for (ServerPlayer player :
                new ArrayList<>(SyncableForge.getServer().getPlayerList().getPlayers())) {
            SyncUtils.write(player);
        }
    }
}
