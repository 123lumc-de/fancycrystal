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

                plugin.reloadConfig();
                plugin.getShopConfig().load();
                plugin.getStorage().load();

                long time = System.currentTimeMillis() - start;

                sender.sendMessage(MM.deserialize(prefix + "<green>FancyCrystal neu geladen! <gray>(" + time + "ms)"));
                plugin.getLogger().info("Reload durch " + sender.getName() + " abgeschlossen (" + time + "ms)");
            }

            case "info" -> {
                sender.sendMessage(MM.deserialize(prefix + "<gray>Version: <aqua>" + plugin.getPluginMeta().getVersion()));
                sender.sendMessage(MM.deserialize(prefix + "<gray>Shop-Items: <aqua>" + plugin.getShopConfig().getAllItems().size()));
                sender.sendMessage(MM.deserialize(prefix + "<gray>Konten: <aqua>" + plugin.getStorage().getAccountCount()));
                sender.sendMessage(MM.deserialize(prefix + "<gray>Umlauf: <aqua>" + NumberFormatter.formatWithSymbol(plugin.getStorage().getTotalCirculation())));
            }

            default -> sender.sendMessage(MM.deserialize(prefix + "<red>Unbekannter Subcommand. Nutze <aqua>/fc <reload|info>"));
        }

        return true;
    }
}
