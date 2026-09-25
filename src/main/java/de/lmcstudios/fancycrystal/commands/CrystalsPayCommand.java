package de.lmcstudios.fancycrystal.commands;

import de.lmcstudios.fancycrystal.FancyCrystalPlugin;
import de.lmcstudios.fancycrystal.util.NumberFormatter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CrystalsPayCommand implements CommandExecutor {

    private final FancyCrystalPlugin plugin;
    private static final MiniMessage MM = MiniMessage.miniMessage();

    public CrystalsPayCommand(FancyCrystalPlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Nur für Spieler.");
            return true;
        }

        String prefix = plugin.getConfig().getString("messages.prefix", "");

        if (args.length < 2) {
            player.sendMessage(MM.deserialize(prefix + "<red>Nutzung: /crystalspay <Spieler> <Betrag>"));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            player.sendMessage(MM.deserialize(prefix +
                plugin.getConfig().getString("messages.player-not-found", "<red>Spieler nicht gefunden.")));
            return true;
        }
        if (target.equals(player)) {
            player.sendMessage(MM.deserialize(prefix +
                plugin.getConfig().getString("messages.pay-self", "<red>Nicht an dich selbst.")));
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            player.sendMessage(MM.deserialize(prefix +
                plugin.getConfig().getString("messages.invalid-amount", "<red>Ungültiger Betrag.")));
            return true;
        }

        if (!plugin.getStorage().has(player.getUniqueId(), amount)) {
            player.sendMessage(MM.deserialize(prefix +
                plugin.getConfig().getString("messages.pay-insufficient", "<red>Nicht genug Crystals.")));
            return true;
        }

        plugin.getStorage().addBalance(player.getUniqueId(), -amount);
        plugin.getStorage().addBalance(target.getUniqueId(), amount);
        plugin.getStorage().saveAsync();

        String symbol = plugin.getConfig().getString("currency.symbol", "✦");
        String amt = NumberFormatter.formatNumber(amount);

        player.sendMessage(MM.deserialize(prefix +
            plugin.getConfig().getString("messages.pay-success", "")
                .replace("%amount%", amt).replace("%symbol%", symbol)
                .replace("%target%", target.getName())));

        target.sendMessage(MM.deserialize(prefix +
            plugin.getConfig().getString("messages.pay-received", "")
                .replace("%amount%", amt).replace("%symbol%", symbol)
                .replace("%sender%", player.getName())));

        return true;
    }
}
