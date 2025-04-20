package com.trenton.coreapi.api;

import com.trenton.coreapi.annotations.CoreCommand;
import com.trenton.coreapi.annotations.CoreListener;
import com.trenton.coreapi.annotations.CoreManager;
import com.trenton.coreapi.util.ConfigUtils;
import com.trenton.coreapi.util.ReflectionUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;

public class CoreAPI {
    private final Plugin plugin;
    private final String packageName;
    private final Map<String, Object> managers = new HashMap<>();
    private final Map<String, CoreCommandAdapter> commands = new HashMap<>();
    private final Map<String, CoreListenerAdapter> listeners = new HashMap<>();
    private FileConfiguration config;
    private FileConfiguration messages;

    public CoreAPI(Plugin plugin, String packageName) {
        this.plugin = plugin;
        this.packageName = packageName;
        setupConfigurations();
    }

    private void setupConfigurations() {
        ConfigUtils.saveDefaultConfig(plugin, "config.yml");
        ConfigUtils.saveDefaultConfig(plugin, "messages.yml");
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public void initialize() {
        scanAndRegisterClasses();
        managers.values().forEach(m -> invokeMethod(m, "init", plugin));
        assignManagersToFields();
        commands.values().forEach(this::registerCommand);
        listeners.values().forEach(this::registerListener);
    }

    public void shutdown() {
        managers.values().forEach(m -> invokeMethod(m, "shutdown"));
    }

    private void scanAndRegisterClasses() {
        List<Class<?>> classes = ReflectionUtils.scanClasses(plugin, packageName);
        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(CoreManager.class)) {
                CoreManager annotation = clazz.getAnnotation(CoreManager.class);
                String name = annotation.name().isEmpty() ? clazz.getSimpleName() : annotation.name();
                Object instance = instantiateClass(clazz);
                if (instance != null) {
                    managers.put(name, instance);
                    invokeMethod(instance, "init", plugin);
                }
            }
        }
        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(CoreCommand.class)) {
                CoreCommand annotation = clazz.getAnnotation(CoreCommand.class);
                String name = annotation.name();
                Object instance = instantiateClass(clazz);
                if (instance != null && instance instanceof CoreCommandHandler) {
                    commands.put(name, new CoreCommandAdapter((CoreCommandHandler) instance, annotation));
                    invokeMethod(instance, "init", plugin);
                } else {
                    plugin.getLogger().warning("Class " + clazz.getSimpleName() + " is annotated with @CoreCommand but does not implement CoreCommandHandler");
                }
            } else if (clazz.isAnnotationPresent(CoreListener.class)) {
                CoreListener annotation = clazz.getAnnotation(CoreListener.class);
                String name = annotation.name().isEmpty() ? clazz.getSimpleName() : annotation.name();
                Object instance = instantiateClass(clazz);
                if (instance != null && instance instanceof CoreListenerInterface) {
                    listeners.put(name, new CoreListenerAdapter((CoreListenerInterface) instance));
                    invokeMethod(instance, "init", plugin);
                } else {
                    plugin.getLogger().warning("Class " + clazz.getSimpleName() + " is annotated with @CoreListener but does not implement CoreListenerInterface");
                }
            }
        }
    }

    private Object instantiateClass(Class<?> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to instantiate " + clazz.getSimpleName() + ": " + e.getMessage());
            return null;
        }
    }

    private void invokeMethod(Object instance, String methodName, Object... args) {
        try {
            Class<?>[] paramTypes = args.length > 0 ? new Class<?>[]{args[0].getClass()} : new Class<?>[0];
            Method method = instance.getClass().getDeclaredMethod(methodName, paramTypes);
            method.setAccessible(true);
            method.invoke(instance, args);
            method.setAccessible(false);
        } catch (NoSuchMethodException e) {
            plugin.getLogger().warning("Method " + methodName + " not found in " + instance.getClass().getSimpleName() + ", skipping");
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to invoke " + methodName + " in " + instance.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    private void assignManagersToFields() {
        try {
            for (Object manager : managers.values()) {
                for (var field : plugin.getClass().getDeclaredFields()) {
                    if (field.getType().isAssignableFrom(manager.getClass())) {
                        field.setAccessible(true);
                        field.set(plugin, manager);
                        field.setAccessible(false);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            plugin.getLogger().severe("Failed to assign manager to field: " + e.getMessage());
        }
    }

    private void registerCommand(CoreCommandAdapter command) {
        CoreCommand annotation = command.getAnnotation();
        String commandName = annotation.name();
        org.bukkit.command.PluginCommand pluginCommand = ((JavaPlugin) plugin).getCommand(commandName);
        if (pluginCommand != null) {
            pluginCommand.setExecutor(command);
        } else {
            plugin.getLogger().warning("Command '" + commandName + "' not found in plugin.yml");
        }
    }

    private void registerListener(CoreListenerAdapter listener) {
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    public Object getManager(String name) {
        return managers.get(name);
    }

    public CoreCommandHandler getCommand(String name) {
        CoreCommandAdapter adapter = commands.get(name);
        return adapter != null ? adapter.getCoreCommand() : null;
    }

    public CoreListenerInterface getListener(String name) {
        CoreListenerAdapter adapter = listeners.get(name);
        return adapter != null ? adapter.getCoreListener() : null;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public FileConfiguration getMessages() {
        return messages;
    }
}