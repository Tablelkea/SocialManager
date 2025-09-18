package fr.tableikea.socialmanager.manager;

import fr.tableikea.socialmanager.models.Profil;
import fr.tableikea.socialmanager.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class FriendCommandHandler {

    private static final Set<UUID> bypassFriendLimitPlayers = new HashSet<>();

    public static void setBypassFriendLimit(Player player, boolean bypass) {
        if (bypass) {
            bypassFriendLimitPlayers.add(player.getUniqueId());
        } else {
            bypassFriendLimitPlayers.remove(player.getUniqueId());
        }
    }

    public static boolean isBypassFriendLimit(Player player) {
        return bypassFriendLimitPlayers.contains(player.getUniqueId());
    }

    public static boolean handleInvite(Player player, String[] args, Profil playerProfil, boolean allowSelfFriend) {
        if (args.length < 2) {
            MessageUtils.send(player, "invalid_argument");
            return true;
        }
        Player target = Bukkit.getPlayer(args[1]);
        int maxFriends = JavaPlugin.getProvidingPlugin(FriendCommandHandler.class).getConfig().getInt("settings.max_friends", 100);

        boolean force = args.length > 2 && args[2].equalsIgnoreCase("force");

        if (!allowSelfFriend && target == player) {
            MessageUtils.send(player, "cannot_add_self");
            return true;
        }
        if (target != null) {
            Profil targetProfil = Profil.profils.computeIfAbsent(target, Profil::new);

            boolean bypassSender = isBypassFriendLimit(player);
            boolean bypassTarget = isBypassFriendLimit(target);

            if (force && bypassSender && !bypassTarget) {
                // Ajout direct dans les deux listes d'amis, sans tenir compte de la limite
                playerProfil.friends.add(target);
                targetProfil.friends.add(player);
                playerProfil.friendRequestsSended.remove(target);
                targetProfil.friendRequestsReceived.remove(player);
                MessageUtils.send(player, "friend_force_added", "{player}", target.getName());
                MessageUtils.send(target, "friend_force_added_by", "{player}", player.getName());
                return true;
            }

            if (!bypassSender && playerProfil.friends.size() >= maxFriends) {
                MessageUtils.send(player, "max_friends_reached");
                return true;
            }
            if (!bypassTarget && targetProfil.friends.size() >= maxFriends) {
                MessageUtils.send(player, "target_max_friends_reached", "{player}", target.getName());
                return true;
            }
            if (targetProfil.friendRequestsReceived.contains(player)) {
                MessageUtils.send(player, "already_sent_request");
                return true;
            }
            targetProfil.friendRequestsReceived.add(player);
            playerProfil.friendRequestsSended.add(target);
            MessageUtils.send(player, "friend_request_sent", "{player}", target.getName());
            MessageUtils.send(target, "friend_request_received", "{player}", player.getName());
        }
        return true;
    }

    public static boolean handleAccept(Player player, String[] args, Profil playerProfil) {
        if (args.length < 2) {
            MessageUtils.send(player, "invalid_argument");
            return true;
        }
        Player target = Bukkit.getPlayer(args[1]);
        int maxFriends = JavaPlugin.getProvidingPlugin(FriendCommandHandler.class).getConfig().getInt("settings.max_friends", 100);

        if (target != null && Profil.profils.containsKey(target)) {
            Profil targetProfil = Profil.profils.get(target);
            if (!isBypassFriendLimit(player) && playerProfil.friends.size() >= maxFriends) {
                MessageUtils.send(player, "max_friends_reached");
                return true;
            }
            if (!isBypassFriendLimit(target) && targetProfil.friends.size() >= maxFriends) {
                MessageUtils.send(player, "target_max_friends_reached", "{player}", target.getName());
                return true;
            }
            if (targetProfil.friendRequestsReceived.contains(player)) {
                SocialActions.acceptFriendRequest(playerProfil, targetProfil);
                MessageUtils.send(player, "friend_request_accepted", "{player}", target.getName());
                MessageUtils.send(target, "your_request_accepted", "{player}", player.getName());
            } else {
                MessageUtils.send(player, "no_request_from_player");
            }
        }
        return true;
    }

    public static void handleDecline(Player player, String[] args, Profil playerProfil) {
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

    public static void handleRemove(Player player, String[] args, Profil playerProfil) {
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

    public static void handleBlock(Player player, String[] args, Profil playerProfil, Object commandInstance) {
        Player target = Bukkit.getPlayer(args[1]);
        if (target != null) {
            Profil targetProfil = Profil.profils.computeIfAbsent(target, Profil::new);
            if (playerProfil.blocked.contains(target)) {
                MessageUtils.send(player, "already_blocked");
                return;
            }
            SocialActions.blockPlayer(playerProfil, targetProfil);
            MessageUtils.send(player, "player_blocked", "{player}", target.getName());
            MessageUtils.send(target, "you_are_blocked", "{player}", player.getName());
        } else {
            OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(args[1]);
            if (offlineTarget.hasPlayedBefore()) {
                UUID targetUUID = offlineTarget.getUniqueId();
                playerProfil.blocked.add(offlineTarget.getPlayer());
                playerProfil.friends.remove(offlineTarget.getPlayer());
                JavaPlugin plugin = JavaPlugin.getProvidingPlugin(commandInstance.getClass());
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

    public static void handleUnblock(Player player, String[] args, Profil playerProfil) {
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

    public static void handleList(Player player, Profil playerProfil) {
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
}
