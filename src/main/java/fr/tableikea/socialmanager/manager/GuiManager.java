package fr.tableikea.socialmanager.manager;

import fr.tableikea.socialmanager.Main;
import fr.tableikea.socialmanager.models.Profil;
import fr.tableikea.socialmanager.utils.ItemBuilder;
import fr.tableikea.socialmanager.utils.MessageFormats;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class GuiManager {

    public void handleMainMenu(Player player, Profil profil, @NonNull String itemName) {
        if (itemName.contains("Liste d'amis")) {
            openFriendsMenu(player, profil);
        } else if (itemName.contains("Demandes d'amis")) {
            openRequestsMenu(player, profil);
        } else if (itemName.contains("Joueurs bloqués")) {
            openBlockedMenu(player, profil);
        } else if (itemName.contains("Tous les joueurs")) {
            openAllPlayersMenu(player);
        }
    }

    public void openFriendsMenu(Player player, @NonNull Profil profil) {
        Inventory inv = Bukkit.createInventory(null, 27, "§8§lAMIS");

        for (String friend : profil.friends) {
            inv.addItem(new ItemBuilder(
                    Material.PLAYER_HEAD,
                    1,
                    "§a" + friend,
                    List.of("§6CLIC GAUCHE§8: §7Retirer", "§6CLIC DROIT§8: §7Bloquer")
            ).getItem());
        }

        player.openInventory(inv);
    }

    public void openRequestsMenu(Player player, @NonNull Profil profil) {
        Inventory inv = Bukkit.createInventory(null, 27, "§8§lDEMANDES D'AMIS");

        for (String requester : profil.friendRequestsReceived) {
            inv.addItem(new ItemBuilder(
                    Material.BOOK,
                    1,
                    "§b" + requester,
                    List.of("§6CLIC GAUCHE§8: §aAccepter", "§6CLIC DROIT§8: §cRefuser")
            ).getItem());
        }

        player.openInventory(inv);
    }

    public void openBlockedMenu(Player player, @NonNull Profil profil) {
        Inventory inv = Bukkit.createInventory(null, 27, "§8§lBLOQUÉS");

        for (String blocked : profil.blocked) {
            inv.addItem(new ItemBuilder(
                    Material.BARRIER,
                    1,
                    "§c" + blocked,
                    List.of("§6CLIC GAUCHE§8: §aDébloquer")
            ).getItem());
        }

        player.openInventory(inv);
    }

    public void openAllPlayersMenu(@NonNull Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§8§lTOUS LES JOUEURS");

        Bukkit.getOnlinePlayers().stream()
                .filter(p -> !p.equals(player))
                .forEach(p -> inv.addItem(
                        new ItemBuilder(
                                Material.PLAYER_HEAD,
                                1,
                                "§e" + p.getName(),
                                List.of("§6CLIC GAUCHE§8: §aAjouter en ami")
                        ).getItem()
                ));

        player.openInventory(inv);
    }

    public void handleFriendsMenu(Player player, String itemName, ClickType click) {

        SocialActionsManager socialManager = Main.getInstance().getSocialActionsManager();

        Player target = Bukkit.getPlayer(stripColor(itemName));
        if (target == null) return;

        if (click == ClickType.LEFT) {
            socialManager.removeFriends(player, target);
            send(player, "friend_removed", target);
            send(target, "removed_by_friend", player);
        }

        if (click == ClickType.RIGHT) {
            socialManager.blockPlayer(player, target);
            send(player, "player_blocked", target);
            send(target, "you_are_blocked", player);
        }

        player.closeInventory();
    }

    public void handleRequestsMenu(Player player, String itemName, ClickType click) {
        SocialActionsManager socialManager = Main.getInstance().getSocialActionsManager();

        Player requester = Bukkit.getPlayer(stripColor(itemName));
        if (requester == null) return;

        if (click == ClickType.LEFT) {
            socialManager.acceptFriendRequest(player, requester);
            send(player, "friend_request_accepted", requester);
            send(requester, "your_request_accepted", player);
        }

        if (click == ClickType.RIGHT) {
            socialManager.refuseFriendRequest(player, requester);
            send(player, "friend_request_declined", requester);
            send(requester, "your_request_declined", player);
        }

        player.closeInventory();
    }

    public void handleBlockedMenu(Player player, String itemName, ClickType click) {
        SocialActionsManager socialManager = Main.getInstance().getSocialActionsManager();

        if (click != ClickType.LEFT) return;

        Player blocked = Bukkit.getPlayer(stripColor(itemName));
        if (blocked == null) return;

        socialManager.unblockPlayer(player, blocked);
        send(player, "player_unblocked", blocked);
        send(blocked, "you_are_unblocked", player);

        player.closeInventory();
    }

    public void handleAllPlayersMenu(Player player, Profil profil, String itemName, ClickType click) {
        FriendsManager friendsManager = Main.getInstance().getFriendsManager();
        SocialActionsManager socialManager = Main.getInstance().getSocialActionsManager();

        if (click != ClickType.LEFT) return;

        Player target = Bukkit.getPlayer(stripColor(itemName));
        if (target == null) return;

        Profil targetProfil = Profil.profils.get(target);
        if (targetProfil == null) return;

        int maxFriends = Main.getInstance().getConfig().getInt("settings.max_friends", 100);

        if (!friendsManager.isBypassFriendLimit(player) && profil.friends.size() >= maxFriends) {
            MessageFormats.send(player, "max_friends_reached");
            return;
        }

        if (!friendsManager.isBypassFriendLimit(target) && targetProfil.friends.size() >= maxFriends) {
            MessageFormats.send(player, "target_max_friends_reached", "{player}", target.getName());
            return;
        }

        socialManager.sendFriendRequest(player, target);

        send(player, "friend_request_sent", target);
        send(target, "friend_request_received", player);

        player.closeInventory();
    }

    public void send(Player player, String key, @NonNull Player target) {
        MessageFormats.send(player, key, "{player}", target.getName());
    }

    @Contract(pure = true)
    public @NonNull String stripColor(@NonNull String text) {
        return text.replaceAll("§.", "");
    }

}
