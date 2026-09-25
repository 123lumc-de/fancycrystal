package de.lmcstudios.fancycrystal.commands;

import de.lmcstudios.fancycrystal.FancyCrystalPlugin;
import de.lmcstudios.fancycrystal.util.NumberFormatter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CrystalsCommand implements CommandExecutor {

    private final FancyCrystalPlugin plugin;
    private static final MiniMessage MM = MiniMessage.miniMessage();

    public CrystalsCommand(FancyCrystalPlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Nur für Spieler.");
            return true;
        }
        if (!player.hasPermission("fancycrystal.use")) {
            player.sendMessage(MM.deserialize(plugin.getConfig().getString("messages.prefix", "") +
                plugin.getConfig().getString("messages.no-permission", "<red>Keine Berechtigung.")));
            return true;
        }

        double bal = plugin.getStorage().getBalance(player.getUniqueId());
        String symbol = plugin.getConfig().getString("currency.symbol", "✦");
        String msg = plugin.getConfig().getString("messages.balance", "")
            .replace("%amount%", NumberFormatter.formatNumber(bal))
            .replace("%symbol%", symbol);

        player.sendMessage(MM.deserialize(
            plugin.getConfig().getString("messages.prefix", "") + msg));
        return true;
    }
}
