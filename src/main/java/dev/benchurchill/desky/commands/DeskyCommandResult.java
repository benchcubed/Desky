package dev.benchurchill.desky.commands;

import kotlinx.serialization.Required;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;
import java.util.Arrays;

@Getter
@NoArgsConstructor
@RequiredArgsConstructor
public class DeskyCommandResult {
    private EDeskyCommandResultStatus status;
    private String reason;

    public DeskyCommandResult(EDeskyCommandResultStatus eDeskyCommandResultStatus, String message) {
    }

    public abstract class IDeskyCommand {

        public abstract DeskyCommandResult execute(CommandSender sender, String[] args);

        private String capitalize(String str) {
            if (str == null || str.isEmpty()) return str;
            return Character.toUpperCase(str.charAt(0)) + str.substring(1).toLowerCase();
        }
    }

    public DeskyCommandResult(EDeskyCommandResultStatus status) {
        this.status = status;
    }
}
