package fr.tableikea.socialmanager;

import fr.tableikea.socialmanager.commands.FriendsCommand;
import fr.tableikea.socialmanager.commands.SocialConfigCommand;
import fr.tableikea.socialmanager.listeners.FriendsGuiListener;
import fr.tableikea.socialmanager.listeners.PlayerJoinListener;
import fr.tableikea.socialmanager.manager.FriendsManager;
import fr.tableikea.socialmanager.manager.GuiManager;
import fr.tableikea.socialmanager.manager.SocialActionsManager;
import fr.tableikea.socialmanager.utils.MessageFormats;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;

    private FriendsManager friendsManager;
    private SocialActionsManager socialActionsManager;
    private GuiManager guiManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        friendsManager = new FriendsManager();
        socialActionsManager = new SocialActionsManager();
        guiManager = new GuiManager();

        instance = this;

        MessageFormats.init(this);

        getCommand("friends").setExecutor(new FriendsCommand());
        getCommand("socialconfig").setExecutor(new SocialConfigCommand());

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new FriendsGuiListener(), this);
    }

    @Override
    public void onDisable() {
    }

    public static Main getInstance() {
        return instance;
    }

    public FriendsManager getFriendsManager() {return friendsManager;}

    public SocialActionsManager getSocialActionsManager() {return socialActionsManager;}

    public GuiManager getGuiManager() {return guiManager;}
}