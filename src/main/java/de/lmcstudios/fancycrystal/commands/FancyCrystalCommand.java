package de.lmcstudios.fancycrystal.commands;

import de.lmcstudios.fancycrystal.FancyCrystalPlugin;
import de.lmcstudios.fancycrystal.util.NumberFormatter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class FancyCrystalCommand implements CommandExecutor {

    private final FancyCrystalPlugin plugin;
    private static final MiniMessage MM = MiniMessage.miniMessage();

    public FancyCrystalCommand(FancyCrystalPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        String prefix = "<gradient:#00E5FF:#7B68EE>[FancyCrystal]</gradient> ";

        if (args.length == 0) {
            sender.sendMessage(MM.deserialize(prefix + "<gray>Nutzung: <aqua>/fc <reload|info>"));
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "reload" -> {
                if (!sender.hasPermission("fancycrystal.admin")) {
                    sender.sendMessage(MM.deserialize(prefix + "<red>Keine Berechtigung."));
                    return true;
                }

                long start = System.currentTimeMillis();

                // 1. Config neu laden
                plugin.reloadConfig();

                // 2. Shop-Items neu einlesen
                plugin.getShopConfig().load();

                // 3. Balances aus Datei neu in den Cache laden
                plugin.getStorage().load();

                long time = System.currentTimeMillis() - start;

                sender.sendMessage(MM.deserialize(prefix + "<green>FancyCrystal neu geladen! <gray>(" + time + "ms)"));

                // Info an alle mit Admin-Permission (optional)
                Bukkit.getOnlinePlayers().stream()
                    .filter(p -> !p.equals(sender) && p.hasPermission("fancycrystal.admin"))
                    .forEach(p -> p.sendMessage(MM.deserialize(prefix + "<gray>Plugin wurde von <yellow>" + sender.getName() + "</yellow> neu geladen.")));

                plugin.getLogger().info("Reload durch " + sender.getName() + " abgeschlossen (" + time + "ms)");
            }

            case "info" -> {
                sender.sendMessage(MM.deserialize(prefix + "<gray>Version: <aqua>" + plugin.getPluginMeta().getVersion()));
                sender.sendMessage(MM.deserialize(prefix + "<gray>Vault: " + (Bukkit.getPluginManager().getPlugin("Vault") != null ? "<green>aktiv" : "<red>inaktiv")));
                sender.sendMessage(MM.deserialize(prefix + "<gray>PlaceholderAPI: " + (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null ? "<green>aktiv" : "<red>inaktiv")));
                sender.sendMessage(MM.deserialize(prefix + "<gray>Shop-Items: <aqua>" + plugin.getShopConfig().getAllItems().size()));
                sender.sendMessage(MM.deserialize(prefix + "<gray>Konten: <aqua>" + plugin.getStorage().getAccountCount()));
                sender.sendMessage(MM.deserialize(prefix + "<gray>Umlauf: <aqua>" + NumberFormatter.formatWithSymbol(plugin.getStorage().getTotalCirculation())));
                sender.sendMessage(MM.deserialize(prefix + "<gray>Shop-Item Anzahl: <aqua>" + plugin.getShopConfig().getAllItems().size()));
            }

            default -> sender.sendMessage(MM.deserialize(prefix + "<red>Unbekannter Subcommand. Nutze <aqua>/fc <reload|info>"));
        }

        return true;
    }
}
