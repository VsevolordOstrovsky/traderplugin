package lordostrov.traderplugin.menu;


import de.tr7zw.nbtapi.NBTItem;
import lordostrov.traderplugin.manageDB.Manager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SellMenu {
    private static CustomInventory customInventory = new CustomInventory();
    private static HashMap<Player, Long> currentNumbers = new HashMap<>();
    private static HashMap<Player, Integer> currentPages = new HashMap<>();

    public static HashMap<Player, Long> getCurrentNumbers() {
        return currentNumbers;
    }
    public static HashMap<Player, Integer> getCurrentPages() {
        return currentPages;
    }

    // Храним текущие числа для каждого игрока

    public static void openBuyMenu(Player player, String title, int page){

        Inventory inv = Bukkit.createInventory(null, 54, title);

        ItemStack back = customInventory.createButton(Material.FILLED_MAP, "back_to_shop",
                "§aНазад", "§7При переходе назад данные не сохраняются");

        ItemStack close = customInventory.createButton(Material.BARRIER, "close_shop",
                "§aЗакрыть инвентарь", "§7Нажмите для закрытия");

        ItemStack next_page = customInventory.createButton(Material.GREEN_BANNER, "next_page", "§aПерейти на следующую страницу", "§7Нажмите для дейсвия");

        ItemStack last_page = customInventory.createButton(Material.GREEN_BANNER, "last_page", "§aПерейти на предыдущую страницу", "§7Нажмите для дейсвия");

        itemsOfAllPlayer(inv, player, page);

        inv.setItem(45, back);
        inv.setItem(49, close);
        inv.setItem(53, next_page);
        inv.setItem(52, last_page);

        currentPages.put(player, page);

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

        numbersButtons(inv, "banner");
        fillBarrierSellMenu(inv, title);

        // Инициализируем число для игрока
        currentNumbers.put(player, 0L);

        player.openInventory(inv);
    }


    public static void openMyItemMenu(Player player){
        String title = "Мои лоты";

        Inventory inv = Bukkit.createInventory(null, 18, title);

        ItemStack back = customInventory.createButton(Material.FILLED_MAP, "back_to_shop",
                "§aНазад", "§7При переходе назад данные не сохраняются");

        ItemStack close = customInventory.createButton(Material.BARRIER, "close_shop",
                "§aЗакрыть инвентарь", "§7Нажмите для закрытия");

        itemsOfPlayer(inv, player);

        inv.setItem(9, back);
        inv.setItem(13, close);


        customInventory.fillInventory(inv, title);

        // Открываем инвентарь игроку
        player.openInventory(inv);
    }

    public static void openBuyItemMenu(Player player, ItemStack item){
        String title = "Покупка";
        Inventory inv = Bukkit.createInventory(null, 54, title);

        ItemStack back = customInventory.createButton(Material.FILLED_MAP, "back_to_shop",
                "§aНазад", "§7При переходе назад данные не сохраняются");

        ItemStack close = customInventory.createButton(Material.BARRIER, "close_shop",
                "§aЗакрыть инвентарь", "§7Нажмите для закрытия");

        ItemStack buy = customInventory.createButton(Material.EMERALD, "buy_item",
                "§aКупить");


        inv.setItem(45, back);
        inv.setItem(49, close);
        inv.setItem(53, buy);

        inv.setItem(10, item);

        numbersButtons(inv, "quantity");
        fillBarrierBuyItemMenu(inv, title);

        currentNumbers.put(player, 0L);

        // Открываем инвентарь игроку
        player.openInventory(inv);
    }

    private static void fillBarrierSellMenu(Inventory inv, String inventoryTitle) {
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

    private static void fillBarrierBuyItemMenu(Inventory inv, String inventoryTitle) {
        ItemStack barrier = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemStack green_barrier = new ItemStack(Material.LIME_STAINED_GLASS_PANE);

        // Сначала NBT
        NBTItem nbtItem = new NBTItem(barrier);
        nbtItem.setBoolean("immovable", true);
        barrier = nbtItem.getItem();

        // Потом ItemMeta и флаги
        ItemMeta meta = barrier.getItemMeta();
        meta.setDisplayName(inventoryTitle); // Устанавливаем пустое название
        barrier.setItemMeta(meta);

        // Сначала NBT
        nbtItem = new NBTItem(green_barrier);
        nbtItem.setBoolean("immovable", true);
        green_barrier = nbtItem.getItem();

        // Потом ItemMeta и флаги
        meta = green_barrier.getItemMeta();
        meta.setDisplayName(inventoryTitle); // Устанавливаем пустое название
        green_barrier.setItemMeta(meta);


        inv.setItem(33, barrier);
        inv.setItem(32, barrier);
        inv.setItem(9, green_barrier);
        inv.setItem(11, green_barrier);
        inv.setItem(3, barrier);
        inv.setItem(12, barrier);
        inv.setItem(21, barrier);

        for(int i = 0; i <= 2; i++){
            inv.setItem(i, green_barrier);
        }
        for(int i = 18; i <= 20; i++){
            inv.setItem(i, green_barrier);
        }

        for(int i = 5; i <= 30; i+=9){
            inv.setItem(i, barrier);
        }

        for(int i = 27; i <= 30; i++){
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


    private static void numbersButtons(Inventory inv, String prefix) {

        ItemStack banner_0 = customInventory.createButton(Material.CYAN_BANNER, prefix+"_0",
                "§a0");
        ItemStack banner_1 = customInventory.createButton(Material.CYAN_BANNER, prefix+"_1",
                "§a1");
        ItemStack banner_2 = customInventory.createButton(Material.CYAN_BANNER, prefix+"_2",
                "§a2");
        ItemStack banner_3 = customInventory.createButton(Material.CYAN_BANNER, prefix+"_3",
                "§a3");
        ItemStack banner_4 = customInventory.createButton(Material.CYAN_BANNER, prefix+"_4",
                "§a4");
        ItemStack banner_5 = customInventory.createButton(Material.CYAN_BANNER, prefix+"_5",
                "§a5");
        ItemStack banner_6 = customInventory.createButton(Material.CYAN_BANNER, prefix+"_6",
                "§a6");
        ItemStack banner_7 = customInventory.createButton(Material.CYAN_BANNER, prefix+"_7",
                "§a7");
        ItemStack banner_8 = customInventory.createButton(Material.CYAN_BANNER, prefix+"_8",
                "§a8");
        ItemStack banner_9 = customInventory.createButton(Material.CYAN_BANNER, prefix+"_9",
                "§a9");
        ItemStack banner_delete = customInventory.createButton(Material.RED_BANNER, prefix+"_delete",
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

    private static void itemsOfPlayer(Inventory inv, Player player) {
        String uuid = player.getUniqueId().toString();
        Manager dbManager = new Manager();

        try {
            // Получаем все записи игрока из marketPlayer
            ResultSet rs = dbManager.executeQuery(
                    "SELECT material, quantity, cost FROM marketPlayer WHERE uuid = '" + uuid + "'");

            int slot = 0; // Слот для размещения предметов в инвентаре

            while (rs.next()) {
                String materialName = rs.getString("material");
                int quantity = rs.getInt("quantity");
                int cost = rs.getInt("cost");

                // Получаем Material по имени
                Material material;
                try {
                    material = Material.valueOf(materialName.toUpperCase());
                } catch (IllegalArgumentException e) {
                    // Если материал не найден, пропускаем
                    continue;
                }

                // Форматируем название по новому стилю
                List<String> lore = Arrays.asList(
                        "§7Количество: §f" + quantity,
                        "§7Цена: §6" + cost + " USDT",
                        "§8Нажмите чтобы вернуть предметы"
                );

                ItemStack itemButton = createButtonOfItemPlayer(
                        player.getUniqueId().toString(),
                        material,
                        "player_item",
                        lore.toArray(new String[0]));

                // Добавляем кнопку в инвентарь
                inv.setItem(slot, itemButton);
                slot++;

                // Если инвентарь заполнен, выходим
                if (slot >= inv.getSize()) break;
            }

        } catch (SQLException e) {
            player.sendMessage("§cОшибка при загрузке ваших предметов");
            e.printStackTrace();
        } finally {
            dbManager.closeConnection();
        }
    }

    private static void itemsOfAllPlayer(Inventory inv, Player player, Integer page) {
        String uuid = player.getUniqueId().toString();
        Manager dbManager = new Manager();

        try {
            // Определяем количество предметов на странице и смещение
            int itemsPerPage = 45;
            int offset = (page - 1) * itemsPerPage;

            // Получаем все записи игроков из marketPlayer с учетом пагинации
            ResultSet rs = dbManager.executeQuery(
                    "SELECT material, quantity, cost FROM marketPlayer WHERE uuid != '" + uuid + "' LIMIT " + itemsPerPage + " OFFSET " + offset);

            int slot = 0; // Слот для размещения предметов в инвентаре

            while (rs.next()) {
                String materialName = rs.getString("material");
                int quantity = rs.getInt("quantity");
                int cost = rs.getInt("cost");

                // Получаем Material по имени
                Material material;
                try {
                    material = Material.valueOf(materialName.toUpperCase());
                } catch (IllegalArgumentException e) {
                    // Если материал не найден, пропускаем
                    continue;
                }

                // Форматируем название по новому стилю
                List<String> lore = Arrays.asList(
                        "§7Количество: §f" + quantity,
                        "§7Цена: §6" + cost + " USDT",
                        "§8Нажмите для взаимодействия"
                );

                ItemStack itemButton = createButtonOfItemPlayer(
                        player.getUniqueId().toString(),
                        material,
                        "all_player_item",
                        lore.toArray(new String[0]));

                // Добавляем кнопку в инвентарь
                inv.setItem(slot, itemButton);
                slot++;

                // Если инвентарь заполнен, выходим
                if (slot >= itemsPerPage) break;
            }

        } catch (SQLException e) {
            player.sendMessage("§cОшибка при загрузке предметов");
            e.printStackTrace();
        } finally {
            dbManager.closeConnection();
        }
    }


    private static ItemStack createButtonOfItemPlayer(String uuid,Material material, String buttonId, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);

        // Добавляем NBT-тег с идентификатором кнопки
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString("buttonId", buttonId);
        nbtItem.setBoolean("immovable", true);
        nbtItem.setString("playerUUID", uuid); // Сохраняем UUID

        return nbtItem.getItem();
    }

    public static int getPage(Player player){
        Manager dbManager = new Manager();
        String uuid = player.getUniqueId().toString();
        int counts = dbManager.countPlayerMarketRecords(uuid);
        System.out.println("-------------------- Counts: "+counts);
        int page = 1 + counts / 45;
        return page;
    }



}
