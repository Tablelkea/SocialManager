package fr.tableikea.socialmanager.listeners;

import fr.tableikea.socialmanager.models.Profil;
import fr.tableikea.socialmanager.models.SocialActions;
import fr.tableikea.socialmanager.utils.ItemBuilder;
import fr.tableikea.socialmanager.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class FriendsGuiEvent implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        Player player = (Player) event.getWhoClicked();
        if (clickedInventory == null) return;

        String inventoryName = event.getView().getTitle();

        // Menu principal
        if (inventoryName.equals("§8§lSOCIAL - Menu")) {
            event.setCancelled(true);
            ItemStack currentItem = event.getCurrentItem();
            if (currentItem == null || !currentItem.hasItemMeta() || !currentItem.getItemMeta().hasDisplayName()) return;
            String itemName = currentItem.getItemMeta().getDisplayName();

            if (itemName.contains("Liste d'amis")) {
                // Ouvre la liste d'amis
                Inventory friendsMenu = Bukkit.createInventory(null, 27, "§8§lAMIS");
                Profil profil = Profil.profils.get(player);
                if (profil != null) {
                    for (Player friend : profil.friends) {
                        friendsMenu.addItem(new ItemBuilder(Material.PLAYER_HEAD, 1, "§a" + friend.getName(), List.of("§6CLIC GAUCHE§8: §7Retirer des amis", "§6CLIC DROIT§8: §7Bloquer")).getItem());
                    }
                }
                player.openInventory(friendsMenu);
            } else if (itemName.contains("Demandes d'amis")) {
                // Ouvre la liste des demandes d'amis
                Inventory requestsMenu = Bukkit.createInventory(null, 27, "§8§lDEMANDES D'AMIS");
                Profil profil = Profil.profils.get(player);
                if (profil != null) {
                    for (Player requester : profil.friendRequestsReceived) {
                        requestsMenu.addItem(new ItemBuilder(Material.BOOK, 1, "§b" + requester.getName(), List.of("§6CLIC GAUCHE§8: §aAccepter", "§6CLIC DROIT§8: §cRefuser")).getItem());
                    }
                }
                player.openInventory(requestsMenu);
            } else if (itemName.contains("Joueurs bloqués")) {
                // Ouvre la liste des bloqués
                Inventory blockedMenu = Bukkit.createInventory(null, 27, "§8§lBLOQUÉS");
                Profil profil = Profil.profils.get(player);
                if (profil != null) {
                    for (Player blocked : profil.blocked) {
                        blockedMenu.addItem(new ItemBuilder(Material.BARRIER, 1, "§c" + blocked.getName(), List.of("§6CLIC GAUCHE§8: §aDébloquer")).getItem());
                    }
                }
                player.openInventory(blockedMenu);
            } else if (itemName.contains("Tous les joueurs")) {
                // Ouvre la liste de tous les joueurs du serveur
                Inventory allPlayersMenu = Bukkit.createInventory(null, 54, "§8§lTOUS LES JOUEURS");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (!p.equals(player)) {
                        allPlayersMenu.addItem(new ItemBuilder(Material.PLAYER_HEAD, 1, "§e" + p.getName(), List.of("§6CLIC GAUCHE§8: §aAjouter en ami")).getItem());
                    }
                }
                player.openInventory(allPlayersMenu);
            }
            return;
        }

        // Menu amis
        if (inventoryName.equals("§8§lAMIS")) {
            event.setCancelled(true);
            ItemStack currentItem = event.getCurrentItem();
            if (currentItem == null || !currentItem.hasItemMeta() || !currentItem.getItemMeta().hasDisplayName()) return;
            String itemName = currentItem.getItemMeta().getDisplayName();
            Player target = Bukkit.getPlayer(itemName.replace("§a", ""));
            if (target == null) return;
            Profil playerProfil = Profil.profils.get(player);
            Profil targetProfil = Profil.profils.get(target);

            if (event.getClick() == ClickType.RIGHT) {
                SocialActions.blockPlayer(playerProfil, targetProfil);
                MessageUtils.send(player, "player_blocked", "{player}", target.getName());
                MessageUtils.send(target, "you_are_blocked", "{player}", player.getName());
                player.closeInventory();
            } else if (event.getClick() == ClickType.LEFT) {
                SocialActions.removeFriend(playerProfil, targetProfil);
                MessageUtils.send(player, "friend_removed", "{player}", target.getName());
                MessageUtils.send(target, "removed_by_friend", "{player}", player.getName());
                player.closeInventory();
            }
            return;
        }

        // Menu demandes d'amis
        if (inventoryName.equals("§8§lDEMANDES D'AMIS")) {
            event.setCancelled(true);
            ItemStack currentItem = event.getCurrentItem();
            if (currentItem == null || !currentItem.hasItemMeta() || !currentItem.getItemMeta().hasDisplayName()) return;
            String itemName = currentItem.getItemMeta().getDisplayName();
            Player requester = Bukkit.getPlayer(itemName.replace("§b", ""));
            if (requester == null) return;
            Profil playerProfil = Profil.profils.get(player);
            Profil requesterProfil = Profil.profils.get(requester);

            if (event.getClick() == ClickType.LEFT) {
                SocialActions.acceptFriendRequest(playerProfil, requesterProfil);
                MessageUtils.send(player, "friend_request_accepted", "{player}", requester.getName());
                MessageUtils.send(requester, "your_request_accepted", "{player}", player.getName());
                player.closeInventory();
            } else if (event.getClick() == ClickType.RIGHT) {
                SocialActions.declineFriendRequest(playerProfil, requesterProfil);
                MessageUtils.send(player, "friend_request_declined", "{player}", requester.getName());
                MessageUtils.send(requester, "your_request_declined", "{player}", player.getName());
                player.closeInventory();
            }
            return;
        }

        // Menu bloqués
        if (inventoryName.equals("§8§lBLOQUÉS")) {
            event.setCancelled(true);
            ItemStack currentItem = event.getCurrentItem();
            if (currentItem == null || !currentItem.hasItemMeta() || !currentItem.getItemMeta().hasDisplayName()) return;
            String itemName = currentItem.getItemMeta().getDisplayName();
            Player blocked = Bukkit.getPlayer(itemName.replace("§c", ""));
            if (blocked == null) return;
            Profil playerProfil = Profil.profils.get(player);
            Profil blockedProfil = Profil.profils.get(blocked);

            if (event.getClick() == ClickType.LEFT) {
                SocialActions.unblockPlayer(playerProfil, blockedProfil);
                MessageUtils.send(player, "player_unblocked", "{player}", blocked.getName());
                MessageUtils.send(blocked, "you_are_unblocked", "{player}", player.getName());
                player.closeInventory();
            }
            return;
        }

        // Menu tous les joueurs
        if (inventoryName.equals("§8§lTOUS LES JOUEURS")) {
            event.setCancelled(true);
            ItemStack currentItem = event.getCurrentItem();
            if (currentItem == null || !currentItem.hasItemMeta() || !currentItem.getItemMeta().hasDisplayName()) return;
            String itemName = currentItem.getItemMeta().getDisplayName();
            Player target = Bukkit.getPlayer(itemName.replace("§e", ""));
            if (target == null) return;
            Profil playerProfil = Profil.profils.get(player);
            Profil targetProfil = Profil.profils.get(target);

            if (event.getClick() == ClickType.LEFT) {
                if (playerProfil.friends.contains(target) || targetProfil.friendRequestsReceived.contains(player)) {
                    MessageUtils.send(player, "already_sent_request");
                    return;
                }
                targetProfil.friendRequestsReceived.add(player);
                playerProfil.friendRequestsSended.add(target);
                MessageUtils.send(player, "friend_request_sent", "{player}", target.getName());
                MessageUtils.send(target, "friend_request_received", "{player}", player.getName());
                player.closeInventory();
            }
        }
    }
}
