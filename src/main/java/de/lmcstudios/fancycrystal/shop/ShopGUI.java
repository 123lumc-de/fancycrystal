package de.lmcstudios.fancycrystal.shop;

import de.lmcstudios.fancycrystal.FancyCrystalPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopGUI {

    private final FancyCrystalPlugin plugin;
    private static final MiniMessage MM = MiniMessage.miniMessage();

    public ShopGUI(FancyCrystalPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        String titleRaw = plugin.getConfig().getString("shop.title", "<aqua>Crystal Shop");
        Component title = MM.deserialize(titleRaw);
        int rows = plugin.getShopConfig().getRows();
        Inventory inv = Bukkit.createInventory(new ShopHolder(), rows * 9, title);

        Material fillerMat = Material.matchMaterial(
            plugin.getConfig().getString("shop.filler.material", "GRAY_STAINED_GLASS_PANE"));
        if (fillerMat == null) fillerMat = Material.GRAY_STAINED_GLASS_PANE;

        ItemStack filler = new ItemStack(fillerMat);
        ItemMeta fmeta = filler.getItemMeta();
        fmeta.displayName(MM.deserialize(plugin.getConfig().getString("shop.filler.name", " "))
            .decoration(TextDecoration.ITALIC, false));
        filler.setItemMeta(fmeta);

        for (int i = 0; i < inv.getSize(); i++) {
            if (plugin.getShopConfig().getItemAtSlot(i) == null) {
                inv.setItem(i, filler);
            }
        }

        double balance = plugin.getEconomy().getBalance(player);

        for (ShopItem item : plugin.getShopConfig().getAllItems()) {
            inv.setItem(item.getSlot(), buildItem(item, balance));
        }

        player.openInventory(inv);
    }

    public ItemStack buildItem(ShopItem item, double balance) {
        ItemStack stack = new ItemStack(item.getMaterial(), item.getAmount());
        ItemMeta meta = stack.getItemMeta();

        boolean canAfford = balance >= item.getPrice();

        String name = item.getDisplayName() != null
            ? item.getDisplayName()
            : plugin.getConfig().getString("shop.item-format.name", "<aqua>%name%");

        meta.displayName(MM.deserialize(name)
            .decoration(TextDecoration.ITALIC, false));

        List<String> loreTemplate = canAfford
            ? plugin.getConfig().getStringList("shop.item-format.lore")
            : plugin.getConfig().getStringList("shop.item-format.lore-cant-afford");

        List<Component> lore = new ArrayList<>();
        String symbol = plugin.getConfig().getString("currency.symbol", "✦");
        String price = String.format("%.2f", item.getPrice());

        for (String line : loreTemplate) {
            line = line.replace("%price%", price)
                       .replace("%symbol%", symbol)
                       .replace("%name%", name);
            lore.add(MM.deserialize(line).decoration(TextDecoration.ITALIC, false));
        }
        if (item.getLore() != null) {
            for (String line : item.getLore()) {
                lore.add(MM.deserialize(line).decoration(TextDecoration.ITALIC, false));
            }
        }

        meta.lore(lore);
        stack.setItemMeta(meta);
        return stack;
    }
}
