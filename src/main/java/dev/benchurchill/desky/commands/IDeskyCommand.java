package dev.benchurchill.desky.commands;

import dev.benchurchill.desky.annotations.CommandInfo;
import dev.benchurchill.desky.annotations.RootCommand;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public abstract class IDeskyCommand {
    public DeskyCommandResult execute(CommandSender sender, String[] args) throws IllegalStateException {
        // ? Maybe do other stuff here in the future... ?
        return checkPropagation(sender, args);
    }

    private DeskyCommandResult checkPropagation(CommandSender sender, String[] args) {
        try {
            if (shouldDispatch() && dispatch(sender, args))
                return new DeskyCommandResult(EDeskyCommandResultStatus.DELEGATED);
            return new DeskyCommandResult(EDeskyCommandResultStatus.PROCESSING); // fallback or override this method
        } catch (Exception e) {
            e.printStackTrace();
            return new DeskyCommandResult(EDeskyCommandResultStatus.FAILED, e.getMessage());
        }
    }

    private boolean dispatch(CommandSender sender, String[] args) {
        if (args.length == 0) return false;

        String subCommand = args[0];
        String[] remainingArgs = Arrays.copyOfRange(args, 1, args.length);
        String thisPackage = this.getClass().getPackage().getName();
        String subCommandClassName = thisPackage + "." + subCommand.toLowerCase() + "." + capitalize(subCommand);

        try {
            Class<?> clazz = Class.forName(subCommandClassName);
            if (DeskyCommandResult.IDeskyCommand.class.isAssignableFrom(clazz)) {
                DeskyCommandResult.IDeskyCommand instance = (DeskyCommandResult.IDeskyCommand) clazz.getDeclaredConstructor().newInstance();

                instance.execute(sender, remainingArgs);
                return true;
            }
        } catch (ClassNotFoundException e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }

    protected boolean shouldDispatch() {
        return this.getClass().isAnnotationPresent(RootCommand.class);
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return Character.toUpperCase(str.charAt(0)) + str.substring(1).toLowerCase();
    }
}