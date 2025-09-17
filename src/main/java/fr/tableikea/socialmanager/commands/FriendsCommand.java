package fr.tableikea.socialmanager.commands;

import fr.tableikea.socialmanager.models.Profil;
import fr.tableikea.socialmanager.utils.ItemBuilder;
import fr.tableikea.socialmanager.utils.MessageUtils;
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
                        MessageUtils.send(player, "cannot_add_self");
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
                            MessageUtils.send(player, "already_sent_request");
                            return false;
                        }else{
                            targetProfil.friendRequestsReceived.add(player);
                            playerProfil.friendRequestsSended.add(target);
                            MessageUtils.send(player, "friend_request_sent", "{player}", target.getName());
                            MessageUtils.send(target, "friend_request_received", "{player}", player.getName());
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
                                MessageUtils.send(player, "friend_request_accepted", "{player}", target.getName());
                                MessageUtils.send(target, "your_request_accepted", "{player}", player.getName());
                            }else{
                                MessageUtils.send(player, "no_request_from_player");
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
                                MessageUtils.send(player, "friend_request_declined", "{player}", target.getName());
                                MessageUtils.send(target, "your_request_declined", "{player}", player.getName());
                            }else{
                                MessageUtils.send(player, "no_request_from_player");
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
                                MessageUtils.send(player, "friend_removed", "{player}", target.getName());
                                MessageUtils.send(target, "removed_by_friend", "{player}", player.getName());
                            }else{
                                MessageUtils.send(player, "not_in_friends");
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
                            MessageUtils.send(player, "already_blocked");
                            return false;
                        }else{
                            playerProfil.blocked.add(target);
                            playerProfil.friends.remove(target);
                            targetProfil.friends.remove(player);
                            MessageUtils.send(player, "player_blocked", "{player}", target.getName());
                            MessageUtils.send(target, "you_are_blocked", "{player}", player.getName());
                        }

                    }

                }else if(argument.equalsIgnoreCase("unblock")){
                    Player target = Bukkit.getPlayer(args[1]);
                    if(target != null){
                        if(playerProfil.blocked.contains(target)){
                            playerProfil.blocked.remove(target);
                            MessageUtils.send(player, "player_unblocked", "{player}", target.getName());
                            MessageUtils.send(target, "you_are_unblocked", "{player}", player.getName());
                        }else{
                            MessageUtils.send(player, "not_in_blocked");
                        }
                    }

                }else if(argument.equalsIgnoreCase("list")){
                    MessageUtils.send(player, "friends_list_header");
                    if(playerProfil.friends.isEmpty()){
                        MessageUtils.send(player, "friends_list_empty");
                    }else{
                        for(Player friend : playerProfil.friends){
                            MessageUtils.send(player, "friends_list_entry", "{player}", friend.getName());
                        }
                    }
                    MessageUtils.send(player, "friends_list_footer");

                }else{
                    MessageUtils.send(player, "invalid_argument");
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
