package lordostrov.traderplugin.menu;

import com.samjakob.spigui.SpiGUI;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import com.samjakob.spigui.menu.SGMenu;
import lordostrov.traderplugin.Traderplugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIbuyCoin {
    private static Traderplugin plugin;
    private SGMenu homeMenu = plugin.spiGUI.create("&cHome Menu", 1);
    private SGMenu buyMenu = plugin.spiGUI.create("&cCrypto Menu", 1);
    private SGMenu marketMenu = plugin.spiGUI.create("&cMarket", 6);
    private SGMenu walletMenu = plugin.spiGUI.create("&cWallet", 2);

    private getCoin coin = new getCoin();

    public void openMyAwesomeMenu(Player player) {

        init_buyMenu(player);
        init_marketMenu(player);
        init_walletMenu(player);
        init_homeMenu(player);

        // Show the GUI
        player.openInventory(homeMenu.getInventory());


    }

    void init_buyMenu(Player player){
        // Create a button
        SGButton debris = new SGButton(
                new ItemBuilder(Material.ANCIENT_DEBRIS)
                        .name("&aBitcoin")
                        .lore("&7Купить")
                        .build()
        ).withListener((InventoryClickEvent event) -> {
            player.closeInventory();
            coin.getPrice(player,"BTCUSDT");
        });

        SGButton diamond = new SGButton(
                new ItemBuilder(Material.DIAMOND)
                        .name("&aEthereum")
                        .lore("&7Купить")
                        .build()
        ).withListener((InventoryClickEvent event) -> {
            player.closeInventory();
            coin.getPrice(player,"ETHUSDT");
        });

        SGButton emerald = new SGButton(
                new ItemBuilder(Material.EMERALD)
                        .name("&aSolana")
                        .lore("&7Купить")
                        .build()
        ).withListener((InventoryClickEvent event) -> {
            player.closeInventory();
            coin.getPrice(player,"SOLUSDT");
        });

        SGButton gold = new SGButton(
                new ItemBuilder(Material.GOLD_INGOT)
                        .name("&aXRP")
                        .lore("&7Купить")
                        .build()
        ).withListener((InventoryClickEvent event) -> {
            player.closeInventory();
            coin.getPrice(player,"XRPUSDT");
        });

        SGButton book = new SGButton(
                new ItemBuilder(Material.BOOK)
                        .name("&aINFO")
                        .lore("&7Посмотреть")
                        .build()
        ).withListener((InventoryClickEvent event) -> {
            event.getWhoClicked().sendMessage("Напечатать информацию о работе с меню и о покупке/продажи ресурсов");
            player.closeInventory();
        });

        SGButton back = new SGButton(
                new ItemBuilder(Material.OAK_WOOD)
                        .name("&aНазад")
                        .build()
        ).withListener((InventoryClickEvent event) -> {
            player.openInventory(homeMenu.getInventory());
        });

        // Add the button to your GUI
        buyMenu.setButton(0, book);
        buyMenu.setButton(2, debris);
        buyMenu.setButton(3, diamond);
        buyMenu.setButton(4, emerald);
        buyMenu.setButton(5, gold);
        buyMenu.setButton(8, back);

    }

    void init_marketMenu(Player player){
        SGButton back = new SGButton(
                new ItemBuilder(Material.OAK_WOOD)
                        .name("&aНазад")
                        .build()
        ).withListener((InventoryClickEvent event) -> {

            player.openInventory(homeMenu.getInventory());
        });
        SGButton sell = new SGButton(
                new ItemBuilder(Material.VILLAGER_SPAWN_EGG)
                        .name("&aВыставить на продажу")
                        .build()
        ).withListener((InventoryClickEvent event) -> {
            event.getWhoClicked().sendMessage("Переход в меню для выставления товара");

        });


        marketMenu.setButton(45, sell);
        marketMenu.setButton(53, back);


    }

    void init_walletMenu(Player player){
        SGButton back = new SGButton(
                new ItemBuilder(Material.OAK_WOOD)
                        .name("&aНазад")
                        .build()
        ).withListener((InventoryClickEvent event) -> {

            player.openInventory(homeMenu.getInventory());
        });
        SGButton sell = new SGButton(
                new ItemBuilder(Material.REDSTONE)
                        .name("&aПродать ресурсы")
                        .build()
        ).withListener((InventoryClickEvent event) -> {
            // Здесь должен быть переход в меню продажи

        });
        // функция для вывода купленных криптовалют (Написать)

        walletMenu.setButton(17, back);
        walletMenu.setButton(9, sell);
    }

    void init_homeMenu(Player player){
        SGButton market = new SGButton(
                new ItemBuilder(Material.LECTERN)
                        .name("&aМагазин")
                        .build()
        ).withListener((InventoryClickEvent event) -> {
            player.openInventory(marketMenu.getInventory());
        });

        SGButton crypto = new SGButton(
                new ItemBuilder(Material.DIAMOND_BLOCK)
                        .name("&aПокупка криптовалюты")
                        .build()
        ).withListener((InventoryClickEvent event) -> {
            player.openInventory(buyMenu.getInventory());
        });

        SGButton wallet = new SGButton(
                new ItemBuilder(Material.CHEST)
                        .name("&aКошелёк")
                        .build()
        ).withListener((InventoryClickEvent event) -> {
            player.openInventory(walletMenu.getInventory());

        });

        homeMenu.setButton(3, market);
        homeMenu.setButton(4, crypto);
        homeMenu.setButton(5, wallet);
    }

}
