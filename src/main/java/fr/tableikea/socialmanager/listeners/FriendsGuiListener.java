package fr.tableikea.socialmanager.listeners;

import fr.tableikea.socialmanager.manager.FriendsManager;
import fr.tableikea.socialmanager.manager.SocialActionsManager;
import fr.tableikea.socialmanager.models.Profil;
import fr.tableikea.socialmanager.utils.ItemBuilder;
import fr.tableikea.socialmanager.utils.MessageFormats;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class FriendsGuiListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null || event.getCurrentItem() == null) return;

        Player player = (Player) event.getWhoClicked();
        ItemStack currentItem = event.getCurrentItem();

        if (!currentItem.hasItemMeta() || !currentItem.getItemMeta().hasDisplayName()) return;
        String itemName = currentItem.getItemMeta().getDisplayName();
        String inventoryTitle = event.getView().getTitle();

        Profil playerProfil = Profil.profils.get(player);
        event.setCancelled(true);

        switch (inventoryTitle) {

            case "§8§lSOCIAL - Menu" -> {
                if (itemName.contains("Liste d'amis")) {
                    Inventory friendsMenu = Bukkit.createInventory(null, 27, "§8§lAMIS");
                    if (playerProfil != null) {
                        for (String friend : playerProfil.friends) {
                            friendsMenu.addItem(
                                    new ItemBuilder(Material.PLAYER_HEAD, 1, "§a" + friend,
                                            List.of("§6CLIC GAUCHE§8: §7Retirer des amis", "§6CLIC DROIT§8: §7Bloquer"))
                                            .getItem());
                        }
                    }
                    player.openInventory(friendsMenu);

                } else if (itemName.contains("Demandes d'amis")) {
                    Inventory requestsMenu = Bukkit.createInventory(null, 27, "§8§lDEMANDES D'AMIS");
                    if (playerProfil != null) {
                        for (String requester : playerProfil.friendRequestsReceived) {
                            requestsMenu.addItem(
                                    new ItemBuilder(Material.BOOK, 1, "§b" + requester,
                                            List.of("§6CLIC GAUCHE§8: §aAccepter", "§6CLIC DROIT§8: §cRefuser"))
                                            .getItem());
                        }
                    }
                    player.openInventory(requestsMenu);

                } else if (itemName.contains("Joueurs bloqués")) {
                    Inventory blockedMenu = Bukkit.createInventory(null, 27, "§8§lBLOQUÉS");
                    if (playerProfil != null) {
                        for (String blocked : playerProfil.blocked) {
                            blockedMenu.addItem(
                                    new ItemBuilder(Material.BARRIER, 1, "§c" + blocked,
                                            List.of("§6CLIC GAUCHE§8: §aDébloquer"))
                                            .getItem());
                        }
                    }
                    player.openInventory(blockedMenu);

                } else if (itemName.contains("Tous les joueurs")) {
                    Inventory allPlayersMenu = Bukkit.createInventory(null, 54, "§8§lTOUS LES JOUEURS");
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (!p.equals(player)) {
                            allPlayersMenu.addItem(
                                    new ItemBuilder(Material.PLAYER_HEAD, 1, "§e" + p.getName(),
                                            List.of("§6CLIC GAUCHE§8: §aAjouter en ami"))
                                            .getItem());
                        }
                    }
                    player.openInventory(allPlayersMenu);
                }
            }

            case "§8§lAMIS" -> {
                Player target = Bukkit.getPlayer(itemName.replace("§a", ""));
                if (target == null) return;

                if (event.getClick() == ClickType.RIGHT) {
                    SocialActionsManager.blockPlayer(player, target);
                    MessageFormats.send(player, "player_blocked", "{player}", target.getName());
                    MessageFormats.send(target, "you_are_blocked", "{player}", player.getName());
                    player.closeInventory();
                } else if (event.getClick() == ClickType.LEFT) {
                    SocialActionsManager.removeFriends(player, target);
                    MessageFormats.send(player, "friend_removed", "{player}", target.getName());
                    MessageFormats.send(target, "removed_by_friend", "{player}", player.getName());
                    player.closeInventory();
                }
            }

            case "§8§lDEMANDES D'AMIS" -> {
                Player requester = Bukkit.getPlayer(itemName.replace("§b", ""));
                if (requester == null) return;

                if (event.getClick() == ClickType.LEFT) {
                    SocialActionsManager.acceptFriendRequest(player, requester);
                    MessageFormats.send(player, "friend_request_accepted", "{player}", requester.getName());
                    MessageFormats.send(requester, "your_request_accepted", "{player}", player.getName());
                    player.closeInventory();
                } else if (event.getClick() == ClickType.RIGHT) {
                    SocialActionsManager.refuseFriendRequest(player, requester);
                    MessageFormats.send(player, "friend_request_declined", "{player}", requester.getName());
                    MessageFormats.send(requester, "your_request_declined", "{player}", player.getName());
                    player.closeInventory();
                }
            }

            case "§8§lBLOQUÉS" -> {
                Player blocked = Bukkit.getPlayer(itemName.replace("§c", ""));
                if (blocked == null) return;

                if (event.getClick() == ClickType.LEFT) {
                    SocialActionsManager.unblockPlayer(player, blocked);
                    MessageFormats.send(player, "player_unblocked", "{player}", blocked.getName());
                    MessageFormats.send(blocked, "you_are_unblocked", "{player}", player.getName());
                    player.closeInventory();
                }
            }

            case "§8§lTOUS LES JOUEURS" -> {
                Player target = Bukkit.getPlayer(itemName.replace("§e", ""));
                if (target == null) return;

                Profil targetProfil = Profil.profils.get(target);
                if (targetProfil == null || playerProfil == null) return;

                if (event.getClick() == ClickType.LEFT) {
                    int maxFriends = JavaPlugin.getProvidingPlugin(getClass()).getConfig().getInt("settings.max_friends", 100);

                    if (!FriendsManager.isBypassFriendLimit(player) && playerProfil.friends.size() >= maxFriends) {
                        MessageFormats.send(player, "max_friends_reached");
                        return;
                    }
                    if (!FriendsManager.isBypassFriendLimit(target) && targetProfil.friends.size() >= maxFriends) {
                        MessageFormats.send(player, "target_max_friends_reached", "{player}", target.getName());
                        return;
                    }

                    if (playerProfil.friends.contains(target)) {
                        MessageFormats.send(player, "already_in_friends");
                        return;
                    }

                    if (targetProfil.friendRequestsReceived.contains(player)) {
                        MessageFormats.send(player, "already_sent_request");
                        return;
                    }

                    SocialActionsManager.sendFriendRequest(player, target);
                    MessageFormats.send(player, "friend_request_sent", "{player}", target.getName());
                    MessageFormats.send(target, "friend_request_received", "{player}", player.getName());
                    player.closeInventory();
                }
            }
        }
    }
}
