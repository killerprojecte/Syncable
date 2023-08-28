package dev.rgbmc.syncable.tasks;

import dev.rgbmc.syncable.SyncableFabric;
import dev.rgbmc.syncable.utils.SyncUtils;
import java.util.ArrayList;
import java.util.TimerTask;
import net.minecraft.server.network.ServerPlayerEntity;

public class AutoSaveTimer extends TimerTask {
  @Override
  public void run() {
    for (ServerPlayerEntity player :
        new ArrayList<>(SyncableFabric.getServer().getPlayerManager().getPlayerList())) {
      SyncUtils.write(player);
    }
  }
}
