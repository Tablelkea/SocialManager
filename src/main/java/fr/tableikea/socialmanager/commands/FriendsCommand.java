package fr.tableikea.socialmanager.commands;

import fr.tableikea.socialmanager.Main;
import fr.tableikea.socialmanager.manager.FriendsManager;
import fr.tableikea.socialmanager.models.Profil;
import fr.tableikea.socialmanager.utils.MessageFormats;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FriendsCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        FriendsManager friendsManager = Main.getInstance().getFriendsManager();

        if (!(sender instanceof Player player)) {
            MessageFormats.send(sender, "only_players");
            return true;
        }

        boolean allowSelfFriend = Main.getInstance().getConfig().getBoolean("allow_self_friend", false);
        Profil playerProfil = Profil.profils.computeIfAbsent(player, Profil::new);

        if (args.length == 0) {
            friendsManager.openFriendsGUI(player);
            return true;
        }

        String argument = args[0].toLowerCase();

        if (argument.equals("bypasslimit")) {
            if (!player.isOp()) {
                MessageFormats.send(player, "no_permission");
                return true;
            }
            boolean newState = !friendsManager.isBypassFriendLimit(player);

            friendsManager.setBypassFriendLimit(player, newState);
            MessageFormats.send(player, newState ? "bypass_enabled" : "bypass_disabled");
            return true;
        }

        switch (argument) {
            case "invite" ->
                    friendsManager.handleInvite(player, args, playerProfil, allowSelfFriend);

            case "accept" ->
                    friendsManager.handleAccept(player, args, playerProfil);

            case "decline" ->
                    friendsManager.handleDecline(player, args, Profil.profils.get(player));

            case "remove" ->
                    friendsManager.handleRemove(player, args, Profil.profils.get(player));

            case "block" -> friendsManager.handleBlock(player, args, playerProfil);

            case "unblock" ->
                    friendsManager.handleUnblock(player, args, playerProfil);

            case "list" ->
                    friendsManager.handleList(player, playerProfil);

            default ->
                    MessageFormats.send(player, "invalid_argument");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) return List.of();

        FriendsManager friendsManager = Main.getInstance().getFriendsManager();

        Profil playerProfil = Profil.profils.get(player);
        if (playerProfil == null) return List.of();

        if (args.length == 1) {
            return List.of(
                    "invite",
                    "accept",
                    "decline",
                    "remove",
                    "block",
                    "unblock",
                    "list",
                    "bypasslimit"
            );
        }

        if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            String partial = args[1].toLowerCase();

            return switch (subCommand) {
                case "invite" -> Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> !name.equalsIgnoreCase(player.getName()))
                        .filter(name -> friendsManager.isFriend(name, playerProfil))
                        .filter(name -> friendsManager.isBlocked(name, playerProfil))
                        .filter(name -> !friendsManager.hasPendingInvite(name, playerProfil))
                        .filter(name -> friendsManager.startsWith(name, partial))
                        .toList();

                case "accept", "decline" -> playerProfil.getFriendRequestsReceived().stream()
                        .filter(name -> friendsManager.startsWith(name, partial))
                        .toList();

                case "remove" -> playerProfil.getFriends().stream()
                        .filter(name -> friendsManager.startsWith(name, partial))
                        .toList();

                case "block" -> Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> !name.equalsIgnoreCase(player.getName()))
                        .filter(name -> friendsManager.isFriend(name, playerProfil))
                        .filter(name -> friendsManager.isBlocked(name, playerProfil))
                        .filter(name -> friendsManager.startsWith(name, partial))
                        .toList();

                case "unblock" -> playerProfil.getBlocked().stream()
                        .filter(name -> friendsManager.startsWith(name, partial))
                        .toList();

                default -> List.of();
            };
        }

        return List.of();
    }
}
