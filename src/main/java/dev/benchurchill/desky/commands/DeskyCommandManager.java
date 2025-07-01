package dev.benchurchill.desky.commands;

import dev.benchurchill.desky.annotations.CommandInfo;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class DeskyCommandManager implements CommandExecutor {

    private final Map<String, IDeskyCommand> commandMap = new HashMap<>();
    private final Map<String, String> aliasToBase = new HashMap<>();
    private final JavaPlugin plugin;
    private final String basePackage;

    public void loadCommands() {
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> baseCommands = reflections.getTypesAnnotatedWith(CommandInfo.class);

        YamlConfiguration pluginYml = new YamlConfiguration();
        try (InputStream in = plugin.getResource("plugin.yml")) {
            if (in != null) {
                pluginYml.load(new InputStreamReader(in));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ConfigurationSection commands = pluginYml.getConfigurationSection("commands");
        for (Class<?> clazz : baseCommands) {
            try {
                if (!IDeskyCommand.class.isAssignableFrom(clazz)) continue;

                CommandInfo info = clazz.getAnnotation(CommandInfo.class);
                IDeskyCommand instance = (IDeskyCommand) clazz.getDeclaredConstructor().newInstance();

                commandMap.put(info.name().toLowerCase(), instance);

                for (String alias : info.aliases()) {
                    aliasToBase.put(alias.toLowerCase(), info.name().toLowerCase());
                }

                // Register command w/Bukkit
                Objects.requireNonNull(plugin.getCommand(info.name())).setExecutor(this);

                // Cross-check plugin.yml with registered commands (Should maybe be done at compile time but we move)
                if (commands == null) {
                    throw new NotFoundException("Command " + info.name() + "not found in plugin.yml");
                }
            } catch (NotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                     IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        assert commands != null;
        Set<String> ymlCommands = commands.getKeys(false);
        try {
            for (String command : ymlCommands) {
                if (commandMap.containsKey(command)) continue;

                throw new NotFoundException("Command " + command + " not found in plugin.yml");
            }
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        IDeskyCommand commandClass = commandMap.get(label);
        if (commandClass == null) {
            sender.sendMessage("§cUnknown command.");
            return false;
        }

        CommandInfo info = commandClass.getClass().getAnnotation(CommandInfo.class);
        if (!info.permission().isEmpty() && !sender.hasPermission(info.permission())) {
            sender.sendMessage("§cYou do not have permission to use this command.");
            return false;
        }

        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        // !!Do this next!!
        // TODO: check sub args length and execute correct class as required.
        // TODO: return the result of the subcommand

        DeskyCommandResult commandResult = commandClass.execute(sender, subArgs);
        // Handle result logic if required.

        // Otherwise, return if command failed as boolean to onCommand func.
        return commandResult.getStatus().failed();
    }
}
