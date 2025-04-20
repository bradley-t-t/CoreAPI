package com.trenton.coreapi.api;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class CoreListenerAdapter implements Listener {
    private final CoreListenerInterface coreListener;

    public CoreListenerAdapter(CoreListenerInterface coreListener) {
        this.coreListener = coreListener;
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (isHandledEvent(event.getClass())) {
            coreListener.handleEvent(event);
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        if (isHandledEvent(event.getClass())) {
            coreListener.handleEvent(event);
        }
    }

    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent event) {
        if (isHandledEvent(event.getClass())) {
            coreListener.handleEvent(event);
        }
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if (isHandledEvent(event.getClass())) {
            coreListener.handleEvent(event);
        }
    }

    @EventHandler
    public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
        if (isHandledEvent(event.getClass())) {
            coreListener.handleEvent(event);
        }
    }

    @EventHandler
    public void onCraftItemEvent(CraftItemEvent event) {
        if (isHandledEvent(event.getClass())) {
            coreListener.handleEvent(event);
        }
    }

    private boolean isHandledEvent(Class<?> eventClass) {
        for (Class<? extends org.bukkit.event.Event> handledEvent : coreListener.getHandledEvents()) {
            if (handledEvent.isAssignableFrom(eventClass)) {
                return true;
            }
        }
        return false;
    }

    public CoreListenerInterface getCoreListener() {
        return coreListener;
    }
}