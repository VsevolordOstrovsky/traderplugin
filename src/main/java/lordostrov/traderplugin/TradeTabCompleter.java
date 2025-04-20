package lordostrov.traderplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TradeTabCompleter implements TabCompleter {

    private static final List<String> TRADE_PAIRS = Arrays.asList("ETHUSDT", "BTCUSDT", "SOLUSDT", "XRPUSDT");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("buy")) {
            if (args.length == 1) {
                // Фильтруем варианты по введенному тексту
                for (String pair : TRADE_PAIRS) {
                    if (pair.toLowerCase().startsWith(args[0].toLowerCase())) {
                        completions.add(pair);
                    }
                }
            }
        }

        return completions;
    }
}