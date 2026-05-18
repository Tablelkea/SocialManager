package fr.tableikea.socialmanager.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

public class MessageFormats {
    private static FileConfiguration config;

    public static void init(@NonNull JavaPlugin plugin) {
        config = plugin.getConfig();
    }

    public static @NonNull String getMessage(String key, String @NonNull ... replacements) {
        String msg = config.getString("messages." + key, "");
        for (int i = 0; i < replacements.length; i += 2) {
            msg = msg.replace(replacements[i], replacements[i + 1]);
        }
        return config.getString("messages.prefix") + msg;
    }

    public static void send(@NonNull Player player, String key, String... replacements) {
        player.sendMessage(getMessage(key, replacements));
    }

    public static void send(@NonNull CommandSender sender, String key, String... replacements) {
        sender.sendMessage(getMessage(key, replacements));
    }

    public static String getSettings(String folder, String key, String @NonNull ... replacements) {
        String msg = config.getString(folder + "." + key, "");
        for (int i = 0; i < replacements.length; i += 2) {
            msg = msg.replace(replacements[i], replacements[i + 1]);
        }
        return msg;
    }
}
