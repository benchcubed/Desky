package dev.benchurchill.desky.command;

import java.util.List;
import java.util.Map;

public interface DeskyCommand {
    boolean execute(CommandContext context, String[] params);
    List<String> tabComplete(CommandContext context, String[] params);

    // Retrieve subcommands mapped by name
    Map<String, DeskyCommand> getSubcommands();
}
