package fr.tableikea.socialmanager.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MessageUtils {
    private static FileConfiguration config;

    public static void init(JavaPlugin plugin) {
        config = plugin.getConfig();
    }

    public static String getMessage(String key, String... replacements) {
        String msg = config.getString("messages." + key, "");
        for (int i = 0; i < replacements.length; i += 2) {
            msg = msg.replace(replacements[i], replacements[i + 1]);
        }
        return config.getString("messages.prefix") + msg;
    }

    public static void send(Player player, String key, String... replacements) {
        player.sendMessage(getMessage(key, replacements));
    }

    public static void send(CommandSender sender, String key, String... replacements) {
        sender.sendMessage(getMessage(key, replacements));
    }

    public static String getSettings(String folder, String key, String... replacements) {
        String msg = config.getString(folder + "." + key, "");
        for (int i = 0; i < replacements.length; i += 2) {
            msg = msg.replace(replacements[i], replacements[i + 1]);
        }
        return msg;
    }
}
