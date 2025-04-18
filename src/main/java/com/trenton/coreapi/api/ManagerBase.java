package com.trenton.coreapi.api;

import org.bukkit.plugin.Plugin;

public interface ManagerBase {
    void init(Plugin plugin);
    void shutdown();
}