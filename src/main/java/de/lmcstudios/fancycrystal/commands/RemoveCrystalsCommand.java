package de.lmcstudios.fancycrystal.commands;

import de.lmcstudios.fancycrystal.FancyCrystalPlugin;
import de.lmcstudios.fancycrystal.util.NumberFormatter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class RemoveCrystalsCommand implements CommandExecutor {

    private final FancyCrystalPlugin plugin;
    private static final MiniMessage MM = MiniMessage.miniMessage();

    public RemoveCrystalsCommand(FancyCrystalPlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        String prefix = plugin.getConfig().getString("messages.prefix", "");

        if (!sender.hasPermission("fancycrystal.admin")) {
            sender.sendMessage(MM.deserialize(prefix +
                plugin.getConfig().getString("messages.no-permission", "<red>Keine Berechtigung.")));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(MM.deserialize(prefix + "<red>Nutzung: /removecrystals <Spieler> <Betrag>"));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(args[0]);
        if (target == null) {
            sender.sendMessage(MM.deserialize(prefix +
                plugin.getConfig().getString("messages.player-not-found", "<red>Spieler nicht gefunden.")));
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage(MM.deserialize(prefix +
                plugin.getConfig().getString("messages.invalid-amount", "<red>Ungültiger Betrag.")));
            return true;
        }

        double balance = plugin.getStorage().getBalance(target.getUniqueId());
        double toRemove = Math.min(balance, amount);
        plugin.getStorage().addBalance(target.getUniqueId(), -toRemove);
        plugin.getStorage().saveAsync();

        String symbol = plugin.getConfig().getString("currency.symbol", "✦");
        String msg = plugin.getConfig().getString("messages.balance-removed", "")
            .replace("%amount%", NumberFormatter.formatNumber(toRemove))
            .replace("%symbol%", symbol)
            .replace("%player%", target.getName() != null ? target.getName() : args[0]);

        sender.sendMessage(MM.deserialize(prefix + msg));
        return true;
    }
}
