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


}
