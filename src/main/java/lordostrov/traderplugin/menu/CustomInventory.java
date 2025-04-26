package lordostrov.traderplugin.menu;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;

public class CustomInventory {


    public static void openHomeMenu(Player player) {
        // Создаем инвентарь с 27 слотами (3 ряда) и названием
        Inventory inv = Bukkit.createInventory(null, 18, "Главное меню");



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

        fillInventory(inv);


        // Открываем инвентарь игроку
        player.openInventory(inv);
    }

    public static void openCoinShop(Player player) {
        // Создаем инвентарь с 27 слотами (3 ряда) и названием
        Inventory inv = Bukkit.createInventory(null, 18, "Покупка криптовалют");

        ItemStack close = createButton(Material.BARRIER, "close_shop",
                "§aЗакрыть инвентарь", "§7Нажмите для закрытия");

        inv.setItem(13, close);
        fillInventory(inv);

        // Открываем инвентарь игроку
        player.openInventory(inv);
    }

    public static void openWallet(Player player) {
        // Создаем инвентарь с 27 слотами (3 ряда) и названием
        Inventory inv = Bukkit.createInventory(null, 18, "Кошелёк");

        ItemStack close = createButton(Material.BARRIER, "close_shop",
                "§aЗакрыть инвентарь", "§7Нажмите для закрытия");

        inv.setItem(13, close);
        fillInventory(inv);

        // Открываем инвентарь игроку
        player.openInventory(inv);
    }

    public static void openShop(Player player) {
        // Создаем инвентарь с 27 слотами (3 ряда) и названием
        Inventory inv = Bukkit.createInventory(null, 18, "Магазин");

        ItemStack close = createButton(Material.BARRIER, "close_shop",
                "§aЗакрыть инвентарь", "§7Нажмите для закрытия");

        inv.setItem(13, close);
        fillInventory(inv);

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

    public static ItemStack createFillItem() {
        // Используем KNOWLEDGE_BOOK (книга знаний) - единственный полностью невидимый предмет
        ItemStack item = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");
        meta.setLore(Collections.emptyList());

        // Добавляем NBT-тег для идентификации
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setBoolean("immovable", true);
        item = nbtItem.getItem();

        return item;
    }


    private static void fillInventory(Inventory inv) {
        ItemStack filler = createFillItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE);

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
