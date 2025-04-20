package lordostrov.traderplugin.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

public class SidebarManager implements Listener {
    private final JavaPlugin plugin;

    public SidebarManager(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        startUpdateTask(); // Запускаем автообновление
    }

    // Создаём скорборд для игрока
    public void createSidebar(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("sidebar", "dummy", "§6§lИнформация");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(scoreboard);
        updateSidebar(player);
    }

    // Обновляем данные
    public void updateSidebar(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective("sidebar");

        if (objective == null) return;

        // Очистка старых строк
        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }

        // Добавление новых строк
        int score = 10;
        addLine(objective, "§7──────────", score--);
        addLine(objective, "§fBTC: §a" + getBtcBalance(player), score--);
        addLine(objective, "§bETH: §a" + getEthBalance(player), score--);
        addLine(objective, "§eSOL: §a" + getSolBalance(player), score--);
        addLine(objective, "§7──────────", score--);
        addLine(objective, "§fИгрок: §b" + player.getName(), score--);
    }

    private void addLine(Objective objective, String text, int score) {
        objective.getScore(text).setScore(score);
    }

    // Автообновление каждые 2 секунды
    private void startUpdateTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                updateSidebar(player);
            }
        }, 0L, 40L); // 40 тиков = 2 секунды
    }

    // Заглушки для баланса
    private String getBtcBalance(Player player) { return "0.001 BTC"; }
    private String getEthBalance(Player player) { return "0.5 ETH"; }
    private String getSolBalance(Player player) { return "10 SOL"; }

    // При входе на сервер
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        createSidebar(event.getPlayer());
    }
}