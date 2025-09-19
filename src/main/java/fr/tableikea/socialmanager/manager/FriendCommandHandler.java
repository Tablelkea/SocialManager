package fr.tableikea.socialmanager.manager;

import fr.tableikea.socialmanager.Main;
import fr.tableikea.socialmanager.models.Profil;
import fr.tableikea.socialmanager.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

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

    public static void handleInvite(Player player, String[] args, Profil playerProfil, boolean allowSelfFriend) {
        if (args.length < 2) {
            MessageUtils.send(player, "invalid_argument");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            MessageUtils.send(player, "not_in_friends");
            return;
        }

        String targetName = target.getName();
        String playerName = player.getName();

        if (!allowSelfFriend && target == player) {
            MessageUtils.send(player, "cannot_add_self");
            return;
        }

        int maxFriends = Main.getInstance().getConfig().getInt("settings.max_friends", 100);
        boolean force = args.length > 2 && args[2].equalsIgnoreCase("force");

        Profil targetProfil = Profil.profils.computeIfAbsent(target, Profil::new);

        boolean bypassSender = isBypassFriendLimit(player);
        boolean bypassTarget = isBypassFriendLimit(target);

        if (force && bypassSender && !bypassTarget) {
            SocialActions.addFriends(player, target);
            MessageUtils.send(player, "friend_force_added", "{player}", targetName);
            MessageUtils.send(target, "friend_force_added_by", "{player}", playerName);
            return;
        }

        if (!bypassSender && playerProfil.friends.size() >= maxFriends) {
            MessageUtils.send(player, "max_friends_reached");
            return;
        }

        if (!bypassTarget && targetProfil.friends.size() >= maxFriends) {
            MessageUtils.send(player, "target_max_friends_reached", "{player}", targetName);
            return;
        }

        if (targetProfil.friendRequestsReceived.contains(playerName)) {
            MessageUtils.send(player, "already_sent_request");
            return;
        }

        if (targetProfil.friends.contains(playerName)) {
            MessageUtils.send(player, "already_in_friends");
            return;
        }

        SocialActions.sendFriendRequest(player, target);
        MessageUtils.send(player, "friend_request_sent", "{player}", targetName);
        MessageUtils.send(target, "friend_request_received", "{player}", playerName);
    }

    public static void handleAccept(Player player, String[] args, Profil playerProfil) {
        if (args.length < 2) {
            MessageUtils.send(player, "invalid_argument");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            MessageUtils.send(player, "no_request_from_player");
            return;
        }

        String targetName = target.getName();
        String playerName = player.getName();

        if (!Profil.profils.containsKey(target)) {
            MessageUtils.send(player, "no_request_from_player");
            return;
        }

        Profil targetProfil = Profil.profils.get(target);
        int maxFriends = Main.getInstance().getConfig().getInt("settings.max_friends", 100);

        if (!isBypassFriendLimit(player) && playerProfil.friends.size() >= maxFriends) {
            MessageUtils.send(player, "max_friends_reached");
            return;
        }

        if (!isBypassFriendLimit(target) && targetProfil.friends.size() >= maxFriends) {
            MessageUtils.send(player, "target_max_friends_reached", "{player}", targetName);
            return;
        }

        if (playerProfil.friendRequestsReceived.contains(targetName)) {
            SocialActions.acceptFriendRequest(player, target);
            MessageUtils.send(player, "friend_request_accepted", "{player}", targetName);
            MessageUtils.send(target, "your_request_accepted", "{player}", playerName);
        } else {
            MessageUtils.send(player, "no_request_from_player");
        }
    }

    public static void handleDecline(Player player, String[] args, Profil playerProfil) {
        if (args.length < 2) {
            MessageUtils.send(player, "invalid_argument");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null || !Profil.profils.containsKey(target)) {
            MessageUtils.send(player, "no_request_from_player");
            return;
        }

        String targetName = target.getName();

        if (playerProfil.friendRequestsReceived.contains(targetName)) {
            SocialActions.refuseFriendRequest(player, target);
            MessageUtils.send(player, "friend_request_declined", "{player}", targetName);
            MessageUtils.send(target, "your_request_declined", "{player}", player.getName());
        } else {
            MessageUtils.send(player, "no_request_from_player");
        }
    }

    public static void handleRemove(Player player, String[] args, Profil playerProfil) {
        if (args.length < 2) {
            MessageUtils.send(player, "invalid_argument");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null || !Profil.profils.containsKey(target)) {
            MessageUtils.send(player, "not_in_friends");
            return;
        }

        String targetName = target.getName();

        if (playerProfil.friends.contains(targetName)) {
            SocialActions.removeFriends(player, target);
            MessageUtils.send(player, "friend_removed", "{player}", targetName);
            MessageUtils.send(target, "removed_by_friend", "{player}", player.getName());
        } else {
            MessageUtils.send(player, "not_in_friends");
        }
    }

    public static void handleBlock(Player player, String[] args, Profil playerProfil) {
        if (args.length < 2) {
            MessageUtils.send(player, "invalid_argument");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target != null) {
            String targetName = target.getName();
            if (playerProfil.blocked.contains(targetName)) {
                MessageUtils.send(player, "already_blocked");
                return;
            }

            SocialActions.blockPlayer(player, target);
            MessageUtils.send(player, "player_blocked", "{player}", targetName);
            MessageUtils.send(target, "you_are_blocked", "{player}", player.getName());
        } else {
            OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(args[1]);
            if (offlineTarget.hasPlayedBefore()) {
                String offlineName = offlineTarget.getName();
                if (playerProfil.blocked.contains(offlineName)) {
                    MessageUtils.send(player, "already_blocked");
                    return;
                }

                SocialActions.blockPlayer(player, offlineTarget.getPlayer());
                UUID targetUUID = offlineTarget.getUniqueId();
                List<String> pending = Main.getInstance().getConfig().getStringList("pending_block_messages." + targetUUID);
                pending.add(player.getUniqueId().toString());
                Main.getInstance().getConfig().set("pending_block_messages." + targetUUID, pending);
                Main.getInstance().saveConfig();

                MessageUtils.send(player, "player_blocked", "{player}", offlineName);
            } else {
                MessageUtils.send(player, "not_in_friends");
            }
        }
    }

    public static void handleUnblock(Player player, String[] args, Profil playerProfil) {
        if (args.length < 2) {
            MessageUtils.send(player, "invalid_argument");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target != null) {
            String targetName = target.getName();
            if (playerProfil.blocked.contains(targetName)) {
                SocialActions.unblockPlayer(player, target);
                MessageUtils.send(player, "player_unblocked", "{player}", targetName);
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
            for (String friendName : playerProfil.friends) {
                MessageUtils.send(player, "friends_list_entry", "{player}", friendName);
            }
        }
        MessageUtils.send(player, "friends_list_footer");
    }
}
