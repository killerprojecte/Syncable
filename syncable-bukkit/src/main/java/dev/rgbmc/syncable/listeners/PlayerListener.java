package dev.rgbmc.syncable.listeners;

import dev.rgbmc.syncable.SyncableBukkit;
import dev.rgbmc.syncable.client.handlers.ReadHandler;
import dev.rgbmc.syncable.client.handlers.WriteHandler;
import dev.rgbmc.syncable.utils.SyncUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {
  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerJoin(PlayerLoginEvent event) {
    if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) return;
    Player player = event.getPlayer();
    SyncableBukkit.getSyncableClient().sendCommand(new ReadHandler(player.getUniqueId()));
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerQuit(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    if (SyncableBukkit.instance.isEnabled()) {
      SyncUtils.write(player);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerOpenInventory(InventoryOpenEvent event) {
    Player player = (Player) event.getPlayer();
    SyncUtils.write(player);
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerClickInventory(InventoryClickEvent event) {
    Player player = (Player) event.getWhoClicked();
    SyncUtils.write(player);
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerCloseInventory(InventoryCloseEvent event) {
    Player player = (Player) event.getPlayer();
    SyncUtils.write(player);
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerDeath(PlayerDeathEvent event) {
    Player player = event.getEntity();
    SyncUtils.write(player);
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerDropItem(PlayerDropItemEvent event) {
    Player player = event.getPlayer();
    SyncUtils.write(player);
  }
}
