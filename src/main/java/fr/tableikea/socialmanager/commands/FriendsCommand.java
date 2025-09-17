package fr.tableikea.socialmanager.commands;

import fr.tableikea.socialmanager.models.Profil;
import fr.tableikea.socialmanager.models.SocialActions;
import fr.tableikea.socialmanager.utils.ItemBuilder;
import fr.tableikea.socialmanager.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;

public class FriendsCommand implements TabExecutor, Listener {

    public FriendsCommand() {
        Bukkit.getPluginManager().registerEvents(this, JavaPlugin.getProvidingPlugin(getClass()));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            MessageUtils.send(sender, "only_players");
            return true;
        }

        boolean allowSelfFriend = JavaPlugin.getProvidingPlugin(getClass()).getConfig().getBoolean("allow_self_friend", false);

        if (args.length == 0) {
            Inventory mainMenu = Bukkit.createInventory(null, 27, "§8§lSOCIAL - Menu");
            mainMenu.setItem(10, new ItemBuilder(Material.PLAYER_HEAD, 1, "§aListe d'amis", List.of("§7Voir vos amis.")).getItem());
            mainMenu.setItem(12, new ItemBuilder(Material.BOOK, 1, "§bDemandes d'amis", List.of("§7Voir vos demandes d'amis reçues.")).getItem());
            mainMenu.setItem(14, new ItemBuilder(Material.BARRIER, 1, "§cJoueurs bloqués", List.of("§7Voir les joueurs que vous avez bloqués.")).getItem());
            mainMenu.setItem(16, new ItemBuilder(Material.PAPER, 1, "§eTous les joueurs", List.of("§7Ajouter un joueur en ami via clic gauche.")).getItem());
            player.openInventory(mainMenu);
            return true;
        }

        String argument = args[0];
        Profil playerProfil = Profil.profils.computeIfAbsent(player, Profil::new);

        switch (argument.toLowerCase()) {
            case "invite" -> {
                Player target = Bukkit.getPlayer(args[1]);
                if (!allowSelfFriend && target == player) {
                    MessageUtils.send(player, "cannot_add_self");
                    return true;
                }
                if (target != null) {
                    Profil targetProfil = Profil.profils.computeIfAbsent(target, Profil::new);
                    if (targetProfil.friendRequestsReceived.contains(player)) {
                        MessageUtils.send(player, "already_sent_request");
                        return true;
                    }
                    targetProfil.friendRequestsReceived.add(player);
                    playerProfil.friendRequestsSended.add(target);
                    MessageUtils.send(player, "friend_request_sent", "{player}", target.getName());
                    MessageUtils.send(target, "friend_request_received", "{player}", player.getName());
                }
            }
            case "accept" -> {
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null && Profil.profils.containsKey(target)) {
                    Profil targetProfil = Profil.profils.get(target);
                    if (targetProfil.friendRequestsReceived.contains(player)) {
                        SocialActions.acceptFriendRequest(playerProfil, targetProfil);
                        MessageUtils.send(player, "friend_request_accepted", "{player}", target.getName());
                        MessageUtils.send(target, "your_request_accepted", "{player}", player.getName());
                    } else {
                        MessageUtils.send(player, "no_request_from_player");
                    }
                }
            }
            case "decline" -> {
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null && Profil.profils.containsKey(target)) {
                    Profil targetProfil = Profil.profils.get(target);
                    if (targetProfil.friendRequestsReceived.contains(player)) {
                        SocialActions.declineFriendRequest(playerProfil, targetProfil);
                        MessageUtils.send(player, "friend_request_declined", "{player}", target.getName());
                        MessageUtils.send(target, "your_request_declined", "{player}", player.getName());
                    } else {
                        MessageUtils.send(player, "no_request_from_player");
                    }
                }
            }
            case "remove" -> {
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null && Profil.profils.containsKey(target)) {
                    Profil targetProfil = Profil.profils.get(target);
                    if (targetProfil.friends.contains(player)) {
                        SocialActions.removeFriend(playerProfil, targetProfil);
                        MessageUtils.send(player, "friend_removed", "{player}", target.getName());
                        MessageUtils.send(target, "removed_by_friend", "{player}", player.getName());
                    } else {
                        MessageUtils.send(player, "not_in_friends");
                    }
                }
            }
            case "block" -> {
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    Profil targetProfil = Profil.profils.computeIfAbsent(target, Profil::new);
                    if (playerProfil.blocked.contains(target)) {
                        MessageUtils.send(player, "already_blocked");
                        return true;
                    }
                    SocialActions.blockPlayer(playerProfil, targetProfil);
                    MessageUtils.send(player, "player_blocked", "{player}", target.getName());
                    MessageUtils.send(target, "you_are_blocked", "{player}", player.getName());
                } else {
                    OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(args[1]);
                    if (offlineTarget != null && offlineTarget.hasPlayedBefore()) {
                        UUID targetUUID = offlineTarget.getUniqueId();
                        playerProfil.blocked.add(offlineTarget.getPlayer());
                        playerProfil.friends.remove(offlineTarget.getPlayer());
                        JavaPlugin plugin = JavaPlugin.getProvidingPlugin(getClass());
                        List<String> pending = plugin.getConfig().getStringList("pending_block_messages." + targetUUID);
                        pending.add(player.getUniqueId().toString());
                        plugin.getConfig().set("pending_block_messages." + targetUUID, pending);
                        plugin.saveConfig();
                        MessageUtils.send(player, "player_blocked", "{player}", offlineTarget.getName());
                    } else {
                        MessageUtils.send(player, "not_in_friends");
                    }
                }
            }
            case "unblock" -> {
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    if (playerProfil.blocked.contains(target)) {
                        SocialActions.unblockPlayer(playerProfil, Profil.profils.get(target));
                        MessageUtils.send(player, "player_unblocked", "{player}", target.getName());
                        MessageUtils.send(target, "you_are_unblocked", "{player}", player.getName());
                    } else {
                        MessageUtils.send(player, "not_in_blocked");
                    }
                }
            }
            case "list" -> {
                MessageUtils.send(player, "friends_list_header");
                if (playerProfil.friends.isEmpty()) {
                    MessageUtils.send(player, "friends_list_empty");
                } else {
                    for (Player friend : playerProfil.friends) {
                        MessageUtils.send(player, "friends_list_entry", "{player}", friend.getName());
                    }
                }
                MessageUtils.send(player, "friends_list_footer");
            }
            default -> MessageUtils.send(player, "invalid_argument");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

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

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        JavaPlugin plugin = JavaPlugin.getProvidingPlugin(getClass());
        List<String> senders = plugin.getConfig().getStringList("pending_block_messages." + player.getUniqueId());
        if (senders != null && !senders.isEmpty()) {
            for (String senderUUID : senders) {
                Player sender = Bukkit.getPlayer(UUID.fromString(senderUUID));
                String senderName = sender != null ? sender.getName() : Bukkit.getOfflinePlayer(UUID.fromString(senderUUID)).getName();
                MessageUtils.send(player, "you_are_blocked", "{player}", senderName);
            }
            plugin.getConfig().set("pending_block_messages." + player.getUniqueId(), null);
            plugin.saveConfig();
        }
    }
}
