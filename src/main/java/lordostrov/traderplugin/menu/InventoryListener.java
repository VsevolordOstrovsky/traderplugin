package lordostrov.traderplugin.menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {
    CustomInventory customInventory = new CustomInventory();

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
            switch (buttonId) {
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

                case "filler":

                    break;

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

    }

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
}
