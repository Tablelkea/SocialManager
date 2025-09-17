package fr.tableikea.socialmanager.commands;

import fr.tableikea.socialmanager.models.Profil;
import fr.tableikea.socialmanager.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FriendsCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if(sender instanceof Player player){

            if(args.length == 0){
                Inventory friendsMenu = Bukkit.createInventory(null, 27, "§8§lAMIS");

                if(Profil.profils.containsKey(player)) {
                    for (Player friends : Profil.profils.get(player).friends) {
                        friendsMenu.addItem(new ItemBuilder(Material.PLAYER_HEAD, 1, "§a" + friends.getName(), List.of("Right-click: Block this Player", "Left-click: Remove this Player")).getItem());
                        player.sendMessage(friends.toString());
                    }
                }
                player.openInventory(friendsMenu);

            }else{
                String argument = args[0];
                Profil playerProfil;

                if(Profil.profils.containsKey(player)){
                    playerProfil = Profil.profils.get(player);
                }else{
                    playerProfil = new Profil(player);
                    Profil.profils.put(player, playerProfil);
                }

                if(argument.equalsIgnoreCase("invite")){
                    Player target = Bukkit.getPlayer(args[1]);

                    if(target == player){
                        player.sendMessage("§cVous ne pouvez pas vous envoyer une demande d'ami à vous-même.");
                        return false;
                    }

                    if(target != null){
                        Profil targetProfil;

                        if(Profil.profils.containsKey(target)){
                            targetProfil = Profil.profils.get(target);
                        }else{
                            targetProfil = new Profil(target);
                            Profil.profils.put(target, targetProfil);
                        }
                        if(targetProfil.friendRequestsReceived.contains(player)){
                            player.sendMessage("§cVous avez déjà envoyé une demande d'ami à ce joueur.");
                            return false;
                        }else{
                            targetProfil.friendRequestsReceived.add(player);
                            playerProfil.friendRequestsSended.add(target);
                            player.sendMessage("§aVous avez envoyé une demande d'ami à " + target.getName() + " !");
                            target.sendMessage("§aVous avez reçu une demande d'ami de " + player.getName() + " !");
                        }

                    }

                }else if(argument.equalsIgnoreCase("accept")){
                    Player target = Bukkit.getPlayer(args[1]);
                    if(target != null){
                        if(Profil.profils.containsKey(target)){
                            Profil targetProfil = Profil.profils.get(target);
                            if(targetProfil.friendRequestsReceived.contains(player)){
                                targetProfil.friendRequestsReceived.remove(player);
                                playerProfil.friendRequestsSended.remove(target);
                                targetProfil.friends.add(player);
                                playerProfil.friends.add(target);
                                player.sendMessage("§aVous avez accepté la demande d'ami de " + target.getName() + " !");
                                target.sendMessage("§a" + player.getName() + " a accepté votre demande d'ami !");
                            }else{
                                player.sendMessage("§cVous n'avez pas de demande d'ami de ce joueur.");
                            }
                        }
                    }

                }else if(argument.equalsIgnoreCase("decline")){
                    Player target = Bukkit.getPlayer(args[1]);
                    if(target != null){
                        if(Profil.profils.containsKey(target)){
                            Profil targetProfil = Profil.profils.get(target);
                            if(targetProfil.friendRequestsReceived.contains(player)){
                                targetProfil.friendRequestsReceived.remove(player);
                                playerProfil.friendRequestsSended.remove(target);
                                player.sendMessage("§cVous avez refusé la demande d'ami de " + target.getName() + " !");
                                target.sendMessage("§c" + player.getName() + " a refusé votre demande d'ami !");
                            }else{
                                player.sendMessage("§cVous n'avez pas de demande d'ami de ce joueur.");
                            }
                        }
                    }
                }else if(argument.equalsIgnoreCase("remove")){
                    Player target = Bukkit.getPlayer(args[1]);
                    if(target != null){
                        if(Profil.profils.containsKey(target)){
                            Profil targetProfil = Profil.profils.get(target);
                            if(targetProfil.friends.contains(player)){
                                targetProfil.friends.remove(player);
                                playerProfil.friends.remove(target);
                                player.sendMessage("§cVous avez supprimé " + target.getName() + " de votre liste d'amis.");
                                target.sendMessage("§c" + player.getName() + " vous a supprimé de sa liste d'amis.");
                            }else{
                                player.sendMessage("§cCe joueur n'est pas dans votre liste d'amis.");
                            }
                        }
                    }
                }else if(argument.equalsIgnoreCase("block")){
                    Player target = Bukkit.getPlayer(args[1]);
                    if(target != null){
                        Profil targetProfil;

                        if(Profil.profils.containsKey(target)){
                            targetProfil = Profil.profils.get(target);
                        }else{
                            targetProfil = new Profil(target);
                            Profil.profils.put(target, targetProfil);
                        }
                        if(playerProfil.blocked.contains(target)){
                            player.sendMessage("§cVous avez déjà bloqué ce joueur.");
                            return false;
                        }else{
                            playerProfil.blocked.add(target);
                            playerProfil.friends.remove(target);
                            targetProfil.friends.remove(player);
                            player.sendMessage("§aVous avez bloqué " + target.getName() + " !");
                            target.sendMessage("§cVous avez été bloqué par " + player.getName() + " !");
                        }

                    }

                }else if(argument.equalsIgnoreCase("unblock")){
                    Player target = Bukkit.getPlayer(args[1]);
                    if(target != null){
                        if(playerProfil.blocked.contains(target)){
                            playerProfil.blocked.remove(target);
                            player.sendMessage("§aVous avez débloqué " + target.getName() + " !");
                            target.sendMessage("§aVous avez été débloqué par " + player.getName() + " !");
                        }else{
                            player.sendMessage("§cCe joueur n'est pas dans votre liste de joueurs bloqués.");
                        }
                    }

                }else if(argument.equalsIgnoreCase("list")){
                    player.sendMessage("§6--- Liste d'amis ---");
                    if(playerProfil.friends.isEmpty()){
                        player.sendMessage("§cVous n'avez pas d'amis.");
                    }else{
                        for(Player friend : playerProfil.friends){
                            player.sendMessage("§a- " + friend.getName());
                        }
                    }
                    player.sendMessage("§6-------------------");

                }else{
                    player.sendMessage("§cArgument invalide. Utilisez /friends <invite|accept|decline|remove|block|unblock|list> [joueur]");
                }
            }


        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if(args.length == 1){
            return List.of("invite", "accept", "decline", "remove", "block", "unblock", "list");
        }else if(args.length == 2) {

            if(args[0].equalsIgnoreCase("invite")){
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> !name.equalsIgnoreCase(sender.getName()))
                        .filter(name -> {
                            if (sender instanceof Player player) {
                                Profil playerProfil = Profil.profils.get(player);
                                if (playerProfil != null) {
                                    Player target = Bukkit.getPlayer(name);
                                    return target != null && !playerProfil.friends.contains(target) && !playerProfil.blocked.contains(target) && !playerProfil.friendRequestsSended.contains(target);
                                }
                            }
                            return false;
                        })
                        .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                        .toList();
            }else if(args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("decline")){
                if(sender instanceof Player player){
                    Profil playerProfil = Profil.profils.get(player);
                    if(playerProfil != null){
                        return playerProfil.friendRequestsReceived.stream()
                                .map(Player::getName)
                                .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                                .toList();
                    }
                }
            }else if(args[0].equalsIgnoreCase("remove")) {
                if (sender instanceof Player player) {
                    Profil playerProfil = Profil.profils.get(player);
                    if (playerProfil != null) {
                        return playerProfil.friends.stream()
                                .map(Player::getName)
                                .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                                .toList();
                    }
                }
            }else if(args[0].equalsIgnoreCase("block")) {
                if (sender instanceof Player player) {
                    Profil playerProfil = Profil.profils.get(player);
                    if (playerProfil != null) {
                        return Bukkit.getOnlinePlayers().stream()
                                .map(Player::getName)
                                .filter(name -> !name.equalsIgnoreCase(sender.getName()))
                                .filter(name -> {
                                    Player target = Bukkit.getPlayer(name);
                                    return target != null && !playerProfil.friends.contains(target) && !playerProfil.blocked.contains(target);
                                })
                                .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                                .toList();
                    }
                }
            }else if(args[0].equalsIgnoreCase("unblock")) {
                if (sender instanceof Player player) {
                    Profil playerProfil = Profil.profils.get(player);
                    if (playerProfil != null) {
                        return playerProfil.blocked.stream()
                                .map(Player::getName)
                                .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                                .toList();
                    }
                }
            }else{
                return List.of();
            }

        }

        return List.of();
    }
}
