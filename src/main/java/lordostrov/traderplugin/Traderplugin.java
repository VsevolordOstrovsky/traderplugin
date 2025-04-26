package lordostrov.traderplugin;


import lordostrov.traderplugin.coins.Commands;
import lordostrov.traderplugin.coins.ManageCoin;
import lordostrov.traderplugin.manageDB.Manager;
import lordostrov.traderplugin.scoreboard.SidebarManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Traderplugin extends JavaPlugin {


    @Override
    public void onEnable() {
        Manager manager = new Manager();
        manager.getConnection();


        // Регистрация команд
        this.getCommand("buy").setExecutor(new Commands());
        this.getCommand("buy").setTabCompleter(new TradeTabCompleter());
        this.getCommand("menu").setExecutor(new Commands());

        // Создание сайдбара для всех онлайн-игроков
        SidebarManager sidebarManager = new SidebarManager(this);
        for (Player player : Bukkit.getOnlinePlayers()) {
            sidebarManager.createSidebar(player);
        }
    }

    @Override
    public void onDisable() {

    }
}