package lordostrov.traderplugin.trade;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ManageInventory {

    private static final Set<Integer> TARGET_SLOTS = new HashSet<>();

    static {
        // Заполняем слоты, которые нужно учитывать
        for (int row = 0; row < 4; row++) {
            int start = row * 9;
            for (int i = start; i < start + 5; i++) {
                TARGET_SLOTS.add(i);
            }
        }
    }

    private Map<Material, Integer> inventoryMap = new HashMap<>();
    //private Material[] resourse = new Material[]{Material.ANCIENT_DEBRIS, Material.DIAMOND, Material.EMERALD, Material.GOLD_INGOT};

    public Map<Material, Integer> getResourses(Player player) {
        Inventory inventory = player.getInventory();

        // Проходим по всем слотам инвентаря
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                // Добавляем количество к существующему значению или создаем новую запись
                inventoryMap.merge(item.getType(), item.getAmount(), Integer::sum);
            }
        }

        return inventoryMap;
    }

    public void sellItems(Player player, Material material, int amount) {
        Inventory inventory = player.getInventory();

        // Перебираем все слоты инвентаря
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() == material) {
                // Если в стеке больше 1 предмета - уменьшаем количество
                if (item.getAmount() > amount) {
                    item.setAmount(item.getAmount() - amount);
                }
                // Если ровно 1 предмет - удаляем весь стек
                else {
                    inventory.remove(item);
                }
                return; // Прекращаем поиск после удаления 1 предмета
            }
        }
    }

    public Map<Material, Integer> getItemsToMarket(Inventory inventory) {

        int counts = 0; // Количество нужных предметов
        Map<Material, Integer> map = new HashMap<>();
        if(!areAllItemsSame(inventory)){
            map.put(Material.BARRIER, -1);
            return map;
        }

        ItemStack[] contents = inventory.getContents();
        Material itemType = null;
        for (int slot : TARGET_SLOTS) {
            ItemStack item = contents[slot];
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }
            itemType = item.getType();
            counts += item.getAmount();

        }

        map.put(itemType,counts);
        return map;

    }


    public static boolean areAllItemsSame(Inventory inventory) {
        ItemStack firstItem = null;

        for (int slot : TARGET_SLOTS) {
            if (slot >= inventory.getSize()) continue;

            ItemStack item = inventory.getItem(slot);
            if (item == null || item.getType() == Material.AIR) continue;

            if (firstItem == null) {
                firstItem = item; // Первый непустой предмет
                continue;
            }

            // Если предмет отличается от первого — значит, не все одинаковые
            if (!isItemEqual(firstItem, item)) {
                return false;
            }
        }

        return true; // Все предметы одинаковые (или слоты пусты)
    }

    private static boolean isItemEqual(ItemStack a, ItemStack b) {
        if (a == b) return true;
        if (a == null || b == null) return false;

        // Проверяем Material и Durability
        if (a.getType() != b.getType() || a.getDurability() != b.getDurability()) {
            return false;
        }

        // Проверяем ItemMeta (название и Lore)
        if (!a.hasItemMeta() && !b.hasItemMeta()) return true;
        if (a.hasItemMeta() != b.hasItemMeta()) return false;

        ItemMeta metaA = a.getItemMeta();
        ItemMeta metaB = b.getItemMeta();

        // Проверяем DisplayName
        if (metaA.hasDisplayName() != metaB.hasDisplayName()) return false;
        if (metaA.hasDisplayName() && !metaA.getDisplayName().equals(metaB.getDisplayName())) {
            return false;
        }

        // Проверяем Lore
        if (metaA.hasLore() != metaB.hasLore()) return false;
        if (metaA.hasLore() && !metaA.getLore().equals(metaB.getLore())) {
            return false;
        }

        return true;
    }


}
