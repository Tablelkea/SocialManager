package fr.tableikea.socialmanager;

import fr.tableikea.socialmanager.commands.FriendsCommand;
import fr.tableikea.socialmanager.listeners.FriendsGuiEvent;
import fr.tableikea.socialmanager.utils.MessageUtils;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        MessageUtils.init(this);
        getCommand("friends").setExecutor(new FriendsCommand());
        getServer().getPluginManager().registerEvents(new FriendsGuiEvent(), this);
    }

    @Override
    public void onDisable() {
        // Rien à faire ici pour l'instant
    }
}
