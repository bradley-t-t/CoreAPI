package com.trenton.coreapi.util;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtils {
    public static String formatEnumName(String enumName) {
        String[] parts = enumName.toLowerCase().split("_");
        StringBuilder formatted = new StringBuilder();
        for (String part : parts) {
            if (formatted.length() > 0) formatted.append(" ");
            formatted.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return formatted.toString();
    }

    public static void sendMessage(FileConfiguration messages, CommandSender sender, String key, Object... args) {
        String message = messages.getString(key, "");
        if (message.isEmpty()) return;
        message = replacePlaceholders(message, args);
        message = ChatColor.translateAlternateColorCodes('&', message);
        sender.sendMessage(message);
    }

    public static void sendActionBar(FileConfiguration messages, Player player, String key, Object... args) {
        String message = messages.getString(key, "");
        if (message.isEmpty()) return;
        message = replacePlaceholders(message, args);
        message = ChatColor.translateAlternateColorCodes('&', message);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }

    public static void broadcast(Plugin plugin, FileConfiguration messages, String key, Object... args) {
        String message = messages.getString(key, "");
        if (message.isEmpty()) return;
        message = replacePlaceholders(message, args);
        message = ChatColor.translateAlternateColorCodes('&', message);
        plugin.getServer().broadcastMessage(message);
    }

    public static void sendTitle(Plugin plugin, FileConfiguration messages, String titleKey, String subtitleKey, Object... args) {
        String title = messages.getString(titleKey, "");
        String subtitle = messages.getString(subtitleKey, "");
        if (title.isEmpty() && subtitle.isEmpty()) return;
        title = replacePlaceholders(title, args);
        subtitle = replacePlaceholders(subtitle, args);
        title = ChatColor.translateAlternateColorCodes('&', title);
        subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.sendTitle(title, subtitle, 10, 60, 10);
        }
    }

    private static String replacePlaceholders(String message, Object... args) {
        List<String> placeholders = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\{([^{}]+)\\}");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            placeholders.add(matcher.group(0));
        }

        String result = message;
        for (int i = 0; i < placeholders.size() && i < args.length; i++) {
            String placeholder = placeholders.get(i);
            String replacement = args[i] != null ? args[i].toString() : "";
            result = result.replace(placeholder, replacement);
        }

        return result;
    }
}