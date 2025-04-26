package lordostrov.traderplugin.coins;



import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class Commands implements CommandExecutor {

    private final String category = "spot";
    private final String symbol = "ETHUSDT";



    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {


        if (command.getName().equalsIgnoreCase("buy")) {
            ManageCoin manageCoin = new ManageCoin(strings[0], category);
            if (strings.length == 0) {
                commandSender.sendMessage("Использование: /trade <ETHUSDT|BTCUSDT>");
                return true;
            }

            String pair = strings[0].toUpperCase();
            switch (pair) {
                case "ETHUSDT":
                    commandSender.sendMessage(ChatColor.GREEN + "Вы выбрали пару ETH/USDT");
                    break;
                case "BTCUSDT":
                    commandSender.sendMessage(ChatColor.GREEN + "Вы выбрали пару BTC/USDT");
                    break;
                default:
                    commandSender.sendMessage(ChatColor.RED + "Неизвестная торговая пара. Доступные варианты: ETHUSDT, BTCUSDT");
            }
            // Проверяем, является ли отправитель команды игроком
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(ChatColor.RED + "Эта команда может быть использована только игроком.");
                return true;
            }

            Player player = (Player) commandSender;
            player.sendMessage(ChatColor.GREEN + "Запрос информации о цене...");

            try {
                String response = manageCoin.getMarkPriceKline();
                ParseResponse parseResponse = new ParseResponse(response);

                // Проверяем, есть ли данные
                if (parseResponse.getTimesTamp() != null) {
                    player.sendMessage(ChatColor.YELLOW + "Данные по цене "+ strings[0] +":");
//                    player.sendMessage("Timestamp (ms): " + parseResponse.getTimesTamp());
//                    player.sendMessage("Open: " + parseResponse.getOpen());
//                    player.sendMessage("High: " + parseResponse.getHigh());
//                    player.sendMessage("Low: " + parseResponse.getLow());
                    player.sendMessage(parseResponse.getClose());
                } else {
                    player.sendMessage(ChatColor.RED + "Нет данных по цене для указанных параметров.");
                }
            } catch (IOException e) {
                player.sendMessage(ChatColor.RED + "Ошибка при выполнении запроса к API: " + e.getMessage());
                e.printStackTrace();
            }

            return true;
        }

        if (command.getName().equalsIgnoreCase("menu")) {


            Player player = (Player) commandSender;


            return true;
        }

        return false;
    }
}