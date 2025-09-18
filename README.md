# SocialManager

SocialManager est un plugin Minecraft Paper permettant de gérer les interactions sociales entre joueurs, comme la gestion des amis, le blocage et les demandes d’amis.

## Fonctionnalités

- Ajouter, accepter, refuser ou supprimer des amis
- Envoyer et recevoir des demandes d’amis
- Bloquer et débloquer des joueurs
- Limite d'amis configurable (`settings.max_friends`)
- Possibilité de bypass la limite d'amis individuellement pour les tests
- Ajout forcé d'amis via `/friends invite <joueur> force` (si le bypass est activé pour l'expéditeur)
- Interface graphique pour voir la liste d'amis, demandes, bloqués, et tous les joueurs du serveur
- Commandes simples et intuitives

## Commandes

- `/friends`  
  Ouvre le menu principal SocialManager avec :
    - Liste d'amis
    - Liste des demandes d'amis
    - Liste des joueurs bloqués
    - Liste de tous les joueurs du serveur (ajout via clic gauche)
- `/friends invite <joueur>`  
  Envoie une demande d’ami (respecte la limite d'amis)
- `/friends invite <joueur> force`  
  Ajoute directement le joueur comme ami, même si la limite est atteinte (nécessite le bypass activé pour l'expéditeur)
- `/friends accept <joueur>`  
  Accepte une demande d’ami (respecte la limite d'amis)
- `/friends decline <joueur>`  
  Refuse une demande d’ami
- `/friends remove <joueur>`  
  Supprime un ami
- `/friends block <joueur>`  
  Bloque un joueur (si hors ligne, le message lui sera envoyé à sa reconnexion)
- `/friends unblock <joueur>`  
  Débloque un joueur
- `/friends list`  
  Affiche la liste de vos amis
- `/friends bypasslimit`  
  Active/désactive le bypass de la limite d'amis pour le joueur (opérateur uniquement)

## Menus graphiques

- **Menu principal** (`/friends`) : navigation vers les listes d'amis, demandes, bloqués, et tous les joueurs.
- **Liste d'amis** : clic gauche pour retirer, clic droit pour bloquer.
- **Demandes d'amis** : clic gauche pour accepter, clic droit pour refuser.
- **Bloqués** : clic gauche pour débloquer.
- **Tous les joueurs** : clic gauche pour envoyer une demande d'ami (respecte la limite ou le bypass).

## Configuration

Exemple de configuration :

```yaml
messages:
  prefix: "§8[§3Social§8] "
  # ... (voir le fichier config.yml pour tous les messages)
settings:
  max_friends: 100
allow_self_friend: true
```

- `settings.max_friends` : nombre maximum d'amis par joueur (hors bypass)
- `allow_self_friend` : permet d'ajouter son propre pseudo en ami (utile pour les tests)

## Permissions

- `/friends bypasslimit` : réservé aux opérateurs (op) pour activer/désactiver le bypass de la limite d'amis sur leur propre compte.

## Dépendances

- Spigot ou Paper 1.21+
- [Adventure API](https://docs.advntr.dev/) (inclus dans Spigot/Paper récents)

## Auteur

- TableIkea\_

## Site web

[www.obsidia.network](http://www.obsidia.network)

---

Plugin développé pour faciliter la gestion des relations sociales sur votre serveur Minecraft !
