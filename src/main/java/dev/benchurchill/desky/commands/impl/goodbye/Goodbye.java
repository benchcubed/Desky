package dev.benchurchill.desky.commands.impl.goodbye;
import dev.benchurchill.desky.annotations.CommandInfo;
import dev.benchurchill.desky.annotations.RootCommand;
import dev.benchurchill.desky.commands.DeskyCommandResult;
import dev.benchurchill.desky.commands.EDeskyCommandResultStatus;
import dev.benchurchill.desky.commands.IDeskyCommand;
import org.bukkit.command.CommandSender;

@RootCommand
@CommandInfo(name = "goodbye")
public class Goodbye extends IDeskyCommand {

    @Override
    public DeskyCommandResult execute(CommandSender sender, String[] args) throws IllegalStateException {
        DeskyCommandResult result = super.execute(sender, args);

        return switch (result.getStatus()) {
            case FAILED, DELEGATED -> result;
            case PROCESSED, NO_PERMISSION, PROCESSING, TIMEOUT, UNKNOWN -> {
                sender.sendMessage("Goodbye!");
                yield new DeskyCommandResult(EDeskyCommandResultStatus.PROCESSED);
            }
            // should never get here
            default -> throw new IllegalStateException("Unexpected status: " + result.getStatus());
        };
    }

}
