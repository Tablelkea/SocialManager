package fr.tableikea.socialmanager.manager;

import fr.tableikea.socialmanager.Main;
import fr.tableikea.socialmanager.models.Profil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SocialActions {

    public static void saveDataToConfig(Player player, String path, Object object) {
        Main.getInstance().getConfig().set(player.getName() + "." + path, object);
        Main.getInstance().saveConfig();
    }

    public static Object getDataFromConfig(Player player, String path) {
        return Main.getInstance().getConfig().get(player.getName() + "." + path);
    }

    public static void addFriends(Player player1, Player player2) {
        Profil pl1 = Profil.profils.get(player1);
        Profil pl2 = Profil.profils.get(player2);

        String name1 = player1.getName();
        String name2 = player2.getName();

        pl1.friends.add(name2);
        pl2.friends.add(name1);

        pl1.friendRequestsReceived.remove(name2);
        pl2.friendRequestsSended.remove(name1);

        saveDataToConfig(player1, "friends", pl1.friends);
        saveDataToConfig(player2, "friends", pl2.friends);
    }

    public static void removeFriends(Player player1, Player player2) {
        Profil pl1 = Profil.profils.get(player1);
        Profil pl2 = Profil.profils.get(player2);

        String name1 = player1.getName();
        String name2 = player2.getName();

        pl1.friends.remove(name2);
        pl2.friends.remove(name1);

        saveDataToConfig(player1, "friends", pl1.friends);
        saveDataToConfig(player2, "friends", pl2.friends);
    }

    public static void sendFriendRequest(Player from, Player to) {
        Profil pFrom = Profil.profils.get(from);
        Profil pTo = Profil.profils.get(to);

        String fromName = from.getName();
        String toName = to.getName();

        pFrom.friendRequestsSended.add(toName);
        pTo.friendRequestsReceived.add(fromName);

        saveDataToConfig(from, "friendRequestsSended", pFrom.friendRequestsSended);
        saveDataToConfig(to, "friendRequestsReceived", pTo.friendRequestsReceived);
    }

    public static void acceptFriendRequest(Player from, Player to) {
        Profil pFrom = Profil.profils.get(from);
        Profil pTo = Profil.profils.get(to);

        String fromName = from.getName();
        String toName = to.getName();

        pFrom.friendRequestsSended.remove(toName);
        pTo.friendRequestsReceived.remove(fromName);

        saveDataToConfig(from, "friendRequestsSended", pFrom.friendRequestsSended);
        saveDataToConfig(to, "friendRequestsReceived", pTo.friendRequestsReceived);

        addFriends(from, to);
    }

    public static void refuseFriendRequest(Player from, Player to) {
        Profil pFrom = Profil.profils.get(from);
        Profil pTo = Profil.profils.get(to);

        String fromName = from.getName();
        String toName = to.getName();

        pFrom.friendRequestsSended.remove(toName);
        pTo.friendRequestsReceived.remove(fromName);

        saveDataToConfig(from, "friendRequestsSended", pFrom.friendRequestsSended);
        saveDataToConfig(to, "friendRequestsReceived", pTo.friendRequestsReceived);
    }

    public static void blockPlayer(Player from, Player to) {
        Profil pFrom = Profil.profils.get(from);
        String toName = to.getName();
        String fromName = from.getName();

        pFrom.blocked.add(toName);
        saveDataToConfig(from, "blocked", pFrom.blocked);

        // Optionnel : bloquer aussi dans l'autre sens ?
        if (to.isOnline()) {
            Profil pTo = Profil.profils.get(to);
            pTo.blocked.add(fromName);
            saveDataToConfig(to, "blocked", pTo.blocked);
        }

        // Supprimer de la liste d'amis s'ils le sont
        removeFriends(from, to);
    }

    public static void unblockPlayer(Player from, Player to) {
        Profil pFrom = Profil.profils.get(from);
        String toName = to.getName();
        String fromName = from.getName();

        pFrom.blocked.remove(toName);
        saveDataToConfig(from, "blocked", pFrom.blocked);

        if (to.isOnline()) {
            Profil pTo = Profil.profils.get(to);
            pTo.blocked.remove(fromName);
            saveDataToConfig(to, "blocked", pTo.blocked);
        }
    }
}
