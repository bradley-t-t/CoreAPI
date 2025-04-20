package com.trenton.coreapi.api;

import com.trenton.coreapi.annotations.CoreCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CoreCommandAdapter implements CommandExecutor {
    private final CoreCommandHandler coreCommand;
    private final CoreCommand annotation;

    public CoreCommandAdapter(CoreCommandHandler coreCommand, CoreCommand annotation) {
        this.coreCommand = coreCommand;
        this.annotation = annotation;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return coreCommand.execute(sender, label, args);
    }

    public CoreCommandHandler getCoreCommand() {
        return coreCommand;
    }

    public CoreCommand getAnnotation() {
        return annotation;
    }
}