package fr.tableikea.socialmanager.manager;

import fr.tableikea.socialmanager.models.Profil;

public class SocialActions {

    public static void addFriend(Profil p1, Profil p2) {
        p1.addFriend(p2.getPlayer());
        p2.addFriend(p1.getPlayer());
    }

    public static void removeFriend(Profil p1, Profil p2) {
        p1.removeFriend(p2.getPlayer());
        p2.removeFriend(p1.getPlayer());
    }

    public static void blockPlayer(Profil blocker, Profil blocked) {
        blocker.blockPlayer(blocked.getPlayer());
        blocked.removeFriend(blocker.getPlayer());
        blocker.removeFriend(blocked.getPlayer());
    }

    public static void unblockPlayer(Profil unblocker, Profil unblocked) {
        unblocker.unblockPlayer(unblocked.getPlayer());
    }

    public static void acceptFriendRequest(Profil receiver, Profil sender) {
        receiver.acceptFriendRequest(sender.getPlayer());
        sender.friendRequestsSended.remove(receiver.getPlayer());
        addFriend(receiver, sender);
    }

    public static void declineFriendRequest(Profil receiver, Profil sender) {
        receiver.friendRequestsReceived.remove(sender.getPlayer());
        sender.friendRequestsSended.remove(receiver.getPlayer());
    }
}
