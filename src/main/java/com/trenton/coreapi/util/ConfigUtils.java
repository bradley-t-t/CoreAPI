package com.trenton.coreapi.util;

import org.bukkit.plugin.Plugin;

import java.io.File;

public class ConfigUtils {
    public static void saveDefaultConfig(Plugin plugin, String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            plugin.saveResource(fileName, false);
        }
    }
}