package dev.benchurchill.desky;

import dev.benchurchill.desky.commands.DeskyCommandManager;
import org.bukkit.plugin.java.JavaPlugin;

public class DeskyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        try {
            Class.forName("dev.benchurchill.desky.commands.commands.office.OfficeCommand");
            getLogger().info("OfficeCommand class loaded successfully!");

            DeskyCommandManager commandManager = new DeskyCommandManager(this, "dev.benchurchill.desky.commands.impl.base");
            commandManager.loadCommands();

        } catch (ClassNotFoundException e) {
            getLogger().severe("Failed to find OfficeCommand class!");
            e.printStackTrace();
        }

        getLogger().info("OfficeBuilder enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("OfficeBuilder disabled.");
    }
}
