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

    /**
     * Lädt (oder lädt neu) alle Shop-Items aus der config.yml.
     * Wird bei Plugin-Start und bei /fc reload aufgerufen.
     */
    public void load() {
        itemsBySlot.clear();

        ConfigurationSection itemsSection = plugin.getConfig().getConfigurationSection("shop.items");
        if (itemsSection == null) {
            plugin.getLogger().warning("Kein 'shop.items'-Block in der config.yml gefunden.");
            return;
        }

        for (String key : itemsSection.getKeys(false)) {
            ConfigurationSection s = itemsSection.getConfigurationSection(key);
            if (s == null) continue;

            int slot = s.getInt("slot", -1);
            if (slot < 0 || slot > 53) {
                plugin.getLogger().warning("Ungültiger Slot für Shop-Item '" + key + "': " + slot);
                continue;
            }

            if (itemsBySlot.containsKey(slot)) {
                plugin.getLogger().warning("Slot " + slot + " wird von mehreren Items benutzt! Überspringe '" + key + "'.");
                continue;
            }

            String materialName = s.getString("material", "STONE");
            Material mat = Material.matchMaterial(materialName);
            if (mat == null) {
                plugin.getLogger().warning("Unbekanntes Material für Shop-Item '" + key + "': " + materialName);
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

        plugin.getLogger().info("Shop geladen: " + itemsBySlot.size() + " Items.");
    }

    public ShopItem getItemAtSlot(int slot) {
        return itemsBySlot.get(slot);
    }

    public List<ShopItem> getAllItems() {
        return new ArrayList<>(itemsBySlot.values());
    }

    public int getRows() {
        return plugin.getConfig().getInt("shop.rows", 6);
    }
}
