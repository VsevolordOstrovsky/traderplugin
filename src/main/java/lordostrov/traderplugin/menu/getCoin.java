package lordostrov.traderplugin.menu;

import lordostrov.traderplugin.coins.ManageCoin;
import lordostrov.traderplugin.coins.ParseResponse;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.IOException;

public class getCoin {
    private final String category = "spot";


    void getPrice(Player player, String symbol){
        ManageCoin manageCoin = new ManageCoin(symbol, category);
        player.sendMessage(ChatColor.GREEN + "Запрос информации о цене...");
        try {
            String response = manageCoin.getMarkPriceKline();
            ParseResponse parseResponse = new ParseResponse(response);

            // Проверяем, есть ли данные
            if (parseResponse.getTimesTamp() != null) {
                player.sendMessage(ChatColor.YELLOW + "Данные по цене "+ symbol +":");
                player.sendMessage(parseResponse.getClose());
            } else {
                player.sendMessage(ChatColor.RED + "Нет данных по цене для указанных параметров.");
            }
        } catch (IOException e) {
            player.sendMessage(ChatColor.RED + "Ошибка при выполнении запроса к API: " + e.getMessage());
            e.printStackTrace();
        }

    }

}
