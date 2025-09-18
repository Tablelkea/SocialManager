package fr.tableikea.socialmanager.commands;

import fr.tableikea.socialmanager.manager.FriendCommandHandler;
import fr.tableikea.socialmanager.models.Profil;
import fr.tableikea.socialmanager.utils.ItemBuilder;
import fr.tableikea.socialmanager.utils.MessageUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class FriendsCommand implements TabExecutor, Listener {

    public FriendsCommand() {
        Bukkit.getPluginManager().registerEvents(this, JavaPlugin.getProvidingPlugin(getClass()));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            MessageUtils.send(sender, "only_players");
            return true;
        }

        boolean allowSelfFriend = JavaPlugin.getProvidingPlugin(getClass()).getConfig().getBoolean("allow_self_friend", false);

        if (args.length == 0) {
            Inventory mainMenu = Bukkit.createInventory(null, 27, Component.text("§8§lSOCIAL - Menu"));
            mainMenu.setItem(10, new ItemBuilder(Material.PLAYER_HEAD, 1, "§aListe d'amis", List.of("§7Voir vos amis.")).getItem());
            mainMenu.setItem(12, new ItemBuilder(Material.BOOK, 1, "§bDemandes d'amis", List.of("§7Voir vos demandes d'amis reçues.")).getItem());
            mainMenu.setItem(14, new ItemBuilder(Material.BARRIER, 1, "§cJoueurs bloqués", List.of("§7Voir les joueurs que vous avez bloqués.")).getItem());
            mainMenu.setItem(16, new ItemBuilder(Material.PAPER, 1, "§eTous les joueurs", List.of("§7Ajouter un joueur en ami via clic gauche.")).getItem());
            player.openInventory(mainMenu);
            return true;
        }

        String argument = args[0];
        Profil playerProfil = Profil.profils.computeIfAbsent(player, Profil::new);

        if (argument.equalsIgnoreCase("bypasslimit")) {
            if (!player.isOp()) {
                MessageUtils.send(player, "no_permission");
                return true;
            }
            boolean newState = !FriendCommandHandler.isBypassFriendLimit(player);
            FriendCommandHandler.setBypassFriendLimit(player, newState);
            MessageUtils.send(player, newState ? "bypass_enabled" : "bypass_disabled");
            return true;
        }

        switch (argument.toLowerCase()) {
            case "invite" -> FriendCommandHandler.handleInvite(player, args, playerProfil, allowSelfFriend);
            case "accept" -> FriendCommandHandler.handleAccept(player, args, playerProfil);
            case "decline" -> FriendCommandHandler.handleDecline(player, args, playerProfil);
            case "remove" -> FriendCommandHandler.handleRemove(player, args, playerProfil);
            case "block" -> FriendCommandHandler.handleBlock(player, args, playerProfil, this);
            case "unblock" -> FriendCommandHandler.handleUnblock(player, args, playerProfil);
            case "list" -> FriendCommandHandler.handleList(player, playerProfil);
            default -> MessageUtils.send(player, "invalid_argument");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if(args.length == 1){
            return List.of("invite", "accept", "decline", "remove", "block", "unblock", "list");
        }else if(args.length == 2) {

            if(args[0].equalsIgnoreCase("invite")){
                Player pl = (Player) sender;

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
        if (!senders.isEmpty()) {
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
