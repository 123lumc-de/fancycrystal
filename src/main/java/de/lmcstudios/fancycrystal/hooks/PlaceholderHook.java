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

            // Rohwerte
            case "balance"                  -> String.valueOf(balance);

            // Zahlenformate
            case "balance_short",
                 "short"                    -> NumberFormatter.shortFormat(balance);
            case "balance_full",
                 "full"                     -> NumberFormatter.fullFormat(balance);
            case "balance_formatted",
                 "formatted"                -> NumberFormatter.formatWithSymbol(balance);

            // MiniMessage-formatierte Varianten
            case "balance_custom",
                 "custom"                   -> formatCustom(player, balance, "placeholders.balance-format");
            case "balance_custom2"          -> formatCustom(player, balance, "placeholders.balance-format-2");

            // Info-Placeholder
            case "symbol"                   -> plugin.getConfig().getString("currency.symbol", "✦");
            case "currency",
                 "currency_name"            -> plugin.getConfig().getString("currency.name", "Crystal");
            case "currency_plural",
                 "currency_name_plural"     -> plugin.getConfig().getString("currency.name-plural", "Crystals");
            case "unit"                     -> unitFor(balance);
            case "shop_items"               -> String.valueOf(plugin.getShopConfig().getAllItems().size());

            default -> null;
        };
    }

    private String formatCustom(OfflinePlayer player, double balance, String configPath) {
        String format = plugin.getConfig().getString(configPath, "");
        if (format == null || format.isEmpty()) {
            return NumberFormatter.formatWithSymbol(balance);
        }
        return applyFormat(format, player, balance);
    }

    private String applyFormat(String format, OfflinePlayer player, double balance) {
        String symbol   = plugin.getConfig().getString("currency.symbol", "✦");
        String currency = plugin.getConfig().getString("currency.name", "Crystal");
        String plural   = plugin.getConfig().getString("currency.name-plural", "Crystals");

        return format
            .replace("%amount%",   NumberFormatter.formatNumber(balance))
            .replace("%raw%",      String.valueOf(balance))
            .replace("%symbol%",   symbol)
            .replace("%currency%", currency)
            .replace("%plural%",   plural)
            .replace("%unit%",     unitFor(balance))
            .replace("%player%",   player.getName() != null ? player.getName() : "");
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
