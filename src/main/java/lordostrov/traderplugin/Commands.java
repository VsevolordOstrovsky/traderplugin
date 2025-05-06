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
            Player player = (Player) commandSender;
            if(strings.length < 1) {
                player.sendMessage(ChatColor.RED + "Ошибка команды /request!!!");
                player.sendMessage(ChatColor.RED + "Формат команды: /request <ETHUSDT|BTCUSDT|SOLUSDT|XRPUDT>");
                return false;
            }
            String tableName = strings[0];

            ResultSet rs = null;
            try {
                rs = manager.executeQuery("SELECT * FROM "+tableName);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


            try {
                switch (tableName) {
                    case "marketPlayer":
                        // Заголовок таблицы
                        System.out.println("Данные из таблицы marketPlayer:");
                        System.out.println("----------------------------------");
                        System.out.printf("%-36s | %-20s | %-8s | %-10s%n",
                                "UUID", "Material", "Quantity", "Cost");
                        System.out.println("----------------------------------");

                        while (rs.next()) {
                            String uuid = rs.getString("uuid");
                            String material = rs.getString("material");
                            int quantity = rs.getInt("quantity");
                            int cost = rs.getInt("cost");

                            System.out.printf("%-36s | %-20s | %-8d | %-10d%n",
                                    uuid, material, quantity, cost);
                        }
                        break;
                    case "player":
                        // Заголовок таблицы
                        System.out.println("Данные из таблицы player:");
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
                        break;
                    case "cryptoPlayer":
                        // Заголовок таблицы
                        System.out.println("Данные из таблицы cryptoPlayer:");
                        System.out.printf("%-36s %-20s %-10s %-10s %-10s%n", "UUID", "BTC", "ETH", "SOL", "XRP");
                        System.out.println("-------------------------------------------------------------");
                        while (rs.next()) {
                            String uuid = rs.getString("uuid");
                            int BTC = rs.getInt("BTC");
                            int ETH = rs.getInt("ETH");
                            int SOL = rs.getInt("SOL");
                            int XRP = rs.getInt("XRP");

                            // Выводим данные в формате таблицы
                            System.out.printf("%-36s %-20s %-10s %-10s %-10s%n", uuid, BTC, ETH, SOL, XRP);
                        }
                        break;
                    case "rating":
                        // Заголовок таблицы
                        System.out.println("Данные из таблицы rating:");
                        System.out.printf("%-10s %-36s %-10s%n", "rating", "UUID", "USDT");
                        System.out.println("-------------------------------------------------------------");
                        while (rs.next()) {
                            int rating = rs.getInt("rating");
                            String uuid = rs.getString("uuid");
                            String usdt = rs.getString("usdt");


                            // Выводим данные в формате таблицы
                            System.out.printf("%-10s %-36s %-10s%n", rating, uuid, usdt);
                        }
                        break;

                }


            }catch (SQLException e) {
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
            return true;
        }

        if(command.getName().equalsIgnoreCase("getUSDT")){
            Player player = (Player) commandSender;
            if(strings.length < 1) {
                player.sendMessage(ChatColor.RED + "Ошибка команды /getUSDT!!!");
                player.sendMessage(ChatColor.RED + "Формат команды: /getUSDT <quantity>");
                return false;
            }
            String tableName = strings[0];
            String uuid = player.getUniqueId().toString();
            manager.addUsdtToPlayer(uuid, tableName);
            player.sendMessage(ChatColor.BLUE+"Полученно USDT: "+tableName);
            return true;


        }

        return false;
    }
}