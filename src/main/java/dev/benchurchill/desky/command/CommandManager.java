package dev.benchurchill.desky.command;

import dev.benchurchill.desky.annotations.CommandInfo;
import dev.benchurchill.desky.command.BaseCommand;
import dev.benchurchill.desky.command.DeskyCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class CommandManager implements CommandExecutor, TabCompleter {

    private final Map<String, DeskyCommand> rootCommands = new HashMap<>();

    public CommandManager(JavaPlugin plugin) {
    }

    public void registerCommands() {
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forPackage("dev.benchurchill.desky.command.commands"))
                        .filterInputsBy(input -> input != null && input.endsWith(".class"))
                        .setScanners(Scanners.SubTypes)
        );

        Set<Class<? extends DeskyCommand>> commandClasses = reflections.getSubTypesOf(DeskyCommand.class).stream()
                .filter(cls -> cls.isAnnotationPresent(CommandInfo.class))
                .filter(cls -> !Modifier.isAbstract(cls.getModifiers()))
                .collect(Collectors.toSet());

        // Map of command path (list of parts) to command instance
        Map<List<String>, DeskyCommand> commandInstances = new HashMap<>();

        System.out.println("Scanning packages... [" + commandClasses.size() + "]");
        for (Class<? extends DeskyCommand> cmdClass : commandClasses) {
            System.out.println("Scanning folder: " + cmdClass.getName() + " from package " + cmdClass.getPackageName());
            try {
                DeskyCommand cmdInstance = cmdClass.getDeclaredConstructor().newInstance();

                CommandInfo info = cmdClass.getAnnotation(CommandInfo.class);
                String name = info.name().toLowerCase();

                // === NEW: Calculate command path based on package structure ===
                String basePackage = "dev.benchurchill.desky.command.commands";
                String fullPackage = cmdClass.getPackageName();

                String subPackages = "";
                if (fullPackage.equals(basePackage)) {
                    subPackages = "";
                } else if (fullPackage.startsWith(basePackage + ".")) {
                    subPackages = fullPackage.substring(basePackage.length() + 1);
                } else {
                    System.out.println("[registerCommands] Warning: Package '" + fullPackage + "' not under base package '" + basePackage + "'");
                    subPackages = "";
                }

                List<String> allSubPackages = subPackages.isEmpty() ?
                        Collections.emptyList() :
                        Arrays.asList(subPackages.split("\\."));

                // Ensure at least one root package (directly under 'commands')
                String rootPackage = allSubPackages.isEmpty() ? null : allSubPackages.get(0);

                // The rest are subcommand packages (excluding the root)
                List<String> subCommandPackages = allSubPackages.size() > 1 ?
                        allSubPackages.subList(1, allSubPackages.size()) :
                        Collections.emptyList();

                List<String> path = new ArrayList<>();
                if (rootPackage != null) {
                    path.add(rootPackage);
                }
                // Add the subcommand packages if any
                path.addAll(subCommandPackages);

                // Only add the command name if it’s NOT equal to the last path segment (to avoid duplication)
                if (path.isEmpty() || !path.get(path.size() - 1).equalsIgnoreCase(name)) {
                    path.add(name);
                }


                System.out.println("[registerCommands] Command path: " + path);

                // Save the command instance with the full path
                commandInstances.put(path, cmdInstance);
                System.out.println("Added command instance: " + name);
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getLogger().severe("Failed to instantiate command class: " + cmdClass.getName());
            }
        }
        System.out.println("Command packages scanned...");

        // Now build command tree
        rootCommands.clear();

        for (Map.Entry<List<String>, DeskyCommand> entry : commandInstances.entrySet()) {
            List<String> path = entry.getKey();
            DeskyCommand command = entry.getValue();

            System.out.println("[registerCommands] PATH: " + path);

            if (path.isEmpty()) continue;

            if (path.size() == 1) {
                // Root command
                String rootName = path.get(0);
                rootCommands.put(rootName, command);
                System.out.println("[registerCommands] Registered root: " + rootName);

                PluginCommand pluginCommand = Bukkit.getPluginCommand(rootName);
                if (pluginCommand == null) {
                    Bukkit.getLogger().warning("PluginCommand is null for command: " + rootName + ". Is it declared in plugin.yml?");
                } else {
                    System.out.println("Setting executor and tab completer for " + pluginCommand.getName() + ":" + pluginCommand.getLabel());
                    pluginCommand.setExecutor(this);
                    pluginCommand.setTabCompleter(this);
                }
            } else {
                // Subcommand, possibly deeply nested
                DeskyCommand parent = findParentCommandRecursive(rootCommands, path.subList(0, path.size() - 1));

                if (parent instanceof BaseCommand) {
                    ((BaseCommand) parent).registerSubcommand(command);
                    System.out.println("[registerCommands] Registered subcommand: " + path + " under parent: " + parent.getClass().getSimpleName());
                } else {
                    System.out.println("[registerCommands] Failed to register subcommand: " + path + " (parent not found or invalid)");
                }
            }
        }
    }

    private DeskyCommand findParentCommandRecursive(Map<String, DeskyCommand> currentLevel, List<String> path) {
        if (path.isEmpty()) return null;

        DeskyCommand current = currentLevel.get(path.get(0));
        if (current == null) return null;

        if (path.size() == 1) {
            return current;
        }

        return findParentCommandRecursive(current.getSubcommands(), path.subList(1, path.size()));
    }

    private Optional<CommandMatch> findCommand(String label, String[] args) {
        Map<String, DeskyCommand> currentLevel = rootCommands;
        DeskyCommand lastCommand = null;
        int lastIndex = -1;

        System.out.println("[findCommand] Root commands available: " + rootCommands.keySet());
        System.out.println("[findCommand] Label: " + label);
        System.out.println("[findCommand] Args: " + Arrays.toString(args));

        // Try to match the root command from label
        String root = label.toLowerCase();
        if (currentLevel.containsKey(root)) {
            lastCommand = currentLevel.get(root);
            currentLevel = lastCommand.getSubcommands();
            lastIndex = 0;
            System.out.println("[findCommand] Matched root command: " + root);
        } else {
            System.out.println("[findCommand] Root command not found for label: " + root);
            return Optional.empty();
        }

        // Process subcommands from args
        for (int i = 0; i < args.length; i++) {
            String arg = args[i].toLowerCase();
            System.out.println("[findCommand] Checking subcommand: " + arg);
            if (currentLevel.containsKey(arg)) {
                lastCommand = currentLevel.get(arg);
                currentLevel = lastCommand.getSubcommands();
                lastIndex = i + 1;
                System.out.println("[findCommand] Matched subcommand: " + arg);
            } else {
                System.out.println("[findCommand] No match for subcommand: " + arg + ", stopping search.");
                break;
            }
        }

        // Remaining args after matched command path are params
        String[] params = Arrays.copyOfRange(args, lastIndex, args.length);
        System.out.println("[findCommand] Matched command: " + lastCommand.getClass().getSimpleName() + ", Params: " + Arrays.toString(params));

        return Optional.of(new CommandMatch(lastCommand, params));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, String label, String[] args) {
        System.out.println("EXECUTING COMMAND: \" + cmd.getLabel() + \" WITH ARGS [" + Arrays.stream(args).map(s -> s) + "]");
        DeskyCommand command = rootCommands.get(label.toLowerCase());
        if (command == null) {
            sender.sendMessage("Unknown command");
            return true;
        }

        return command.execute(new CommandContext(sender, label, args), args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        Map<String, DeskyCommand> currentLevel = rootCommands;
        DeskyCommand currentCommand = null;

        for (int i = 0; i < args.length - 1; i++) {
            String arg = args[i].toLowerCase();
            if (!currentLevel.containsKey(arg)) {
                return Collections.emptyList();
            }
            currentCommand = currentLevel.get(arg);
            currentLevel = currentCommand.getSubcommands();
        }

        String lastArg = args.length > 0 ? args[args.length - 1].toLowerCase() : "";
        List<String> suggestions = new ArrayList<>();

        for (String key : currentLevel.keySet()) {
            if (key.startsWith(lastArg)) {
                suggestions.add(key);
            }
        }

        return suggestions;
    }

    private static class CommandMatch {
        final DeskyCommand command;
        final String[] params;

        CommandMatch(DeskyCommand command, String[] params) {
            this.command = command;
            this.params = params;
        }
    }
}
