package dev.benchurchill.desky.command.commands.office;

import dev.benchurchill.desky.annotations.CommandInfo;
import dev.benchurchill.desky.command.BaseCommand;
import dev.benchurchill.desky.command.CommandContext;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(name = "office", permission = "desky.office")
public class OfficeCommand extends BaseCommand {

    public OfficeCommand() {
    }

    @Override
    public boolean execute(CommandContext context, String[] params) {
        context.sender().sendMessage("Welcome to the office!");
        return true;
    }

    @Override
    public List<String> tabComplete(CommandContext context, String[] params) {
        if (params.length == 0) {
            return new ArrayList<>(getSubcommands().keySet());
        }
        return super.tabComplete(context, params);
    }
}