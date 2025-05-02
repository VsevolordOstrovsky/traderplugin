package lordostrov.traderplugin.menu;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


import java.util.Arrays;


public class CustomInventory {


    public static void openHomeMenu(Player player) {
        String title = "Главное меню";
        // Создаем инвентарь с 27 слотами (3 ряда) и названием
        Inventory inv = Bukkit.createInventory(null, 18, title);



        ItemStack shop = createButton(Material.EMERALD, "open_shop",
                "§aМагазин", "§7Нажмите чтобы открыть магазин");

        ItemStack coinShop = createButton(Material.ANCIENT_DEBRIS, "coinShop",
                "§aПокупка криптовалют","§7Нажмите для открытия");

        ItemStack wallet = createButton(Material.CHEST, "wallet",
                "§aКошелёк", "§7Нажмите для открытия");

        ItemStack close = createButton(Material.BARRIER, "close_shop",
                "§aЗакрыть инвентарь", "§7Нажмите для закрытия");

        ItemStack info = createButton(Material.BOOK, "info", "§aINFO", "§7Нажмите для чтения");

        inv.setItem(3, shop);
        inv.setItem(4, coinShop);
        inv.setItem(5, wallet);
        inv.setItem(13, close);
        inv.setItem(17, info);

       fillInventory(inv, title);




        // Открываем инвентарь игроку
        player.openInventory(inv);
    }

    public static void openCoinShop(Player player) {
        String title = "Покупка криптовалют";
        Inventory inv = Bukkit.createInventory(null, 18, title);

        ItemStack back = createButton(Material.FILLED_MAP, "back_to_home",
                "§aНазад", "§7При переходе назад данные не сохраняются");

        ItemStack close = createButton(Material.BARRIER, "close_shop",
                "§aЗакрыть инвентарь", "§7Нажмите для закрытия");


        inv.setItem(9, back);
        inv.setItem(13, close);

        fillInventory(inv, title);

        // Открываем инвентарь игроку
        player.openInventory(inv);
    }

    public static void openWallet(Player player) {

        String title = "Кошелёк";
        // Создаем инвентарь с 27 слотами (3 ряда) и названием
        Inventory inv = Bukkit.createInventory(null, 18, title);

        ItemStack back = createButton(Material.FILLED_MAP, "back_to_home",
                "§aНазад", "§7При переходе назад данные не сохраняются");

        ItemStack close = createButton(Material.BARRIER, "close_shop",
                "§aЗакрыть инвентарь", "§7Нажмите для закрытия");

        inv.setItem(9, back);
        inv.setItem(13, close);
        fillInventory(inv, title);

        // Открываем инвентарь игроку
        player.openInventory(inv);
    }

    public static void openShop(Player player) {

        String title = "Магазин";

        // Создаем инвентарь с 27 слотами (3 ряда) и названием
        Inventory inv = Bukkit.createInventory(null, 18, title);


        ItemStack buy = createButton(Material.GOLD_INGOT, "buy_market_menu",
                "§aРынок", "§7Нажмите для перехода");

        ItemStack my_lots = createButton(Material.BOOKSHELF, "my_lots_market_menu",
                "§aМои лоты", "§7Нажмите для перехода");

        ItemStack sell = createButton(Material.EMERALD, "sell_market_menu",
                "§aВыставить предметы на продажу", "§7Нажмите для перехода");


        ItemStack back = createButton(Material.FILLED_MAP, "back_to_home",
                "§aНазад", "§7При переходе назад данные не сохраняются");

        ItemStack close = createButton(Material.BARRIER, "close_shop",
                "§aЗакрыть инвентарь", "§7Нажмите для закрытия");

        inv.setItem(3, buy);
        inv.setItem(4, sell);
        inv.setItem(5, my_lots);
        inv.setItem(9, back);
        inv.setItem(13, close);

        fillInventory(inv, title);

        // Открываем инвентарь игроку
        player.openInventory(inv);
    }

    public static ItemStack createButton(Material material, String buttonId, String displayName, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);

        // Добавляем NBT-тег с идентификатором кнопки
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString("buttonId", buttonId);
        nbtItem.setBoolean("immovable", true);
        return nbtItem.getItem();
    }

    private static ItemStack createFillItem(String inventoryTitle) {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);

        // Сначала NBT
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setBoolean("immovable", true);
        item = nbtItem.getItem();

        // Потом ItemMeta и флаги
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(inventoryTitle); // Устанавливаем пустое название
        item.setItemMeta(meta);

        return item;
    }


    public static void fillInventory(Inventory inv, String inventoryTitle) {
        ItemStack filler = createFillItem(inventoryTitle);

        // Перебираем все слоты инвентаря
        for (int slot = 0; slot < inv.getSize(); slot++) {
            ItemStack currentItem = inv.getItem(slot);

            // Если слот пустой (AIR или null), заполняем его
            if (currentItem == null || currentItem.getType() == Material.AIR) {
                inv.setItem(slot, filler);
            }
        }
    }



}
