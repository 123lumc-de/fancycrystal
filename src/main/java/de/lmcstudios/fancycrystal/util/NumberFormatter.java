package de.lmcstudios.fancycrystal.util;

import de.lmcstudios.fancycrystal.FancyCrystalPlugin;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class NumberFormatter {

    private NumberFormatter() {}

    public static String shortFormat(double value) {
        boolean negative = value < 0;
        double abs = Math.abs(value);

        String suffix;
        double divisor;

        if (abs >= 1_000_000_000_000L) {
            suffix = "T";
            divisor = 1_000_000_000_000D;
        } else if (abs >= 1_000_000_000D) {
            suffix = "B";
            divisor = 1_000_000_000D;
        } else if (abs >= 1_000_000D) {
            suffix = "M";
            divisor = 1_000_000D;
        } else if (abs >= 1_000D) {
            suffix = "K";
            divisor = 1_000D;
        } else {
            DecimalFormat df = buildFormat(decimals());
            return df.format(value);
        }

        double result = abs / divisor;
        DecimalFormat df = buildFormat(1);
        String formatted = df.format(result);
        if (formatted.endsWith(",0")) {
            formatted = formatted.substring(0, formatted.length() - 2);
        }
        return (negative ? "-" : "") + formatted + suffix;
    }

    public static String fullFormat(double value) {
        DecimalFormat df = buildFormat(decimals());
        return df.format(value);
    }

    public static String formatWithSymbol(double value) {
        FancyCrystalPlugin plugin = FancyCrystalPlugin.getInstance();
        boolean shortFmt = plugin.getConfig().getBoolean("currency.short-format", true);
        String symbol = plugin.getConfig().getString("currency.symbol", "✦");
        String num = shortFmt ? shortFormat(value) : fullFormat(value);
        return num + " " + symbol;
    }

    public static String formatNumber(double value) {
        FancyCrystalPlugin plugin = FancyCrystalPlugin.getInstance();
        boolean shortFmt = plugin.getConfig().getBoolean("currency.short-format", true);
        return shortFmt ? shortFormat(value) : fullFormat(value);
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
