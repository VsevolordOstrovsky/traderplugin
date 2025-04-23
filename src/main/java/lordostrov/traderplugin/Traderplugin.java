package lordostrov.traderplugin;

import com.samjakob.spigui.SpiGUI;
import lordostrov.traderplugin.coins.Commands;
import lordostrov.traderplugin.coins.ManageCoin;
import lordostrov.traderplugin.manageDB.Manager;
import lordostrov.traderplugin.menu.CustomInventory;
import lordostrov.traderplugin.scoreboard.SidebarManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Traderplugin extends JavaPlugin {

    public static SpiGUI spiGUI;
    private static Traderplugin instance;

    @Override
    public void onEnable() {
        CustomInventory customInv = new CustomInventory(this);

        Manager manager = new Manager();
        manager.getConnection();
        spiGUI = new SpiGUI(this);
        // Plugin startup logic

        getCommand("buy").setExecutor(new Commands(customInv));
        getCommand("buy").setTabCompleter(new TradeTabCompleter());

        getCommand("menu").setExecutor(new Commands(customInv));

        new SidebarManager(this); // Активируем меню

        // Для уже подключённых игроков (если плагин включили при работающем сервере)
        for (Player player : Bukkit.getOnlinePlayers()) {
            new SidebarManager(this).createSidebar(player);
        }

        getServer().getPluginManager().registerEvents(customInv, this);

    }

    public static Traderplugin getInstance() {
        return instance;
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
        spiGUI = null;
    }
}
