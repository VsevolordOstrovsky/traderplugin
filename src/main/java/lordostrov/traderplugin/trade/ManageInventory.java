package trade;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageInventory {

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


}
