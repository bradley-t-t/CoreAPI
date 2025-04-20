package com.trenton.coreapi.api;

import org.bukkit.command.CommandSender;

public interface CoreCommandHandler {
    boolean execute(CommandSender sender, String label, String[] args);
}