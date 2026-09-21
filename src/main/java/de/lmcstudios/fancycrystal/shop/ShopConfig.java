package de.lmcstudios.fancycrystal.shop;

import de.lmcstudios.fancycrystal.FancyCrystalPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopConfig {

    private final FancyCrystalPlugin plugin;
    private final Map<Integer, ShopItem> itemsBySlot = new HashMap<>();

    public ShopConfig(FancyCrystalPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        itemsBySlot.clear();
        ConfigurationSection itemsSection = plugin.getConfig().getConfigurationSection("shop.items");
        if (itemsSection == null) return;

        for (String key : itemsSection.getKeys(false)) {
            ConfigurationSection s = itemsSection.getConfigurationSection(key);
            if (s == null) continue;

            int slot = s.getInt("slot", -1);
            Material mat = Material.matchMaterial(s.getString("material", "STONE"));
            if (slot < 0 || mat == null) {
                plugin.getLogger().warning("Ungültiges Shop-Item: " + key);
                continue;
            }

            ShopItem item = new ShopItem(
                key,
                slot,
                mat,
                s.getInt("amount", 1),
                s.getDouble("price", 0.0),
                s.getString("display-name", null),
                s.getStringList("lore"),
                s.getStringList("commands")
            );
            itemsBySlot.put(slot, item);
        }
    }

    public ShopItem getItemAtSlot(int slot) { return itemsBySlot.get(slot); }
    public List<ShopItem> getAllItems() { return new ArrayList<>(itemsBySlot.values()); }
    public int getRows() { return plugin.getConfig().getInt("shop.rows", 6); }
}
