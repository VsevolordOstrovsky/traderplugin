package lordostrov.traderplugin;



import lordostrov.traderplugin.coins.ManageCoin;
import lordostrov.traderplugin.coins.ParseResponse;
import lordostrov.traderplugin.manageDB.Manager;
import lordostrov.traderplugin.menu.CustomInventory;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Commands implements CommandExecutor {

    private final String category = "spot";
    private final String symbol = "ETHUSDT";
    private CustomInventory customInventory = new CustomInventory();
    Manager manager = new Manager();



    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {


        if (command.getName().equalsIgnoreCase("infoCoin")) {
            ManageCoin manageCoin = new ManageCoin(strings[0], category);
            if (strings.length == 0) {
                commandSender.sendMessage("Использование: /infoCoin <ETHUSDT|BTCUSDT|SOLUSDT|XRPUSDT>");
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
                case "SOLUSDT":
                    commandSender.sendMessage(ChatColor.GREEN + "Вы выбрали пару SOL/USDT");
                    break;
                case "XRPUSDT":
                    commandSender.sendMessage(ChatColor.GREEN + "Вы выбрали пару XRP/USDT");
                    break;
                default:
                    commandSender.sendMessage(ChatColor.RED + "Неизвестная торговая пара. Доступные варианты: ETHUSDT, BTCUSDT, ");
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

            customInventory.openHomeMenu(player);

            return true;
        }

        if (command.getName().equalsIgnoreCase("request")) {

            ResultSet rs = null;
            try {
                rs = manager.executeQuery("SELECT * FROM player");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            try {
                // Заголовок таблицы
                System.out.printf("%-36s %-20s %-10s %-10s%n", "UUID", "Name", "USDT", "Rating");
                System.out.println("-------------------------------------------------------------");

                while (rs.next()) {
                    String uuid = rs.getString("uuid");
                    String name = rs.getString("name");
                    String usdt = rs.getString("usdt");
                    int rating = rs.getInt("rating");

                    // Выводим данные в формате таблицы
                    System.out.printf("%-36s %-20s %-10s %-10d%n", uuid, name, usdt, rating);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                // Важно закрыть ResultSet и соединение после использования
                try {
                    if (rs != null) {
                        rs.close(); // Закрываем ResultSet
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                manager.closeConnection();
            }


        }

        return false;
    }
}