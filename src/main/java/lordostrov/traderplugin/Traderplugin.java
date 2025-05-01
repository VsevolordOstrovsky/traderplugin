package lordostrov.traderplugin;


import lordostrov.traderplugin.manageDB.Manager;
import lordostrov.traderplugin.menu.InventoryListener;
import lordostrov.traderplugin.scoreboard.SidebarManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class Traderplugin extends JavaPlugin implements Listener {
    Manager managerDB = new Manager();

    @Override
    public void onEnable() {

        System.out.println("------------------------------------Traderplugin enabled------------------------------------");
        getServer().getPluginManager().registerEvents(this, this);


        getServer().getPluginManager().registerEvents(new InventoryListener(), this);


        // Регистрация команд
        /*-----------------*/
        this.getCommand("infoCoin").setExecutor(new Commands());
        this.getCommand("infoCoin").setTabCompleter(new TradeTabCompleter());
        /*-----------------*/
        this.getCommand("menu").setExecutor(new Commands());
        /*-----------------*/
        this.getCommand("request").setExecutor(new Commands());

        // Создание сайдбара для всех онлайн-игроков
        SidebarManager sidebarManager = new SidebarManager(this);
        for (Player player : Bukkit.getOnlinePlayers()) {
            sidebarManager.createSidebar(player);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        System.out.println("---------------------------------------Traderplugin joined-------------------------------");

        Player player = event.getPlayer();
        player.sendTitle(ChatColor.BLUE + player.getName(), "Добро пожаловать!", 0, 75, 0);

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                // Создаем таблицы если их нет
                managerDB.createTables();
                // Добавляем игрока если его нет в БД
                managerDB.addPlayerIfNotExists(event);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onDisable() {
        System.out.printf("---------------------------------------Traderplugin disabled---------------------------------");
    }
}