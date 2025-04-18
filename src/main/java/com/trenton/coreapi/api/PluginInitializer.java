package com.trenton.coreapi.api;

import com.trenton.coreapi.util.ReflectionUtils;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.List;

public class PluginInitializer {
    private final Plugin plugin;
    private final String packageName;
    private List<ManagerBase> managers;
    private List<CommandBase> commands;
    private List<ListenerBase> listeners;

    public PluginInitializer(Plugin plugin, String packageName) {
        this.plugin = plugin;
        this.packageName = packageName;
    }

    public void initialize() {
        managers = ReflectionUtils.initializeClasses(plugin, packageName, ManagerBase.class);
        commands = ReflectionUtils.initializeClasses(plugin, packageName, CommandBase.class);
        listeners = ReflectionUtils.initializeClasses(plugin, packageName, ListenerBase.class);
        managers.forEach(m -> m.init(plugin));
        assignManagersToPluginFields();
        commands.forEach(c -> c.register(plugin));
        listeners.forEach(l -> l.register(plugin));
    }

    public void shutdown() {
        managers.forEach(ManagerBase::shutdown);
    }

    private void assignManagersToPluginFields() {
        try {
            for (ManagerBase manager : managers) {
                for (Field field : plugin.getClass().getDeclaredFields()) {
                    if (field.getType().isAssignableFrom(manager.getClass())) {
                        field.setAccessible(true);
                        field.set(plugin, manager);
                        field.setAccessible(false);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            plugin.getLogger().severe("Failed to assign manager to plugin field: " + e.getMessage());
        }
    }

    public List<ManagerBase> getManagers() {
        return managers;
    }

    public List<CommandBase> getCommands() {
        return commands;
    }

    public List<ListenerBase> getListeners() {
        return listeners;
    }
}