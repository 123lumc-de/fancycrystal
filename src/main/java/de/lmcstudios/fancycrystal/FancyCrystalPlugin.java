package de.lmcstudios.fancycrystal;

import de.lmcstudios.fancycrystal.commands.*;
import de.lmcstudios.fancycrystal.economy.CrystalStorage;
import de.lmcstudios.fancycrystal.listeners.ShopListener;
import de.lmcstudios.fancycrystal.shop.ShopConfig;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class FancyCrystalPlugin extends JavaPlugin {

    private static final int BSTATS_PLUGIN_ID = 34195;

    private static FancyCrystalPlugin instance;
    private CrystalStorage storage;
    private ShopConfig shopConfig;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        reloadConfig();

        this.storage = new CrystalStorage(this);
        this.storage.load();

        this.shopConfig = new ShopConfig(this);

        // Commands
        getCommand("crystals").setExecutor(new CrystalsCommand(this));
        getCommand("crystalshop").setExecutor(new CrystalShopCommand(this));
        getCommand("crystalspay").setExecutor(new CrystalsPayCommand(this));
        getCommand("setcrystals").setExecutor(new SetCrystalsCommand(this));
        getCommand("addcrystals").setExecutor(new AddCrystalsCommand(this));
        getCommand("removecrystals").setExecutor(new RemoveCrystalsCommand(this));
        getCommand("fancycrystal").setExecutor(new FancyCrystalCommand(this));

        // Listener
        Bukkit.getPluginManager().registerEvents(new ShopListener(this), this);

        // bStats
        setupBStats();

        getLogger().info("FancyCrystal v" + getPluginMeta().getVersion() + " von LMC Studios aktiviert!");
    }

    private void setupBStats() {
        Metrics metrics = new Metrics(this, BSTATS_PLUGIN_ID);

        metrics.addCustomChart(new SimplePie("shop_item_count", () -> {
            int size = shopConfig.getAllItems().size();
            if (size == 0) return "0";
            if (size <= 5) return "1-5";
            if (size <= 10) return "6-10";
            if (size <= 25) return "11-25";
            return "25+";
        }));

        metrics.addCustomChart(new SimplePie("storage_type", () -> "yaml"));

        metrics.addCustomChart(new SimplePie("short_format", () ->
            getConfig().getBoolean("currency.short-format", true) ? "ja" : "nein"));

        metrics.addCustomChart(new SingleLineChart("total_crystals_in_circulation",
            () -> (int) storage.getTotalCirculation()));

        metrics.addCustomChart(new SingleLineChart("total_accounts",
            () -> storage.getAccountCount()));
    }

    @Override
    public void onDisable() {
        if (storage != null) storage.save();
        getLogger().info("FancyCrystal deaktiviert.");
    }

    public static FancyCrystalPlugin getInstance() { return instance; }
    public CrystalStorage getStorage() { return storage; }
    public ShopConfig getShopConfig() { return shopConfig; }
}
