package fr.tableikea.socialmanager.listeners;

import fr.tableikea.socialmanager.Main;
import fr.tableikea.socialmanager.manager.FriendsManager;
import fr.tableikea.socialmanager.manager.GuiManager;
import fr.tableikea.socialmanager.manager.SocialActionsManager;
import fr.tableikea.socialmanager.models.Profil;
import fr.tableikea.socialmanager.utils.ItemBuilder;
import fr.tableikea.socialmanager.utils.MessageFormats;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class FriendsGuiListener implements Listener {

    @EventHandler
    public void onInventoryClick(@NonNull InventoryClickEvent event) {

        GuiManager guiManager = Main.getInstance().getGuiManager();

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;
        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) return;

        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
        String title = event.getView().getTitle();
        Profil profil = Profil.profils.get(player);

        event.setCancelled(true);

        switch (title) {
            case "§8§lSOCIAL - Menu" -> guiManager.handleMainMenu(player, profil, itemName);
            case "§8§lAMIS" -> guiManager.handleFriendsMenu(player, itemName, event.getClick());
            case "§8§lDEMANDES D'AMIS" -> guiManager.handleRequestsMenu(player, itemName, event.getClick());
            case "§8§lBLOQUÉS" -> guiManager.handleBlockedMenu(player, itemName, event.getClick());
            case "§8§lTOUS LES JOUEURS" -> guiManager.handleAllPlayersMenu(player, profil, itemName, event.getClick());
        }
    }
}