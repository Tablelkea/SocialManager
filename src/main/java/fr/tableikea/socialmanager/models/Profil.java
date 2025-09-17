package fr.tableikea.socialmanager.models;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Profil {
    public final Player player;
    public ArrayList<Player> friends;
    public ArrayList<Player> blocked;
    public ArrayList<Player> friendRequestsSended;
    public ArrayList<Player> friendRequestsReceived;

    public static Map<Player, Profil> profils = new HashMap<>() {
    };

    public Profil(Player player) {
        this.player = player;
        this.friends = new ArrayList<>();
        this.blocked = new ArrayList<>();
        this.friendRequestsSended = new ArrayList<>();
        this.friendRequestsReceived = new ArrayList<>();
    }

    public Player getPlayer() {
        return player;
    }

    public ArrayList<Player> getFriends() {
        return friends;
    }

    public ArrayList<Player> getBlocked() {
        return blocked;
    }

    public void addFriend(Player friend) {
        if (!friends.contains(friend) && !blocked.contains(friend)) {
            friends.add(friend);
        }
    }

    public void removeFriend(Player friend) {
        friends.remove(friend);
    }

    public void blockPlayer(Player player) {
        if (!blocked.contains(player)) {
            blocked.add(player);
            friends.remove(player); // Remove from friends if blocking
        }
    }

    public void unblockPlayer(Player player) {
        blocked.remove(player);
    }

    public ArrayList<Player> getFriendRequestsSended() {
        return friendRequestsSended;
    }
    public ArrayList<Player> getFriendRequestsReceived() {
        return friendRequestsReceived;
    }
    public void sendFriendRequest(Player to) {
        if (!friendRequestsSended.contains(to) && !friends.contains(to) && !blocked.contains(to)) {
            friendRequestsSended.add(to);
        }
    }
    public void receiveFriendRequest(Player from) {
        if (!friendRequestsReceived.contains(from) && !friends.contains(from) && !blocked.contains(from)) {
            friendRequestsReceived.add(from);
        }
    }
    public void acceptFriendRequest(Player from) {
        if (friendRequestsReceived.contains(from)) {
            friendRequestsReceived.remove(from);
            addFriend(from);
        }
    }
}
