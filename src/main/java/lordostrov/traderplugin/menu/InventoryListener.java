package lordostrov.traderplugin.menu;
import lordostrov.traderplugin.manageDB.Manager;
import lordostrov.traderplugin.trade.ManageInventory;
import lordostrov.traderplugin.trade.ManageStrok;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
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

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class InventoryListener implements Listener {
    CustomInventory customInventory = new CustomInventory();
    SellMenu sellMenu = new SellMenu();
    HashMap<Player, Long> currentNumbers = sellMenu.getCurrentNumbers();
    HashMap<Player, Integer> currentPages = sellMenu.getCurrentPages();
    ManageInventory manageInventory = new ManageInventory();
    Manager managerBD = new Manager();
    ManageStrok manageStrok = new ManageStrok();

    int page = 1;

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
                handleBannerClick(player, buttonId, event.getInventory());
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
                    System.out.println("------------- Page: "+page);
                    break;
                case "sell_market_menu":
                    openSellMenu(player);
                    break;
                case "my_lots_market_menu":
                    openMyItemMenu(player);
                    break;
                case "back_to_shop":
                    open_shop(player);
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
                    System.out.println("------------- maxPage: "+sellMenu.getPage(player));
                    if(page > 1){
                        page--;
                        newTitleOfBuyMenu(player, page);
                    }else{
                        player.sendMessage(ChatColor.RED + "Это первая страница");
                    }
                    System.out.println("------------- Page: "+page);
                    break;
                case "next_page":
                    System.out.println("------------- maxPage: "+sellMenu.getPage(player));

                    if(page < sellMenu.getPage(player)){
                        page++;
                        newTitleOfBuyMenu(player, page);
                    }else{
                        player.sendMessage(ChatColor.RED + "Страниц больше нету");
                    }
                    System.out.println("------------- Page: "+page);
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
    void openBuyMenu(Player player, int page){sellMenu.openBuyMenu(player, "Рынок: "+page+"/"+sellMenu.getPage(player),1);}
    void openSellMenu(Player player){sellMenu.openSellMenu(player);}
    void openMyItemMenu(Player player){sellMenu.openMyItemMenu(player);}
    void closeInventory(Player player){
        manageInventory.deleteCloseButton(player);
        player.closeInventory();
    }

    private void handleBannerClick(Player player, String buttonId, Inventory inv) {

        // Получаем текущее число игрока как long
        long currentNumber = currentNumbers.getOrDefault(player, 0L);

        if (buttonId.equals("banner_delete")) {
            // Удаляем последнюю цифру
            currentNumber = currentNumber / 10;
        } else {
            // Получаем цифру из buttonId (banner_1 -> 1)
            int digit = Integer.parseInt(buttonId.replace("banner_", ""));

            // Добавляем цифру к числу
            long newNumber = currentNumber * 10 + digit;
            if (newNumber > 1_000_000_000L) { // Максимальная цена - 1,000,000,000
                player.sendMessage("§cМаксимальная цена - 1,000,000,000");
                return;
            }
            currentNumber = newNumber;
        }

        // Обновляем число игрока
        currentNumbers.put(player, currentNumber);

        // Обновляем название инвентаря
        String newTitle = "Цена за 1: " + currentNumber;
        updateInventoryTitle(player, inv, newTitle);
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
//        if(managerBD.countRecordsByUuid(uuid) >= 5){
//            player.sendMessage(ChatColor.RED + "Вы можете выстовить не более 5 предложений!!!");
//            return;
//        }

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


}
