package lordostrov.traderplugin.menu;
import lordostrov.traderplugin.manageDB.Manager;
import lordostrov.traderplugin.trade.ManageInventory;
import lordostrov.traderplugin.trade.ManageStrok;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InventoryListener implements Listener {
    CustomInventory customInventory = new CustomInventory();
    SellMenu sellMenu = new SellMenu();
    HashMap<Player, Long> currentNumbers = sellMenu.getCurrentNumbers();
    HashMap<Player, Integer> currentPages = sellMenu.getCurrentPages();
    HashMap<Player, Long> currentPricesMap = new HashMap<>();
    HashMap<Player, Long> currentQuantitiesMap = new HashMap<>();
    ManageInventory manageInventory = new ManageInventory();
    Manager managerBD = new Manager();
    ManageStrok manageStrok = new ManageStrok();

    int page = 1;

    BannerHandlerConfig quantityConfig = new BannerHandlerConfig();

    BannerHandlerConfig priceConfig = new BannerHandlerConfig();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack clicked = event.getCurrentItem();


        if (clicked == null) return;
        if (clicked == null || clicked.getType() == Material.AIR) return;

        NBTItem nbt = new NBTItem(clicked);
        if (nbt.hasKey("immovable") && nbt.getBoolean("immovable")) {
            event.setCancelled(true);
            if (!nbt.hasTag("buttonId")) return;

            String buttonId = nbt.getString("buttonId");
            Player player = (Player) event.getWhoClicked();
            page = currentPages.getOrDefault(player, 1);
            // Обработка цифровых баннеров
            if (buttonId.startsWith("banner_")) {

                priceConfig.setPrefix("banner");
                priceConfig.setMaxValue(1_000_000_000L);
                priceConfig.setTitleTemplate("Цена за 1: %s");
                handleBannerClick(player, buttonId, event.getInventory(), currentPricesMap, priceConfig);
                return;
            }
            if(buttonId.startsWith("quantity_")){

                quantityConfig.setPrefix("quantity");
                quantityConfig.setTitleTemplate("Количество: %s");

                handleBannerClick(player, buttonId,  event.getInventory(), currentQuantitiesMap, quantityConfig);
                return;
            }
            switch (buttonId) {
                /*---HomeMenu---*/
                case "back_to_home":
                    open_home(player);
                    break;
                case "open_shop":
                    open_shop(player);
                    break;
                case "close_shop":
                    closeInventory(player);
                    break;
                case "coinShop":
                    coinShop(player);
                    break;
                case "wallet":
                    wallet(player);
                    break;
                case "info":
                    info(player);
                    break;
                /*------------*/

                /*---SellMenu---*/
                case "buy_market_menu":
                    newTitleOfBuyMenu(player,page);
                    break;
                case "sell_market_menu":
                    openSellMenu(player);
                    break;
                case "my_lots_market_menu":
                    openMyItemMenu(player);
                    break;
                case "back_to_shop":
                    open_shop(player);
                    currentPricesMap.put(player, 0L);
                    currentQuantitiesMap.put(player, 0L);
                    break;
                // сделать push_item button
                case "push_item":
                    pushItemToMarket(player, event.getInventory());
                    break;
                case "player_item":
                    System.out.println(clicked.getType());
                    givePlayerItemsFromMarket(player, clicked.getType());
                    openMyItemMenu(player);
                    break;
                case "last_page":
                    if(page > 1){
                        page--;
                        newTitleOfBuyMenu(player, page);
                    }else{
                        player.sendMessage(ChatColor.RED + "Это первая страница");
                    }
                    break;
                case "next_page":
                    if(page < sellMenu.getPage(player)){
                        page++;
                        newTitleOfBuyMenu(player, page);
                    }else{
                        player.sendMessage(ChatColor.RED + "Страниц больше нету");
                    }
                    break;
                case "all_player_item":
                    openBuyItemMenu(player, event);
                    break;


                /*--------------*/
                case "teleport_spawn":
                    player.teleport(player.getWorld().getSpawnLocation());
                    player.sendMessage("§eВы телепортированы на спавн!");
                    break;

                default:
                    player.sendMessage("§cТакой кнопки нету: " + buttonId);
            }

        }


    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() == null &&
                event.getView().getTitle().startsWith("На продажу | Цена: ")) {
            currentNumbers.remove((Player) event.getPlayer());
        }
    }


    /*---HomeMenu---*/
    void open_home(Player player) {customInventory.openHomeMenu(player);}
    void open_shop(Player player) {customInventory.openShop(player);}
    void coinShop(Player player){
        customInventory.openCoinShop(player);
    }
    void wallet(Player player){
        customInventory.openWallet(player);
    }
    void info(Player player){
        player.sendMessage("Будет открыта информация");
    }
    /*------------*/

    /*---SellMenu---*/
    void openSellMenu(Player player){sellMenu.openSellMenu(player);}
    void openMyItemMenu(Player player){sellMenu.openMyItemMenu(player);}
    void closeInventory(Player player){
        manageInventory.deleteCloseButton(player);
        player.closeInventory();
    }
    void openBuyItemMenu(Player player, InventoryClickEvent event){
        ItemStack itemStack = event.getCurrentItem();
        int quan = getQuantityFromLore(itemStack);
        quantityConfig.setMaxValue(quan);
        sellMenu.openBuyItemMenu(player, itemStack);
    }


    private void handleBannerClick(Player player, String buttonId, Inventory inv,
                                   Map<Player, Long> currentValuesMap,
                                   BannerHandlerConfig config) {

        // Получаем текущее значение игрока
        long currentValue = currentValuesMap.getOrDefault(player, 0L);

        // Обработка кнопки удаления
        if (config.isDeleteLastDigit() && buttonId.equals(config.getPrefix() + "_delete")) {
            currentValue /= 10;
            if (currentValue < config.getMinValue()) {
                currentValue = config.getMinValue();
            }
        }
        // Обработка цифровых кнопок
        else if (buttonId.startsWith(config.getPrefix() + "_")) {
            try {
                int digit = Integer.parseInt(buttonId.replace(config.getPrefix() + "_", ""));

                // Добавляем цифру к числу
                long newValue = currentValue * 10 + digit;

                // Проверка максимального значения
                if (newValue > config.getMaxValue()) {
                    player.sendMessage(String.format("§cМаксимальное значение - %s",
                            config.getMaxValue()));
                    return;
                }

                // Проверка минимального значения
                if (newValue < config.getMinValue()) {
                    player.sendMessage(String.format("§cМинимальное значение - %s",
                            config.getMinValue()));
                    return;
                }

                currentValue = newValue;
            } catch (NumberFormatException e) {
                // Не цифровая кнопка - игнорируем
                return;
            }
        }

        // Обновляем значение игрока
        currentValuesMap.put(player, currentValue);

        // Обновляем интерфейс
        updateInventoryTitle(player, inv,
                String.format(config.getTitleTemplate(), currentValue));
    }
    private void updateInventoryTitle(Player player, Inventory inv, String newTitle) {
        // К сожалению, Bukkit не позволяет изменить название уже открытого инвентаря,
        // поэтому нужно закрыть и открыть заново с новым названием

        // Сохраняем содержимое инвентаря
        ItemStack[] contents = inv.getContents();

        // Создаем новый инвентарь
        Inventory newInv = Bukkit.createInventory(null, inv.getSize(), newTitle);
        newInv.setContents(contents);

        // Открываем новый инвентарь
        player.openInventory(newInv);
    }

    private void pushItemToMarket(Player player, Inventory inventory){
        Map<Material, Integer> mapItem = manageInventory.getItemsToMarket(inventory);
        Map.Entry<Material, Integer> entry = mapItem.entrySet().iterator().next();
        Material key = entry.getKey();
        Integer value = entry.getValue();

        // Получаем открытый инвентарь
        InventoryView openInventory = player.getOpenInventory();

        // Получаем верхнюю часть инвентаря (обычно это название меню)
        Inventory topInventory = openInventory.getTopInventory();

        // Получаем название инвентаря

        String inventoryTitle = openInventory.getTitle();

        int cost = manageStrok.extractPrice(inventoryTitle);
        String uuid = player.getUniqueId().toString();


        // Раскоментировать после отладки меню покупки.
        if(managerBD.countRecordsByUuid(uuid) >= 5){
            player.sendMessage(ChatColor.RED + "Вы можете выстовить не более 5 предложений!!!");
            return;
        }

        if(value == -1 && key == Material.BARRIER){
            player.sendMessage(ChatColor.RED + "Вы пытаетесь продать разные предметы!!!");
            return;
        }
        if(value == 0){
            player.sendMessage(ChatColor.RED + "Вы не положили предметы!!!");
            return;
        }

        // Далеее отправляем в бд

        if(!inventoryTitle.equals("На продажу") ){

            managerBD.insertMarketPlayer(uuid, String.valueOf(key), value, cost);

        }else{
            player.sendMessage(ChatColor.RED + "Цена не указана!!!");
        }

        openSellMenu(player);

    }

    private void givePlayerItemsFromMarket(Player player, Material material) {
        Manager dbManager = new Manager();

        String uuid = player.getUniqueId().toString();
        try {
            // Получаем количество предметов из БД
            ResultSet rs = dbManager.executeQuery(
                    "SELECT quantity FROM marketPlayer WHERE uuid = '" + uuid + "' AND material = '" + material.name() + "'");

            if (rs.next()) {
                int quantity = rs.getInt("quantity");

                // Создаем ItemStack
                ItemStack itemStack = new ItemStack(material, quantity);

                // Добавляем предметы в инвентарь (остатки выпадут на землю)
                HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(itemStack);

                // Если что-то не поместилось - выбросить на землю
                if (!leftover.isEmpty()) {
                    for (ItemStack item : leftover.values()) {
                        player.getWorld().dropItemNaturally(player.getLocation(), item);
                    }
                    player.sendMessage("§eНе все предметы поместились в инвентарь!");
                }
                dbManager.closeConnection();

                // Удаляем запись из БД
                dbManager.executeUpdate(
                        "DELETE FROM marketPlayer WHERE uuid = '" + uuid + "' AND material = '" + material.name() + "'");
                player.sendMessage("§aВы получили вернули свой предмет");
            } else {
                player.sendMessage("§cУ вас нет таких предметов на рынке");
            }

        } catch (SQLException e) {
            player.sendMessage("§cОшибка при получении предметов");
            e.printStackTrace();
        } finally {
            dbManager.closeConnection();
        }
    }



    private void newTitleOfBuyMenu(Player player, int page){
        int maxPage = sellMenu.getPage(player);
        String newTitle = "Рынок: "+page+"/"+maxPage;
        updateBuyMenuTitle(player, newTitle, page);
    }

    private void updateBuyMenuTitle(Player player, String newTitle, int page) {
        sellMenu.openBuyMenu(player, newTitle, page);
    }

    public static int getQuantityFromLore(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return 0;

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasLore()) return 0;

        List<String> lore = meta.getLore();
        for (String line : lore) {
            if (line.contains("Количество:")) {
                // Находим позицию "Количество:" и берем подстроку после него
                int startIndex = line.indexOf("Количество:") + "Количество:".length();
                String afterKeyword = line.substring(startIndex).trim();

                // Удаляем всё, кроме цифр (теперь только число после "Количество:")
                String numbers = afterKeyword.replaceAll("[^0-9]", "");

                try {
                    return numbers.isEmpty() ? 0 : Integer.parseInt(numbers);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        }
        return 0;
    }


}



