package fr.tableikea.socialmanager;

import fr.tableikea.socialmanager.commands.FriendsCommand;
import fr.tableikea.socialmanager.commands.SocialConfig;
import fr.tableikea.socialmanager.listeners.FriendsGuiEvent;
import fr.tableikea.socialmanager.utils.MessageUtils;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;
        MessageUtils.init(this);
        getCommand("friends").setExecutor(new FriendsCommand());
        getCommand("socialconfig").setExecutor(new SocialConfig());

        getServer().getPluginManager().registerEvents(new FriendsGuiEvent(), this);
    }

    @Override
    public void onDisable() {
        // Rien à faire ici pour l'instant
    }

    public static Main getInstance() {
        return instance;
    }
}
