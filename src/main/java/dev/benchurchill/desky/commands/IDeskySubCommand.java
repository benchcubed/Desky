package dev.benchurchill.desky.commands;

import org.bukkit.command.CommandSender;

public abstract class IDeskySubCommand {
    public abstract DeskyCommandResult execute(CommandSender sender, String[] args);
}