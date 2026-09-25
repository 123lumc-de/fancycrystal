package de.lmcstudios.fancycrystal.shop;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ConfirmHolder implements InventoryHolder {

    private final ShopItem item;

    public ConfirmHolder(ShopItem item) {
        this.item = item;
    }

    public ShopItem getItem() {
        return item;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
