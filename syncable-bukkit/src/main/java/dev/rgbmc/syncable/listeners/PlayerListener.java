package dev.rgbmc.syncable.listeners;

import dev.rgbmc.syncable.SyncableBukkit;
import dev.rgbmc.syncable.client.handlers.ReadHandler;
import dev.rgbmc.syncable.utils.SyncUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.CompletableFuture;

public class PlayerListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ReadHandler readHandler = new ReadHandler(player.getUniqueId());
        if (SyncableBukkit.instance.isFreezeMode()) {
            FreezeListener.freeze(player.getUniqueId());
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            future.thenAcceptAsync(isNew -> {
                FreezeListener.unfreeze(player.getUniqueId());
            });
            readHandler.callItWhenFinishLoading(future);
        }
        SyncableBukkit.getSyncableClient().sendCommand(readHandler);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (SyncableBukkit.instance.isEnabled()) {
            SyncUtils.write(player);
        }
        if (FreezeListener.isFreezing(player.getUniqueId())) {
            FreezeListener.unfreeze(player.getUniqueId());
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerEnchant(EnchantItemEvent event) {
        Player player = event.getEnchanter();
        SyncUtils.write(player);
    }
}
