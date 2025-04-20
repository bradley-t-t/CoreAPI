package com.trenton.coreapi.api;

import org.bukkit.event.Event;

public interface CoreListenerInterface {
    void handleEvent(Event event);
    Class<? extends Event>[] getHandledEvents();
}