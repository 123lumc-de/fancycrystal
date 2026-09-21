package de.lmcstudios.fancycrystal.economy;

import de.lmcstudios.fancycrystal.FancyCrystalPlugin;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CrystalEconomy implements Economy {

    private final FancyCrystalPlugin plugin;
    private final CrystalStorage storage;

    public CrystalEconomy(FancyCrystalPlugin plugin) {
        this.plugin = plugin;
        this.storage = plugin.getStorage();
    }

    @Override
    public boolean isEnabled() { return plugin.isEnabled(); }

    @Override
    public String getName() { return "Crystals"; }

    @Override
    public boolean hasBankSupport() { return false; }

    @Override
    public int fractionalDigits() {
        return plugin.getConfig().getInt("currency.decimals", 2);
    }

    @Override
    public String format(double amount) {
        String symbol = plugin.getConfig().getString("currency.symbol", "✦");
        return String.format("%.2f %s", amount, symbol);
    }

    @Override
    public String currencyNamePlural() {
        return plugin.getConfig().getString("currency.name-plural", "Crystals");
    }

    @Override
    public String currencyNameSingular() {
        return plugin.getConfig().getString("currency.name", "Crystal");
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) { return true; }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) { return true; }

    @Override
    public boolean hasAccount(String playerName) { return true; }

    @Override
    public boolean hasAccount(String playerName, String worldName) { return true; }

    @Override
    public double getBalance(OfflinePlayer player) {
        return storage.getBalance(player.getUniqueId());
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return getBalance(player);
    }

    @Override
    public double getBalance(String playerName) {
        OfflinePlayer player = plugin.getServer().getOfflinePlayer(playerName);
        return storage.getBalance(player.getUniqueId());
    }

    @Override
    public double getBalance(String playerName, String world) {
        return getBalance(playerName);
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return getBalance(player) >= amount;
    }

    @Override
    public boolean has(OfflinePlayer player, String world, double amount) {
        return has(player, amount);
    }

    @Override
    public boolean has(String playerName, double amount) {
        return getBalance(playerName) >= amount;
    }

    @Override
    public boolean has(String playerName, String world, double amount) {
        return has(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        if (amount < 0)
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Betrag negativ");
        UUID uuid = player.getUniqueId();
        double bal = storage.getBalance(uuid);
        if (bal < amount)
            return new EconomyResponse(0, bal, EconomyResponse.ResponseType.FAILURE, "Nicht genug Guthaben");
        storage.setBalance(uuid, bal - amount);
        storage.saveAsync();
        return new EconomyResponse(amount, bal - amount, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String world, double amount) {
        return withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        OfflinePlayer player = plugin.getServer().getOfflinePlayer(playerName);
        return withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String world, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        if (amount < 0)
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Betrag negativ");
        UUID uuid = player.getUniqueId();
        double bal = storage.getBalance(uuid);
        storage.setBalance(uuid, bal + amount);
        storage.saveAsync();
        return new EconomyResponse(amount, bal + amount, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String world, double amount) {
        return depositPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        OfflinePlayer player = plugin.getServer().getOfflinePlayer(playerName);
        return depositPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String world, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) { return true; }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String world) { return true; }

    @Override
    public boolean createPlayerAccount(String playerName) { return true; }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) { return true; }

    private EconomyResponse notSupported() {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banken nicht unterstützt");
    }

    @Override public EconomyResponse createBank(String name, OfflinePlayer player) { return notSupported(); }
    @Override public EconomyResponse createBank(String name, String world) { return notSupported(); }
    @Override public EconomyResponse deleteBank(String name) { return notSupported(); }
    @Override public EconomyResponse bankBalance(String name) { return notSupported(); }
    @Override public EconomyResponse bankHas(String name, double amount) { return notSupported(); }
    @Override public EconomyResponse bankWithdraw(String name, double amount) { return notSupported(); }
    @Override public EconomyResponse bankDeposit(String name, double amount) { return notSupported(); }
    @Override public EconomyResponse isBankOwner(String name, OfflinePlayer player) { return notSupported(); }
    @Override public EconomyResponse isBankOwner(String name, String playerName) { return notSupported(); }
    @Override public EconomyResponse isBankMember(String name, OfflinePlayer player) { return notSupported(); }
    @Override public EconomyResponse isBankMember(String name, String playerName) { return notSupported(); }
    @Override public List<String> getBanks() { return Collections.emptyList(); }
}
