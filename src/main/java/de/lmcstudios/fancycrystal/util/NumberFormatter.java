package de.lmcstudios.fancycrystal.util;

import de.lmcstudios.fancycrystal.FancyCrystalPlugin;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class NumberFormatter {

    private NumberFormatter() {}

    /**
     * Formatiert eine Zahl kompakt mit K (Tausend), M (Million), B (Billion), T (Trillion).
     * Beispiel: 1500 -> "1,5K", 2500000 -> "2,5M", 1200000000 -> "1,2B"
     */
    public static String shortFormat(double value) {
        boolean negative = value < 0;
        double abs = Math.abs(value);

        String suffix;
        double divisor;

        if (abs >= 1_000_000_000_000L) {       // Trillion
            suffix = "T";
            divisor = 1_000_000_000_000D;
        } else if (abs >= 1_000_000_000D) {    // Billion
            suffix = "B";
            divisor = 1_000_000_000D;
        } else if (abs >= 1_000_000D) {        // Million
            suffix = "M";
            divisor = 1_000_000D;
        } else if (abs >= 1_000D) {            // Thousand
            suffix = "K";
            divisor = 1_000D;
        } else {
            DecimalFormat df = buildFormat(decimals());
            return df.format(value);
        }

        double result = abs / divisor;
        DecimalFormat df = buildFormat(1);
        String formatted = df.format(result);
        // ",0" am Ende wegschneiden (z.B. 2,0K -> 2K)
        if (formatted.endsWith(",0")) {
            formatted = formatted.substring(0, formatted.length() - 2);
        }
        return (negative ? "-" : "") + formatted + suffix;
    }

    /**
     * Formatiert eine Zahl mit Tausendertrennzeichen und der in der Config
     * definierten Nachkommastellen-Anzahl.
     * Beispiel: 1234567.89 -> "1.234.567,89" (bei Locale DE)
     */
    public static String fullFormat(double value) {
        DecimalFormat df = buildFormat(decimals());
        return df.format(value);
    }

    /**
     * Formatiert die Zahl abhängig von `currency.short-format`.
     * Kein Symbol, keine Farbe - nur die nackte Zahl.
     * Beispiel: "1,5K" oder "1.500,00"
     */
    public static String formatNumber(double value) {
        FancyCrystalPlugin plugin = FancyCrystalPlugin.getInstance();
        boolean shortFmt = plugin.getConfig().getBoolean("currency.short-format", true);
        return shortFmt ? shortFormat(value) : fullFormat(value);
    }

    /**
     * Kompatibilitätsmethode: liefert dasselbe wie formatNumber(), aber ohne
     * Währungssymbol (da das Symbol jetzt aus der Config entfernt wurde).
     * Wird weiterhin von CrystalEconomy.format() aufgerufen.
     */
    public static String formatWithSymbol(double value) {
        return formatNumber(value);
    }

    private static int decimals() {
        return FancyCrystalPlugin.getInstance().getConfig().getInt("currency.decimals", 2);
    }

    private static DecimalFormat buildFormat(int decimals) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.GERMANY);
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        StringBuilder pattern = new StringBuilder("#,##0");
        if (decimals > 0) {
            pattern.append('.');
            pattern.append("0".repeat(decimals));
        }
        return new DecimalFormat(pattern.toString(), symbols);
    }
}
