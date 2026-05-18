package fr.tableikea.socialmanager.models;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Profil {
    private final Player player;

    public List<String> friends;
    public List<String> blocked;
    public List<String> friendRequestsSended;
    public List<String> friendRequestsReceived;

    public static Map<Player, Profil> profils = new HashMap<>();

    public Profil(Player player) {
        this.player = player;
        this.friends = new ArrayList<>();
        this.blocked = new ArrayList<>();
        this.friendRequestsSended = new ArrayList<>();
        this.friendRequestsReceived = new ArrayList<>();
        profils.put(player, this);
    }

    public Player getPlayer() {
        return player;
    }

    public List<String> getFriends() {
        return friends;
    }

    public List<String> getBlocked() {
        return blocked;
    }

    public List<String> getFriendRequestsSended() {
        return friendRequestsSended;
    }

    public List<String> getFriendRequestsReceived() {
        return friendRequestsReceived;
    }

    public boolean isFriend(String name) {
        return friends.contains(name);
    }

    public boolean isBlocked(String name) {
        return blocked.contains(name);
    }
}
