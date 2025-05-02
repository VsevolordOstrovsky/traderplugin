package lordostrov.traderplugin.menu;
import lordostrov.traderplugin.manageDB.Manager;
import lordostrov.traderplugin.trade.ManageInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


public class InventoryListener implements Listener {
    CustomInventory customInventory = new CustomInventory();
    SellMenu sellMenu = new SellMenu();
    HashMap<Player, Long> currentNumbers = sellMenu.getCurrentNumbers();
    ManageInventory manageInventory = new ManageInventory();
    Manager managerBD = new Manager();

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
                    player.closeInventory();
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
                    openBuyMenu(player);
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
                    pushItemToMarket(event.getInventory());
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
    void openBuyMenu(Player player){sellMenu.openBuyMenu(player);}
    void openSellMenu(Player player){sellMenu.openSellMenu(player);}
    void openMyItemMenu(Player player){sellMenu.openMyItemMenu(player);}

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

    private void pushItemToMarket(Inventory inventory){
        Map<Material, Integer> mapItem = manageInventory.getItemsToMarket(inventory);
        Map.Entry<Material, Integer> entry = mapItem.entrySet().iterator().next();
        Material key = entry.getKey();
        Integer value = entry.getValue();
        // Далеее отправляем в бд
    }

}
