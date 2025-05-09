package lordostrov.traderplugin.scoreboard;

import lordostrov.traderplugin.manageDB.Manager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SidebarManager implements Listener {
    private final JavaPlugin plugin;
    private Manager managerDB = new Manager();

    public SidebarManager(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        startUpdateTask(); // Запускаем автообновление
    }

    // Создаём скорборд для игрока
    public void createSidebar(Player player) throws SQLException {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("sidebar", "dummy", "§6§lИнформация");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(scoreboard);
        updateSidebar(player);
    }

    // Обновляем данные
    public void updateSidebar(Player player) throws SQLException {
        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective("sidebar");

        if (objective == null) return;

        // Очистка старых строк
        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }

        // Добавление новых строк
        int score = 1;
        addLine(objective, "§7──────────", score++);
        addLine(objective, "§fUSDT: §a" + getUsdtBalance(player), score++);
        addLine(objective, "§fBTC: §a" + getBtcBalance(player), score++);
        addLine(objective, "§bETH: §a" + getEthBalance(player), score++);
        addLine(objective, "§eSOL: §a" + getSolBalance(player), score++);
        addLine(objective, "§7──────────", score++);
        addLine(objective, "§fИгрок: §b" + player.getName(), score++);
    }

    private void addLine(Objective objective, String text, int score) {
        objective.getScore(text).setScore(score);
    }

    // Автообновление каждые 5 секунды
    private void startUpdateTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                try {
                    updateSidebar(player);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 0L, 100L); // 100 тиков = 5 секунды
    }

    // Заглушки для баланса
    private String getCryptoBalanceUnsafe(Player player, String columnName, String currencySymbol) {
        String uuid = player.getUniqueId().toString();
        String query = "SELECT " + columnName + " FROM cryptoPlayer WHERE uuid = '" + uuid + "'";

        try (ResultSet rs = managerDB.executeQuery(query)) {
            if (rs.next()) {
                String balance = rs.getString(columnName);
                return (balance != null ? balance : "0") + " " + currencySymbol;
            } else {
                return "0 " + currencySymbol;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении баланса " + currencySymbol, e);
        } finally {
            managerDB.closeConnection(); // Закрываем соединение (но это не оптимально)
        }
    }

    private String getBtcBalance(Player player) {
        return getCryptoBalanceUnsafe(player, "BTC", "BTC");
    }

    private String getEthBalance(Player player) {
        return getCryptoBalanceUnsafe(player, "ETH", "ETH");
    }

    private String getSolBalance(Player player) {
        return getCryptoBalanceUnsafe(player, "SOL", "SOL");
    }

    public String getUsdtBalance(Player player) throws SQLException {
        String uuid = player.getUniqueId().toString();
        String query = "SELECT usdt FROM player WHERE uuid = '" + uuid + "'"; // Риск SQL-инъекции!

        try (ResultSet rs = managerDB.executeQuery(query)) {
            if (rs.next()) {
                String usdtBalance = rs.getString("usdt");
                return (usdtBalance != null ? usdtBalance : "0") + " USDT";
            } else {
                return "0 USDT";
            }
        }finally {
            managerDB.closeConnection();// Только ResultSet закрывается, Connection и Statement остаются открытыми!
        }
    }

    // При входе на сервер
    @EventHandler
    public void onJoin(PlayerJoinEvent event) throws SQLException {
        createSidebar(event.getPlayer());
    }
}