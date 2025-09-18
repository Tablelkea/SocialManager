package fr.tableikea.socialmanager.commands;

import fr.tableikea.socialmanager.Main;
import fr.tableikea.socialmanager.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SocialConfig implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if(sender instanceof Player player){
            if(args.length == 0){
                player.sendMessage("§6§lSocialManager §f- §ePlugin de gestion sociale");
                player.sendMessage("§eVersion §f: §a" + MessageUtils.getSettings("plugin-info", "version"));
                player.sendMessage("§eAuteur §f: §a" + MessageUtils.getSettings("plugin-info", "author"));
                player.sendMessage("§eGitHub §f: §a" + MessageUtils.getSettings("plugin-info", "website"));
            }else{
                if(args[0].equalsIgnoreCase("configurationReload")){
                    // Reload config
                    Main.getInstance().reloadConfig();
                    Main.getInstance().getConfig().options().copyDefaults(true);
                    MessageUtils.send(player, "config-reloaded");
                }else{
                    player.sendMessage("§cCommande inconnue !");
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if(args.length == 1){
            return List.of("configurationReload");
        }

        return List.of();
    }
}
