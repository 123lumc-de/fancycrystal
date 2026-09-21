package de.lmcstudios.fancycrystal.hooks;

import de.lmcstudios.fancycrystal.FancyCrystalPlugin;
import de.lmcstudios.fancycrystal.util.NumberFormatter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlaceholderHook extends PlaceholderExpansion {

    private final FancyCrystalPlugin plugin;

    public PlaceholderHook(FancyCrystalPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "fancycrystal";
    }

    @Override
    public @NotNull String getAuthor() {
        return "LMC Studios";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) return "";

        double balance = plugin.getStorage().getBalance(player.getUniqueId());

        return switch (params.toLowerCase()) {
            case "balance"                -> String.valueOf(balance);
            case "balance_formatted",
                 "formatted"              -> NumberFormatter.formatWithSymbol(balance);
            case "balance_short",
                 "short"                  -> NumberFormatter.shortFormat(balance);
            case "balance_full",
                 "full"                   -> NumberFormatter.fullFormat(balance);
            case "symbol"                 -> plugin.getConfig().getString("currency.symbol", "✦");
            case "currency",
                 "currency_name"          -> plugin.getConfig().getString("currency.name", "Crystal");
            case "currency_plural",
                 "currency_name_plural"   -> plugin.getConfig().getString("currency.name-plural", "Crystals");
            case "unit"                   -> unitFor(balance);
            case "shop_items"             -> String.valueOf(plugin.getShopConfig().getAllItems().size());
            default -> null;
        };
    }

    private String unitFor(double value) {
        double abs = Math.abs(value);
        if (abs >= 1_000_000_000_000L) return "T";
        if (abs >= 1_000_000_000D)     return "B";
        if (abs >= 1_000_000D)         return "M";
        if (abs >= 1_000D)             return "K";
        return "";
    }
}
