package com.trenton.coreapi.util;

import com.google.common.reflect.ClassPath;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class ReflectionUtils {
    public static List<Class<?>> scanClasses(Plugin plugin, String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        try {
            ClassPath classPath = ClassPath.from(plugin.getClass().getClassLoader());
            for (ClassPath.ClassInfo info : classPath.getTopLevelClassesRecursive(packageName)) {
                Class<?> clazz = info.load();
                if (!clazz.isInterface() && !clazz.isEnum()) {
                    classes.add(clazz);
                }
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to scan classes: " + e.getMessage());
        }
        return classes;
    }
}