package de.lmcstudios.fancycrystal.shop;

import de.lmcstudios.fancycrystal.FancyCrystalPlugin;
import de.lmcstudios.fancycrystal.util.NumberFormatter;
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

public class ConfirmGUI {

    private final FancyCrystalPlugin plugin;
    private static final MiniMessage MM = MiniMessage.miniMessage();

    public ConfirmGUI(FancyCrystalPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player player, ShopItem item) {
        String titleRaw = plugin.getConfig().getString("shop.confirmation.title",
            "<gradient:#E31749:#FF5C7F>Kauf bestätigen</gradient>");
        Component title = MM.deserialize(titleRaw);

        Inventory inv = Bukkit.createInventory(new ConfirmHolder(item), 27, title);

        String symbol = plugin.getConfig().getString("currency.symbol", "✦");
        String priceStr = NumberFormatter.formatNumber(item.getPrice());

        // Filler
        Material fillerMat = Material.matchMaterial(plugin.getConfig()
            .getString("shop.confirmation.filler.material", "BLACK_STAINED_GLASS_PANE"));
        if (fillerMat == null) fillerMat = Material.BLACK_STAINED_GLASS_PANE;

        ItemStack filler = new ItemStack(fillerMat);
        ItemMeta fmeta = filler.getItemMeta();
        fmeta.displayName(MM.deserialize(plugin.getConfig()
            .getString("shop.confirmation.filler.name", " "))
            .decoration(TextDecoration.ITALIC, false));
        filler.setItemMeta(fmeta);

        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, filler);
        }

        // Vorschau-Item (Mitte)
        ItemStack preview = new ItemStack(item.getMaterial(), item.getAmount());
        ItemMeta pmeta = preview.getItemMeta();

        String previewName = plugin.getConfig()
            .getString("shop.confirmation.preview.name", "<color:#E31749>%name%</color>")
            .replace("%name%", item.getDisplayName() != null ? item.getDisplayName() : item.getId());
        pmeta.displayName(MM.deserialize(previewName).decoration(TextDecoration.ITALIC, false));

        List<Component> previewLore = new ArrayList<>();
        for (String line : plugin.getConfig().getStringList("shop.confirmation.preview.lore")) {
            line = line
                .replace("%amount%", String.valueOf(item.getAmount()))
                .replace("%price%", priceStr)
                .replace("%symbol%", symbol)
                .replace("%name%", item.getId());
            previewLore.add(MM.deserialize(line).decoration(TextDecoration.ITALIC, false));
        }
        pmeta.lore(previewLore);
        preview.setItemMeta(pmeta);
        inv.setItem(13, preview);

        // Bestätigen-Button
        int confirmSlot = plugin.getConfig().getInt("shop.confirmation.confirm.slot", 11);
        inv.setItem(confirmSlot, buildButton(
            "shop.confirmation.confirm",
            "LIME_CONCRETE",
            "<green>✔ Kauf bestätigen",
            List.of("<gray>Klicke, um den Kauf abzuschließen.")
        ));

        // Abbrechen-Button
        int cancelSlot = plugin.getConfig().getInt("shop.confirmation.cancel.slot", 15);
        inv.setItem(cancelSlot, buildButton(
            "shop.confirmation.cancel",
            "RED_CONCRETE",
            "<red>✘ Abbrechen",
            List.of("<gray>Klicke, um den Kauf abzubrechen.")
        ));

        player.openInventory(inv);
    }

    private ItemStack buildButton(String path, String defaultMat, String defaultName, List<String> defaultLore) {
        String matName = plugin.getConfig().getString(path + ".material", defaultMat);
        Material mat = Material.matchMaterial(matName);
        if (mat == null) mat = Material.matchMaterial(defaultMat);
        if (mat == null) mat = Material.STONE;

        ItemStack stack = new ItemStack(mat);
        ItemMeta meta = stack.getItemMeta();

        String name = plugin.getConfig().getString(path + ".name", defaultName);
        meta.displayName(MM.deserialize(name).decoration(TextDecoration.ITALIC, false));

        List<String> loreRaw = plugin.getConfig().getStringList(path + ".lore");
        if (loreRaw.isEmpty()) loreRaw = defaultLore;

        List<Component> lore = new ArrayList<>();
        for (String line : loreRaw) {
            lore.add(MM.deserialize(line).decoration(TextDecoration.ITALIC, false));
        }
        meta.lore(lore);
        stack.setItemMeta(meta);
        return stack;
    }
}
