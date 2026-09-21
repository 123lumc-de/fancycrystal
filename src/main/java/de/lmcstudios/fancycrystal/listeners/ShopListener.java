package de.lmcstudios.fancycrystal.listeners;

import de.lmcstudios.fancycrystal.FancyCrystalPlugin;
import de.lmcstudios.fancycrystal.shop.ShopGUI;
import de.lmcstudios.fancycrystal.shop.ShopHolder;
import de.lmcstudios.fancycrystal.shop.ShopItem;
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

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof ShopHolder)) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;

        ShopItem item = plugin.getShopConfig().getItemAtSlot(event.getRawSlot());
        if (item == null) return;

        double balance = plugin.getEconomy().getBalance(player);
        if (balance < item.getPrice()) {
            player.sendMessage(MM.deserialize(
                plugin.getConfig().getString("messages.prefix", "") +
                plugin.getConfig().getString("messages.pay-insufficient", "<red>Nicht genug Crystals.")));
            return;
        }

        plugin.getEconomy().withdrawPlayer(player, item.getPrice());
        player.sendMessage(MM.deserialize(
            plugin.getConfig().getString("messages.prefix", "") +
            "<green>Du hast <aqua>" + item.getId() + "</aqua> für <aqua>" +
            String.format("%.2f", item.getPrice()) + "</aqua> gekauft!"));

        if (item.getCommands() == null || item.getCommands().isEmpty()) {
            player.getInventory().addItem(new ItemStack(item.getMaterial(), item.getAmount()));
        } else {
            for (String cmd : item.getCommands()) {
                String parsed = cmd.replace("%player%", player.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsed);
            }
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> new ShopGUI(plugin).open(player), 1L);
    }
}
