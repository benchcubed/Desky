package dev.benchurchill.desky.command.commands.desk.book;

import dev.benchurchill.desky.annotations.CommandInfo;
import dev.benchurchill.desky.command.BaseCommand;
import dev.benchurchill.desky.command.CommandContext;
import dev.benchurchill.desky.command.DeskyCommand;
import dev.benchurchill.desky.command.commands.AbstractCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CommandInfo(name = "book")
public class DeskBookCommand extends BaseCommand {

    @Override
    public boolean execute(CommandContext context, String[] params) {
        context.sender().sendMessage("Book desk command triggered");
        return false;
    }

    @Override
    public List<String> tabComplete(CommandContext context, String[] params) {
        return List.of();
    }

    @Override
    public Map<String, DeskyCommand> getSubcommands() {
        return Map.of();
    }
}
