package dev.benchurchill.desky.commands.impl.hello;

import dev.benchurchill.desky.annotations.CommandInfo;
import dev.benchurchill.desky.annotations.RootCommand;
import dev.benchurchill.desky.commands.DeskyCommandResult;
import dev.benchurchill.desky.commands.EDeskyCommandResultStatus;
import dev.benchurchill.desky.commands.IDeskyCommand;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

@RootCommand
@CommandInfo(name = "hello")
public class Hello extends IDeskyCommand {

    @Override
    public DeskyCommandResult execute(CommandSender sender, String[] args) {
        sender.sendMessage("Hello!");
        return new DeskyCommandResult(EDeskyCommandResultStatus.PROCESSED);
    }
}
