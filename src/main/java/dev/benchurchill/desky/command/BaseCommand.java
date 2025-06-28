package dev.benchurchill.desky.command;

import dev.benchurchill.desky.annotations.CommandInfo;

import java.util.*;

public abstract class BaseCommand implements DeskyCommand {

    protected final Map<String, DeskyCommand> subcommands = new HashMap<>();

    @Override
    public boolean execute(CommandContext context, String[] params) {
        if (params.length > 0) {
            String sub = params[0].toLowerCase();
            DeskyCommand subcommand = subcommands.get(sub);
            if (subcommand != null) {
                return subcommand.execute(context, Arrays.copyOfRange(params, 1, params.length));
            }
        }
        return false;
    }

    @Override
    public Map<String, DeskyCommand> getSubcommands() {
        return subcommands;
    }

    public void registerSubcommand(DeskyCommand command) {
        CommandInfo info = command.getClass().getAnnotation(CommandInfo.class);
        if (info != null) {
            subcommands.put(info.name().toLowerCase(), command);
        }
    }

    // Default tabComplete and execute can be overridden
    @Override
    public List<String> tabComplete(CommandContext context, String[] params) {
        if (params.length == 0) {
            return new ArrayList<>(subcommands.keySet());
        }
        return Collections.emptyList();
    }
}
