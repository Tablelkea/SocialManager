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
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
        Profil playerProfil = Profil.profils.computeIfAbsent(player, Profil::new);

        if (args.length == 0) {
            Inventory mainMenu = Bukkit.createInventory(null, 27, Component.text("§8§lSOCIAL - Menu"));
            mainMenu.setItem(10, new ItemBuilder(Material.PLAYER_HEAD, 1, "§aListe d'amis", List.of("§7Voir vos amis.")).getItem());
            mainMenu.setItem(12, new ItemBuilder(Material.BOOK, 1, "§bDemandes d'amis", List.of("§7Voir vos demandes d'amis reçues.")).getItem());
            mainMenu.setItem(14, new ItemBuilder(Material.BARRIER, 1, "§cJoueurs bloqués", List.of("§7Voir les joueurs que vous avez bloqués.")).getItem());
            mainMenu.setItem(16, new ItemBuilder(Material.PAPER, 1, "§eTous les joueurs", List.of("§7Ajouter un joueur en ami via clic gauche.")).getItem());
            player.openInventory(mainMenu);
            return true;
        }

        String argument = args[0].toLowerCase();

        if (argument.equals("bypasslimit")) {
            if (!player.isOp()) {
                MessageUtils.send(player, "no_permission");
                return true;
            }
            boolean newState = !FriendCommandHandler.isBypassFriendLimit(player);
            FriendCommandHandler.setBypassFriendLimit(player, newState);
            MessageUtils.send(player, newState ? "bypass_enabled" : "bypass_disabled");
            return true;
        }

        switch (argument) {
            case "invite" -> FriendCommandHandler.handleInvite(player, args, playerProfil, allowSelfFriend);
            case "accept" -> FriendCommandHandler.handleAccept(player, args, playerProfil);
            case "decline" -> FriendCommandHandler.handleDecline(player, args, Profil.profils.get(player));
            case "remove" -> FriendCommandHandler.handleRemove(player, args, Profil.profils.get(player));
            case "block" -> FriendCommandHandler.handleBlock(player, args, playerProfil);
            case "unblock" -> FriendCommandHandler.handleUnblock(player, args, playerProfil);
            case "list" -> FriendCommandHandler.handleList(player, playerProfil);
            default -> MessageUtils.send(player, "invalid_argument");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) return List.of();

        Profil playerProfil = Profil.profils.get(player);
        if (playerProfil == null) return List.of();

        if (args.length == 1) {
            return List.of("invite", "accept", "decline", "remove", "block", "unblock", "list", "bypasslimit");
        }

        if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            String partial = args[1].toLowerCase();

            return switch (subCommand) {
                case "invite" -> Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> !name.equalsIgnoreCase(player.getName()))
                        .filter(name ->
                                !playerProfil.getFriends().contains(name) &&
                                        !playerProfil.getBlocked().contains(name) &&
                                        !playerProfil.getFriendRequestsSended().contains(name))
                        .filter(name -> name.toLowerCase().startsWith(partial))
                        .toList();

                case "accept", "decline" -> playerProfil.getFriendRequestsReceived().stream()
                        .filter(name -> name.toLowerCase().startsWith(partial))
                        .toList();

                case "remove" -> playerProfil.getFriends().stream()
                        .filter(name -> name.toLowerCase().startsWith(partial))
                        .toList();

                case "block" -> Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> !name.equalsIgnoreCase(player.getName()))
                        .filter(name ->
                                !playerProfil.getFriends().contains(name) &&
                                        !playerProfil.getBlocked().contains(name))
                        .filter(name -> name.toLowerCase().startsWith(partial))
                        .toList();

                case "unblock" -> playerProfil.getBlocked().stream()
                        .filter(name -> name.toLowerCase().startsWith(partial))
                        .toList();

                default -> List.of();
            };
        }

        return List.of();
    }
}
