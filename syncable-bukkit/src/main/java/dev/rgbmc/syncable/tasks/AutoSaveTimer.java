package dev.rgbmc.syncable.tasks;

import dev.rgbmc.syncable.utils.SyncUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.TimerTask;

public class AutoSaveTimer extends TimerTask {
    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            SyncUtils.write(player);
        }
    }
}
