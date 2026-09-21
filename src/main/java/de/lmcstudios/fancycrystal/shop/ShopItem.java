package de.lmcstudios.fancycrystal.shop;

import org.bukkit.Material;

import java.util.List;

public class ShopItem {

    private final String id;
    private final int slot;
    private final Material material;
    private final int amount;
    private final double price;
    private final String displayName;
    private final List<String> lore;
    private final List<String> commands;

    public ShopItem(String id, int slot, Material material, int amount, double price,
                    String displayName, List<String> lore, List<String> commands) {
        this.id = id;
        this.slot = slot;
        this.material = material;
        this.amount = amount;
        this.price = price;
        this.displayName = displayName;
        this.lore = lore;
        this.commands = commands;
    }

    public String getId() { return id; }
    public int getSlot() { return slot; }
    public Material getMaterial() { return material; }
    public int getAmount() { return amount; }
    public double getPrice() { return price; }
    public String getDisplayName() { return displayName; }
    public List<String> getLore() { return lore; }
    public List<String> getCommands() { return commands; }
}
