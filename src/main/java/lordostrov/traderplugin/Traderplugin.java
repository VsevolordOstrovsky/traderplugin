package lordostrov.traderplugin;


import lordostrov.traderplugin.manageDB.Manager;
import lordostrov.traderplugin.menu.InventoryListener;
import lordostrov.traderplugin.scoreboard.SidebarManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class Traderplugin extends JavaPlugin {


    @Override
    public void onEnable() {


        getServer().getPluginManager().registerEvents(new InventoryListener(), this);


        // Регистрация команд
        /*-----------------*/
        this.getCommand("infoCoin").setExecutor(new Commands());
        this.getCommand("infoCoin").setTabCompleter(new TradeTabCompleter());
        /*-----------------*/
        this.getCommand("menu").setExecutor(new Commands());
        /*-----------------*/

        // Создание сайдбара для всех онлайн-игроков
        SidebarManager sidebarManager = new SidebarManager(this);
        for (Player player : Bukkit.getOnlinePlayers()) {
            sidebarManager.createSidebar(player);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.sendTitle(ChatColor.BLUE + player.getName(), "Добро пожаловать!", 0, 75, 0);
        Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                // Добавление пользователя в таблицы

            }
        });

    }

    @Override
    public void onDisable() {

    }
}