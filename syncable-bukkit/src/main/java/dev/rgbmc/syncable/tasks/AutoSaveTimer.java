package dev.rgbmc.syncable.tasks;

import dev.rgbmc.syncable.utils.SyncUtils;
import java.util.TimerTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class AutoSaveTimer extends TimerTask {
  @Override
  public void run() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      SyncUtils.write(player);
    }
  }
}
