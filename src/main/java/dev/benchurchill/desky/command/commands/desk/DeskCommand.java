package dev.benchurchill.desky.command.commands.desk;

import dev.benchurchill.desky.annotations.CommandInfo;
import dev.benchurchill.desky.command.BaseCommand;
import dev.benchurchill.desky.command.CommandContext;
import dev.benchurchill.desky.command.DeskyCommand;

@CommandInfo(name = "desk", permission = "desky.desk")
public class DeskCommand extends BaseCommand {

    public DeskCommand() {

    }

    @Override
    public boolean execute(CommandContext context, String[] params) {
        // If subcommand handled it, return true immediately
        System.out.println("HANDLING DESK COMMAND");
        boolean didSucceed = super.execute(context, params);
        System.out.println("EXECUTING SUPER OF DESK COMMAND");
        if (didSucceed) {
            return true;
        }

        System.out.println("DIDNT SUCCEED :((");

        // No subcommand matched, run DeskCommand default logic
        context.sender().sendMessage("DESK COMMAND");
        return true;
    }
}
