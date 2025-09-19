package fr.tableikea.socialmanager.listeners;

import fr.tableikea.socialmanager.manager.SocialActions;
import fr.tableikea.socialmanager.models.Profil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Profil playerProfil = new Profil(player);

        // Chargement des données depuis la config
        Object blockedObj = SocialActions.getDataFromConfig(player, "blocked");
        if (blockedObj instanceof List) {
            playerProfil.blocked.addAll((List<String>) blockedObj);
        }

        Object friendsObj = SocialActions.getDataFromConfig(player, "friends");
        if (friendsObj instanceof List) {
            playerProfil.friends.addAll((List<String>) friendsObj);
        }

        Object requestsSentObj = SocialActions.getDataFromConfig(player, "friendRequestsSended");
        if (requestsSentObj instanceof List) {
            playerProfil.friendRequestsSended.addAll((List<String>) requestsSentObj);
        }

        Object requestsReceivedObj = SocialActions.getDataFromConfig(player, "friendRequestsReceived");
        if (requestsReceivedObj instanceof List) {
            playerProfil.friendRequestsReceived.addAll((List<String>) requestsReceivedObj);
        }

        // Enregistrer le profil dans la map globale
        Profil.profils.put(player, playerProfil);

        // Message de join personnalisé
        event.joinMessage(Component.text("§8[§2+§8] §7" + player.getName()));
    }
}
