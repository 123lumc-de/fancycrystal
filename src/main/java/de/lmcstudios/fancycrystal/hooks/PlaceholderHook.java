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
        return true; // Reload-sicher
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) return "";

        double balance = plugin.getStorage().getBalance(player.getUniqueId());

        return switch (params.toLowerCase()) {
            // Rohwerte ohne Formatierung
            case "balance"             -> String.valueOf(balance);

            // Formatiert mit Symbol (z.B. "1.5K ✦")
            case "balance_formatted",
                 "formatted"           -> NumberFormatter.formatWithSymbol(balance);

            // Nur die Zahl, ohne Symbol (z.B. "1.5K")
            case "balance_short",
                 "short"               -> NumberFormatter.shortFormat(balance);

            // Volle Zahl mit Trennzeichen (z.B. "1.500,00")
            case "balance_full",
                 "full"                -> NumberFormatter.fullFormat(balance);

            // Nur das Symbol (z.B. "✦")
            case "symbol"              -> plugin.getConfig().getString("currency.symbol", "✦");

            // Name der Währung im Singular/Plural
            case "currency",
                 "currency_name"       -> plugin.getConfig().getString("currency.name", "Crystal");
            case "currency_plural",
                 "currency_name_plural"-> plugin.getConfig().getString("currency.name-plural", "Crystals");

            // Kürzel ohne Zahl, z.B. "K"/"M"/"B"/"T" – nützlich für eigene Layouts
            case "unit"                -> unitFor(balance);

            // Platzhalter für die aktuellen Shop-Anzahl
            case "shop_items"          -> String.valueOf(plugin.getShopConfig().getAllItems().size());

            default -> null; // unbekannter Platzhalter
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
