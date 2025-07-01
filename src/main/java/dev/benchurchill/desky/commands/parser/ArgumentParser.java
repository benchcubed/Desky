package dev.benchurchill.desky.commands.parser;

import org.bukkit.command.CommandSender;

public interface ArgumentParser<T> {
    T parse(CommandSender sender, String input) throws IllegalArgumentException;
}