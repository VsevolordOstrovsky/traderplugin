package lordostrov.traderplugin.menu;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SellMenu {
    private static CustomInventory customInventory = new CustomInventory();

    public static void openBuyMenu(Player player){
        String title = "Рынок";

        Inventory inv = Bukkit.createInventory(null, 54, title);

        customInventory.fillInventory(inv, title);

        // Открываем инвентарь игроку
        player.openInventory(inv);
    }
    public static void openSellMenu(Player player){
        String title = "На продажу";

        Inventory inv = Bukkit.createInventory(null, 54, title);

        customInventory.fillInventory(inv, title);

        // Открываем инвентарь игроку
        player.openInventory(inv);
    }
    public static void openMyItemMenu(Player player){
        String title = "Мои лоты";

        Inventory inv = Bukkit.createInventory(null, 54, title);

        customInventory.fillInventory(inv, title);

        // Открываем инвентарь игроку
        player.openInventory(inv);
    }




}
