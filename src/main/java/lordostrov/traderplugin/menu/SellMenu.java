package lordostrov.traderplugin.menu;


import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

public class SellMenu {
    private static CustomInventory customInventory = new CustomInventory();
    private static HashMap<Player, Long> currentNumbers = new HashMap<>();

    public static HashMap<Player, Long> getCurrentNumbers() {
        return currentNumbers;
    }

    // Храним текущие числа для каждого игрока

    public static void openBuyMenu(Player player){
        String title = "Рынок";

        Inventory inv = Bukkit.createInventory(null, 54, title);

        ItemStack back = customInventory.createButton(Material.FILLED_MAP, "back_to_shop",
                "§aНазад", "§7При переходе назад данные не сохраняются");

        ItemStack close = customInventory.createButton(Material.BARRIER, "close_shop",
                "§aЗакрыть инвентарь", "§7Нажмите для закрытия");

        inv.setItem(45, back);
        inv.setItem(49, close);

        customInventory.fillInventory(inv, title);



        // Открываем инвентарь игроку
        player.openInventory(inv);
    }


    public static void openSellMenu(Player player){
        String title = "На продажу";

        Inventory inv = Bukkit.createInventory(null, 54, title);

        ItemStack back = customInventory.createButton(Material.FILLED_MAP, "back_to_shop",
                "§aНазад", "§7При переходе назад данные не сохраняются");

        ItemStack close = customInventory.createButton(Material.BARRIER, "close_shop",
                "§aЗакрыть инвентарь", "§7Нажмите для закрытия");

        ItemStack push = customInventory.createButton(Material.EMERALD, "push_item",
                "§aВыставить");

        inv.setItem(45, back);
        inv.setItem(49, close);
        inv.setItem(53, push);

        numbersButtons(inv);
        fillBarrierSellMenu(inv, title);

        // Инициализируем число для игрока
        currentNumbers.put(player, 0L);

        player.openInventory(inv);
    }


    public static void openMyItemMenu(Player player){
        String title = "Мои лоты";

        Inventory inv = Bukkit.createInventory(null, 54, title);

        ItemStack back = customInventory.createButton(Material.FILLED_MAP, "back_to_shop",
                "§aНазад", "§7При переходе назад данные не сохраняются");

        ItemStack close = customInventory.createButton(Material.BARRIER, "close_shop",
                "§aЗакрыть инвентарь", "§7Нажмите для закрытия");

        inv.setItem(45, back);
        inv.setItem(49, close);


        customInventory.fillInventory(inv, title);

        // Открываем инвентарь игроку
        player.openInventory(inv);
    }

    public static void fillBarrierSellMenu(Inventory inv, String inventoryTitle) {
        ItemStack barrier = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);

        // Сначала NBT
        NBTItem nbtItem = new NBTItem(barrier);
        nbtItem.setBoolean("immovable", true);
        barrier = nbtItem.getItem();

        // Потом ItemMeta и флаги
        ItemMeta meta = barrier.getItemMeta();
        meta.setDisplayName(inventoryTitle); // Устанавливаем пустое название
        barrier.setItemMeta(meta);


        inv.setItem(33, barrier);
        for(int i = 5; i <= 32; i+=9){
            inv.setItem(i, barrier);
        }

        for(int i = 36; i <= 44; i++){
            inv.setItem(i, barrier);
        }
        for(int i = 46; i <= 48; i++){
            inv.setItem(i, barrier);
        }
        for(int i = 50; i <= 52; i++){
            inv.setItem(i, barrier);
        }


    }


    public static void numbersButtons(Inventory inv) {

        ItemStack banner_0 = customInventory.createButton(Material.CYAN_BANNER, "banner_0",
                "§a0");
        ItemStack banner_1 = customInventory.createButton(Material.CYAN_BANNER, "banner_1",
                "§a1");
        ItemStack banner_2 = customInventory.createButton(Material.CYAN_BANNER, "banner_2",
                "§a2");
        ItemStack banner_3 = customInventory.createButton(Material.CYAN_BANNER, "banner_3",
                "§a3");
        ItemStack banner_4 = customInventory.createButton(Material.CYAN_BANNER, "banner_4",
                "§a4");
        ItemStack banner_5 = customInventory.createButton(Material.CYAN_BANNER, "banner_5",
                "§a5");
        ItemStack banner_6 = customInventory.createButton(Material.CYAN_BANNER, "banner_6",
                "§a6");
        ItemStack banner_7 = customInventory.createButton(Material.CYAN_BANNER, "banner_7",
                "§a7");
        ItemStack banner_8 = customInventory.createButton(Material.CYAN_BANNER, "banner_8",
                "§a8");
        ItemStack banner_9 = customInventory.createButton(Material.CYAN_BANNER, "banner_9",
                "§a9");
        ItemStack banner_delete = customInventory.createButton(Material.RED_BANNER, "banner_delete",
                "§adel");

        inv.setItem(34, banner_0);
        inv.setItem(24, banner_1);
        inv.setItem(25, banner_2);
        inv.setItem(26, banner_3);
        inv.setItem(15, banner_4);
        inv.setItem(16, banner_5);
        inv.setItem(17, banner_6);
        inv.setItem(6, banner_7);
        inv.setItem(7, banner_8);
        inv.setItem(8, banner_9);
        inv.setItem(35, banner_delete);
    }




}
