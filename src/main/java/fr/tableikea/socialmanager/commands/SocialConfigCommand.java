package fr.tableikea.socialmanager.commands;

import fr.tableikea.socialmanager.Main;
import fr.tableikea.socialmanager.utils.MessageFormats;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SocialConfigCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if(sender instanceof Player player){
            if(args.length == 0){
                Main.getInstance().getSocialActionsManager().sendPluginInformation(player);
            }else{
                if(args[0].equalsIgnoreCase("configurationReload")){
                    // Reload config
                    Main.getInstance().reloadConfig();
                    Main.getInstance().getConfig().options().copyDefaults(true);
                    MessageFormats.send(player, "config-reloaded");
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