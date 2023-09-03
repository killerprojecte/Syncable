package dev.rgbmc.syncable.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FreezeListener implements Listener {
    private static final List<UUID> frozen_players = new ArrayList<>();

    private static void cancel(Cancellable cancellable) {
        if (cancellable instanceof PlayerEvent) {
            PlayerEvent event = (PlayerEvent) cancellable;
            if (isFreezing(event.getPlayer().getUniqueId())) cancellable.setCancelled(true);
        } else if (cancellable instanceof EntityEvent) {
            EntityEvent event = (EntityEvent) cancellable;
            if (!(event.getEntity() instanceof Player)) return;
            if (isFreezing(event.getEntity().getUniqueId())) cancellable.setCancelled(true);
        } else if (cancellable instanceof InventoryOpenEvent) {
            InventoryOpenEvent event = (InventoryOpenEvent) cancellable;
            if (isFreezing(event.getPlayer().getUniqueId())) cancellable.setCancelled(true);
        } else if (cancellable instanceof InventoryInteractEvent) {
            InventoryInteractEvent event = (InventoryInteractEvent) cancellable;
            if (isFreezing(event.getWhoClicked().getUniqueId())) cancellable.setCancelled(true);
        }
    }

    public static void freeze(UUID playerId) {
        if (!frozen_players.contains(playerId)) frozen_players.add(playerId);
    }

    public static void unfreeze(UUID playerId) {
        frozen_players.remove(playerId);
    }

    public static boolean isFreezing(UUID playerId) {
        return frozen_players.contains(playerId);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        cancel(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDropItem(PlayerDropItemEvent event) {
        cancel(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        cancel(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        cancel(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        cancel(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onOpenInventory(InventoryOpenEvent event) {
        cancel(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClickInventory(InventoryClickEvent event) {
        cancel(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        cancel(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDragInventory(InventoryDragEvent event) {
        cancel(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTradeSelect(TradeSelectEvent event) {
        cancel(event);
    }
}
