package lordostrov.traderplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TradeTabCompleter implements TabCompleter {

    private static final List<String> TRADE_PAIRS = Arrays.asList("ETHUSDT", "BTCUSDT", "SOLUSDT", "XRPUSDT");
    private static final List<String> TABLE_NAME = Arrays.asList("marketPlayer", "player", "rating", "cryptoPlayer");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("infoCoin")) {
            if (args.length == 1) {
                // Фильтруем варианты по введенному тексту
                for (String pair : TRADE_PAIRS) {
                    if (pair.toLowerCase().startsWith(args[0].toLowerCase())) {
                        completions.add(pair);
                    }
                }
            }
        }

        if (command.getName().equalsIgnoreCase("request")) {
            if (args.length == 1) {
                // Фильтруем варианты по введенному тексту
                for (String pair : TABLE_NAME) {
                    if (pair.toLowerCase().startsWith(args[0].toLowerCase())) {
                        completions.add(pair);
                    }
                }
            }
        }

        return completions;
    }
}