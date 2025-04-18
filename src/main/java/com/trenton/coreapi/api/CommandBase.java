package com.trenton.coreapi.api;

import org.bukkit.plugin.Plugin;

public interface CommandBase {
    void register(Plugin plugin);
    String getCommandName();
}