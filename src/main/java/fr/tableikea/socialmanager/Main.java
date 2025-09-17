package fr.tableikea.socialmanager;

import fr.tableikea.socialmanager.commands.FriendsCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getCommand("friends").setExecutor(new FriendsCommand());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
