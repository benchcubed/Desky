package dev.benchurchill.desky;

import dev.benchurchill.desky.command.CommandManager;
import org.bukkit.plugin.java.JavaPlugin;

public class DeskyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        try {
            Class.forName("dev.benchurchill.desky.command.commands.office.OfficeCommand");
            getLogger().info("OfficeCommand class loaded successfully!");
        } catch (ClassNotFoundException e) {
            getLogger().severe("Failed to find OfficeCommand class!");
            e.printStackTrace();
        }

        getLogger().info("OfficeBuilder enabled.");
        CommandManager commandManager = new CommandManager(this);
        commandManager.registerCommands();
    }

    @Override
    public void onDisable() {
        getLogger().info("OfficeBuilder disabled.");
    }
}
