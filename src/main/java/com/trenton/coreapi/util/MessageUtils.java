package com.trenton.coreapi.util;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

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

    public static void sendMessage(Plugin plugin, FileConfiguration messages, CommandSender sender, String key, Object... args) {
        String message = messages.getString(key, "");
        if (message.isEmpty()) return;
        message = replacePlaceholders(message, args);
        message = ChatColor.translateAlternateColorCodes('&', message);
        sender.sendMessage(message);
    }

    public static void sendActionBar(Plugin plugin, FileConfiguration messages, Player player, String key, Object... args) {
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
            player.sendTitle(title, subtitle, 10, 60, 10); // Fade in: 0.5s, Stay: 3s, Fade out: 0.5s
        }
    }

    private static String replacePlaceholders(String message, Object... args) {
        if (args.length == 0) return message;
        if (args[0] instanceof Quest quest) {
            message = message.replace("{quest}", quest.getObjective())
                    .replace("{amount}", String.valueOf(quest.getAmount()));
            if (args.length > 1 && args[1] instanceof Integer progress) {
                message = message.replace("{progress}", String.valueOf(progress));
            }
            if (args.length > 1 && args[1] instanceof Player player) {
                message = message.replace("{player}", player.getName());
            }
        } else if (args[0] instanceof Player player) {
            message = message.replace("{player}", player.getName());
        }
        if (args.length > 1 && args[1] instanceof Integer time) {
            message = message.replace("{time}", String.valueOf(time));
        }
        return message;
    }

    public static class Quest {
        private final String objective;
        private final int amount;

        public Quest(String objective, int amount) {
            this.objective = objective;
            this.amount = amount;
        }

        public String getObjective() {
            return objective;
        }

        public int getAmount() {
            return amount;
        }
    }
}