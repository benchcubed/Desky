package dev.benchurchill.desky.command;

import org.bukkit.command.CommandSender;

public class CommandContext {
    private final CommandSender sender;
    private final String label;
    private final String[] args;

    public CommandContext(CommandSender sender, String label, String[] args) {
        this.sender = sender;
        this.label = label;
        this.args = args;
    }

    public CommandSender sender() {
        return sender;
    }

    public String label() {
        return label;
    }

    public String[] args() {
        return args;
    }

    public String arg(int index) {
        return index < args.length ? args[index] : null;
    }
}
