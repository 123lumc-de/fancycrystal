package de.lmcstudios.fancycrystal.economy;

import de.lmcstudios.fancycrystal.FancyCrystalPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CrystalStorage {

    private final FancyCrystalPlugin plugin;
    private final File file;
    private FileConfiguration data;
    private final Map<UUID, Double> cache = new HashMap<>();

    public CrystalStorage(FancyCrystalPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "balances.yml");
    }

    public void load() {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Konnte balances.yml nicht erstellen: " + e.getMessage());
            }
        }
        data = YamlConfiguration.loadConfiguration(file);
        cache.clear();
        for (String key : data.getKeys(false)) {
            try {
                cache.put(UUID.fromString(key), data.getDouble(key));
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public void save() {
        for (Map.Entry<UUID, Double> entry : cache.entrySet()) {
            data.set(entry.getKey().toString(), entry.getValue());
        }
        try {
            data.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Konnte balances.yml nicht speichern: " + e.getMessage());
        }
    }

    public double getBalance(UUID uuid) {
        return cache.getOrDefault(uuid, 0.0);
    }

    public void setBalance(UUID uuid, double amount) {
        cache.put(uuid, amount);
    }

    /**
     * Addiert (oder subtrahiert bei negativem Delta) einen Betrag auf das Konto.
     * Negative Ergebnisse werden auf 0 begrenzt.
     */
    public void addBalance(UUID uuid, double delta) {
        double current = getBalance(uuid);
        cache.put(uuid, Math.max(0, current + delta));
    }

    /**
     * Prüft, ob der Spieler mindestens den angegebenen Betrag hat.
     */
    public boolean has(UUID uuid, double amount) {
        return getBalance(uuid) >= amount;
    }

    public void saveAsync() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, this::save);
    }

    public double getTotalCirculation() {
        double sum = 0.0;
        for (double v : cache.values()) sum += v;
        return sum;
    }

    public int getAccountCount() {
        return cache.size();
    }
}
