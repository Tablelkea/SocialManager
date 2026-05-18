# 🤝 SocialManager

SocialManager est un plugin Minecraft permettant de gérer un système social complet avec amis, demandes d’amis, blocages et interface graphique intuitive.

---

## ✨ Fonctionnalités

### 👥 Gestion des amis
- Ajouter un ami
- Supprimer un ami
- Accepter / Refuser une demande d’ami
- Limite configurable du nombre d’amis

---

### 🚫 Système de blocage
- Bloquer un joueur
- Débloquer un joueur
- Empêche les interactions sociales avec les joueurs bloqués

---

### 🖥️ Interface graphique (GUI)
Menu social complet avec :
- 📋 Liste d’amis
- 📩 Demandes d’amis
- 🚫 Joueurs bloqués
- 🌍 Liste des joueurs connectés

Navigation simple avec clic gauche / clic droit.

---

### ⚙️ Configuration
Personnalisation via `config.yml` :
- nombre maximum d’amis
- messages configurables
- permissions bypass

---

## 📦 Installation

1. Télécharger le `.jar`
2. Placer le fichier dans le dossier :

```bash
/plugins/
```
3. Redémarrer le serveur

---

## 🛠️ Commandes

```bash
/friends
/friends invite <joueur>
/friends accept <joueur>
/friends decline <joueur>
/friends remove <joueur>
/friends block <joueur>
/friends unblock <joueur>
```

---

## 🔐 Permissions

```yaml
socialmanager.use
socialmanager.bypass.maxfriends
socialmanager.admin
```

---
## 📁 Configuration exemple

```yaml
settings:
  max_friends: 100
```

---

## 📌 Compatibilité

- Paper
- Purpur

Versions supportées :

1.21+

---

## 👤 Auteur
Développé par Tablelkea

GitHub : https://github.com/Tablelkea