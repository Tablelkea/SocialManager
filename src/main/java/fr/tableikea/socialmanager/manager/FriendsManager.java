package fr.tableikea.socialmanager.manager;

import fr.tableikea.socialmanager.Main;
import fr.tableikea.socialmanager.models.Profil;
import fr.tableikea.socialmanager.utils.ItemBuilder;
import fr.tableikea.socialmanager.utils.MessageFormats;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jspecify.annotations.NonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class FriendsManager {

    private final Set<UUID> bypassFriendLimitPlayers = new HashSet<>();

    public void setBypassFriendLimit(Player player, boolean bypass) {
        if (bypass) {
            bypassFriendLimitPlayers.add(player.getUniqueId());
        } else {
            bypassFriendLimitPlayers.remove(player.getUniqueId());
        }
    }

    public boolean isBypassFriendLimit(@NonNull Player player) {
        return bypassFriendLimitPlayers.contains(player.getUniqueId());
    }

    public void handleInvite(Player player, String @NonNull [] args, Profil playerProfil, boolean allowSelfFriend) {

        SocialActionsManager socialActionsManager = Main.getInstance().getSocialActionsManager();

        if(checkArgs(args, 2, player)) return;

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            MessageFormats.send(player, "not_in_friends");
            return;
        }

        String targetName = target.getName();
        String playerName = player.getName();

        if (!allowSelfFriend && target == player) {
            MessageFormats.send(player, "cannot_add_self");
            return;
        }

        int maxFriends = Main.getInstance().getConfig().getInt("settings.max_friends", 100);
        boolean force = args.length > 2 && args[2].equalsIgnoreCase("force");

        Profil targetProfil = Profil.profils.computeIfAbsent(target, Profil::new);

        boolean bypassSender = isBypassFriendLimit(player);
        boolean bypassTarget = isBypassFriendLimit(target);

        if (force && bypassSender && !bypassTarget) {
            socialActionsManager.addFriends(player, target);
            MessageFormats.send(player, "friend_force_added", "{player}", targetName);
            MessageFormats.send(target, "friend_force_added_by", "{player}", playerName);
            return;
        }

        if (!bypassSender && playerProfil.friends.size() >= maxFriends) {
            MessageFormats.send(player, "max_friends_reached");
            return;
        }

        if (!bypassTarget && targetProfil.friends.size() >= maxFriends) {
            MessageFormats.send(player, "target_max_friends_reached", "{player}", targetName);
            return;
        }

        if (targetProfil.friendRequestsReceived.contains(playerName)) {
            MessageFormats.send(player, "already_sent_request");
            return;
        }

        if (targetProfil.friends.contains(playerName)) {
            MessageFormats.send(player, "already_in_friends");
            return;
        }

        socialActionsManager.sendFriendRequest(player, target);
        MessageFormats.send(player, "friend_request_sent", "{player}", targetName);
        MessageFormats.send(target, "friend_request_received", "{player}", playerName);
    }

    public void handleAccept(Player player, String @NonNull [] args, Profil playerProfil) {

        SocialActionsManager socialActionsManager = Main.getInstance().getSocialActionsManager();

        if(checkArgs(args, 2, player)) return;

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            MessageFormats.send(player, "no_request_from_player");
            return;
        }

        String targetName = target.getName();
        String playerName = player.getName();

        if (!Profil.profils.containsKey(target)) {
            MessageFormats.send(player, "no_request_from_player");
            return;
        }

        Profil targetProfil = Profil.profils.get(target);
        int maxFriends = Main.getInstance().getConfig().getInt("settings.max_friends", 100);

        if (!isBypassFriendLimit(player) && playerProfil.friends.size() >= maxFriends) {
            MessageFormats.send(player, "max_friends_reached");
            return;
        }

        if (!isBypassFriendLimit(target) && targetProfil.friends.size() >= maxFriends) {
            MessageFormats.send(player, "target_max_friends_reached", "{player}", targetName);
            return;
        }

        if (playerProfil.friendRequestsReceived.contains(targetName)) {
            socialActionsManager.acceptFriendRequest(player, target);
            MessageFormats.send(player, "friend_request_accepted", "{player}", targetName);
            MessageFormats.send(target, "your_request_accepted", "{player}", playerName);
        } else {
            MessageFormats.send(player, "no_request_from_player");
        }
    }

    public void handleDecline(Player player, String @NonNull [] args, Profil playerProfil) {

        SocialActionsManager socialActionsManager = Main.getInstance().getSocialActionsManager();

        if(checkArgs(args, 2, player)) return;

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null || !Profil.profils.containsKey(target)) {
            MessageFormats.send(player, "no_request_from_player");
            return;
        }

        String targetName = target.getName();

        if (playerProfil.friendRequestsReceived.contains(targetName)) {
            socialActionsManager.refuseFriendRequest(player, target);
            MessageFormats.send(player, "friend_request_declined", "{player}", targetName);
            MessageFormats.send(target, "your_request_declined", "{player}", player.getName());
        } else {
            MessageFormats.send(player, "no_request_from_player");
        }
    }

    public void handleRemove(Player player, String @NonNull [] args, Profil playerProfil) {

        SocialActionsManager socialActionsManager = Main.getInstance().getSocialActionsManager();

        if(checkArgs(args, 2, player)) return;

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null || !Profil.profils.containsKey(target)) {
            MessageFormats.send(player, "not_in_friends");
            return;
        }

        String targetName = target.getName();

        if (playerProfil.friends.contains(targetName)) {
            socialActionsManager.removeFriends(player, target);
            MessageFormats.send(player, "friend_removed", "{player}", targetName);
            MessageFormats.send(target, "removed_by_friend", "{player}", player.getName());
        } else {
            MessageFormats.send(player, "not_in_friends");
        }
    }

    public void handleBlock(Player player, String @NonNull [] args, Profil playerProfil) {

        SocialActionsManager socialActionsManager = Main.getInstance().getSocialActionsManager();

        if(checkArgs(args, 2, player)) return;

        Player target = Bukkit.getPlayer(args[1]);

        if (target != null) {
            String targetName = target.getName();
            if (playerProfil.blocked.contains(targetName)) {
                MessageFormats.send(player, "already_blocked");
                return;
            }

            socialActionsManager.blockPlayer(player, target);
            MessageFormats.send(player, "player_blocked", "{player}", targetName);
            MessageFormats.send(target, "you_are_blocked", "{player}", player.getName());
        } else {
            OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(args[1]);
            if (offlineTarget.hasPlayedBefore()) {
                String offlineName = offlineTarget.getName();
                if (playerProfil.blocked.contains(offlineName)) {
                    MessageFormats.send(player, "already_blocked");
                    return;
                }

                socialActionsManager.blockPlayer(player, offlineTarget.getPlayer());
                UUID targetUUID = offlineTarget.getUniqueId();
                List<String> pending = Main.getInstance().getConfig().getStringList("pending_block_messages." + targetUUID);
                pending.add(player.getUniqueId().toString());
                Main.getInstance().getConfig().set("pending_block_messages." + targetUUID, pending);
                Main.getInstance().saveConfig();

                MessageFormats.send(player, "player_blocked", "{player}", offlineName);
            } else {
                MessageFormats.send(player, "not_in_friends");
            }
        }
    }

    public void handleUnblock(Player player, String @NonNull [] args, Profil playerProfil) {
        if(checkArgs(args, 2, player)) return;

        SocialActionsManager socialActionsManager = Main.getInstance().getSocialActionsManager();

        Player target = Bukkit.getPlayer(args[1]);
        if (target != null) {
            String targetName = target.getName();
            if (playerProfil.blocked.contains(targetName)) {
                socialActionsManager.unblockPlayer(player, target);
                MessageFormats.send(player, "player_unblocked", "{player}", targetName);
                MessageFormats.send(target, "you_are_unblocked", "{player}", player.getName());
            } else {
                MessageFormats.send(player, "not_in_blocked");
            }
        }
    }

    public void handleList(Player player, @NonNull Profil playerProfil) {
        MessageFormats.send(player, "friends_list_header");
        if (playerProfil.friends.isEmpty()) {
            MessageFormats.send(player, "friends_list_empty");
        } else {
            for (String friendName : playerProfil.friends) {
                MessageFormats.send(player, "friends_list_entry", "{player}", friendName);
            }
        }
        MessageFormats.send(player, "friends_list_footer");
    }

    public void openFriendsGUI(@NonNull Player player){
        Inventory mainMenu = Bukkit.createInventory(null, 27, Component.text("§8§lSOCIAL - Menu"));
        mainMenu.setItem(10, new ItemBuilder(
                Material.PLAYER_HEAD,
                1,
                "§aListe d'amis",
                List.of("§7Voir vos amis."))
                .getItem()
        );

        mainMenu.setItem(12, new ItemBuilder(
                Material.BOOK,
                1,
                "§bDemandes d'amis",
                List.of("§7Voir vos demandes d'amis reçues."))
                .getItem()
        );

        mainMenu.setItem(14, new ItemBuilder(
                Material.BARRIER,
                1,
                "§cJoueurs bloqués",
                List.of("§7Voir les joueurs que vous avez bloqués."))
                .getItem()
        );

        mainMenu.setItem(16, new ItemBuilder(
                Material.PAPER,
                1,
                "§eTous les joueurs",
                List.of("§7Ajouter un joueur en ami via clic gauche."))
                .getItem()
        );

        player.openInventory(mainMenu);
    }

    public boolean startsWith(@NonNull String name, String partial) {
        return name.toLowerCase().startsWith(partial);
    }

    public boolean isFriend(String name, @NonNull Profil profil) {
        return !profil.getFriends().contains(name);
    }

    public boolean isBlocked(String name, @NonNull Profil profil) {
        return !profil.getBlocked().contains(name);
    }

    public boolean hasPendingInvite(String name, @NonNull Profil profil) {
        return profil.getFriendRequestsSended().contains(name);
    }

    public boolean checkArgs(String @NonNull [] args, int size, Player player){
        if (args.length < size) {
            MessageFormats.send(player, "invalid_argument");
            return true;
        }
        return false;
    }
}
