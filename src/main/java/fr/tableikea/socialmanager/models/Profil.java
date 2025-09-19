package fr.tableikea.socialmanager.models;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Profil {
    private final Player player;

    // Utilisation de String pour la persistance (nom du joueur)
    public List<String> friends;
    public List<String> blocked;
    public List<String> friendRequestsSended;
    public List<String> friendRequestsReceived;

    // Map de tous les profils (associée à chaque joueur en ligne)
    public static Map<Player, Profil> profils = new HashMap<>();

    public Profil(Player player) {
        this.player = player;
        this.friends = new ArrayList<>();
        this.blocked = new ArrayList<>();
        this.friendRequestsSended = new ArrayList<>();
        this.friendRequestsReceived = new ArrayList<>();
        profils.put(player, this); // Ajout automatique dans la map
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

    // Vérifie si un joueur est ami
    public boolean isFriend(String name) {
        return friends.contains(name);
    }

    // Vérifie si un joueur est bloqué
    public boolean isBlocked(String name) {
        return blocked.contains(name);
    }
}
