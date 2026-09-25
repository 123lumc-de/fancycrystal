package de.lmcstudios.fancycrystal.listeners;

import de.lmcstudios.fancycrystal.FancyCrystalPlugin;
import de.lmcstudios.fancycrystal.shop.ConfirmGUI;
import de.lmcstudios.fancycrystal.shop.ConfirmHolder;
import de.lmcstudios.fancycrystal.shop.ShopGUI;
import de.lmcstudios.fancycrystal.shop.ShopHolder;
import de.lmcstudios.fancycrystal.shop.ShopItem;
import de.lmcstudios.fancycrystal.util.NumberFormatter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ShopListener implements Listener {

    private final FancyCrystalPlugin plugin;
    private static final MiniMessage MM = MiniMessage.miniMessage();

    public ShopListener(FancyCrystalPlugin plugin) {
        this.plugin = plugin;
    }

    // ============================================
    // Klick im Shop-GUI
    // ============================================
    @EventHandler
    public void onShopClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof ShopHolder)) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;

        ShopItem item = plugin.getShopConfig().getItemAtSlot(event.getRawSlot());
        if (item == null) return;

        boolean confirmationEnabled = plugin.getConfig()
            .getBoolean("shop.confirmation.enabled", true);

        if (confirmationEnabled) {
            // Bestätigungs-GUI öffnen
            new ConfirmGUI(plugin).open(player, item);
        } else {
            // Direkt kaufen
            processPurchase(player, item);
        }
    }

    // ============================================
    // Klick im Bestätigungs-GUI
    // ============================================
    @EventHandler
    public void onConfirmClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof ConfirmHolder holder)) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;

        ShopItem item = holder.getItem();
        if (item == null) return;

        int confirmSlot = plugin.getConfig().getInt("shop.confirmation.confirm.slot", 11);
        int cancelSlot = plugin.getConfig().getInt("shop.confirmation.cancel.slot", 15);

        if (event.getRawSlot() == confirmSlot) {
            // Kauf ausführen
            boolean success = processPurchase(player, item);
            if (success) {
                player.closeInventory();
            } else {
                player.closeInventory();
                new ShopGUI(plugin).open(player);
            }
        } else if (event.getRawSlot() == cancelSlot) {
            // Abbrechen → zurück zum Shop
            player.sendMessage(MM.deserialize(
                plugin.getConfig().getString("messages.prefix", "") +
                plugin.getConfig().getString("shop.confirmation.messages.cancelled", "<red>Kauf abgebrochen.")));
            new ShopGUI(plugin).open(player);
        }
    }

    // ============================================
    // Kauf-Logik (gemeinsam genutzt)
    // ============================================
    private boolean processPurchase(Player player, ShopItem item) {
        double balance = plugin.getStorage().getBalance(player.getUniqueId());

        if (balance < item.getPrice()) {
            player.sendMessage(MM.deserialize(
                plugin.getConfig().getString("messages.prefix", "") +
                plugin.getConfig().getString("shop.confirmation.messages.not-enough",
                    "<red>Du hast nicht genug Crystals!")));
            return false;
        }

        // Abzug
        plugin.getStorage().addBalance(player.getUniqueId(), -item.getPrice());
        plugin.getStorage().saveAsync();

        // Erfolgsmeldung
        String symbol = plugin.getConfig().getString("currency.symbol", "✦");
        String priceStr = NumberFormatter.formatNumber(item.getPrice());
        String itemName = item.getDisplayName() != null ? item.getDisplayName() : item.getId();

        String successMsg = plugin.getConfig()
            .getString("shop.confirmation.messages.success",
                "<green>Du hast <color:#E31749>%item%</color> für <color:#E31749>%price% %symbol%</color> gekauft!")
            .replace("%item%", itemName)
            .replace("%price%", priceStr)
            .replace("%symbol%", symbol);

        player.sendMessage(MM.deserialize(
            plugin.getConfig().getString("messages.prefix", "") + successMsg));

        // Belohnung: Commands oder Item
        if (item.getCommands() == null || item.getCommands().isEmpty()) {
            player.getInventory().addItem(new ItemStack(item.getMaterial(), item.getAmount()));
        } else {
            for (String cmd : item.getCommands()) {
                String parsed = cmd.replace("%player%", player.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsed);
            }
        }

        return true;
    }
}
