package lordostrov.traderplugin.menu;

import lordostrov.traderplugin.Traderplugin;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CustomInventory implements Listener {
    private final Traderplugin pluginInstance;

    public CustomInventory(Traderplugin pluginInstance) {
        this.pluginInstance = pluginInstance;
    }


    public Inventory createInventory(Player player) {
        Inventory inv = Bukkit.createInventory(player, 54, Color.RED + "Custom Inventory");

        // Создаем кнопки с ID
        ItemStack closeButton = createStaticButton("close", Material.BARRIER, "&cЗакрыть");
        inv.setItem(49, closeButton);

        ItemStack actionButton = createStaticButton("action", Material.EMERALD, "&aДействие", "&7Эта кнопка не двигается");
        inv.setItem(22, actionButton);

        ItemStack rewardButton = createStaticButton("reward", Material.DIAMOND, "&bНаграда", "&7Получить 5 алмазов");
        inv.setItem(31, rewardButton);

        // Обычный предмет
        ItemStack movableItem = new ItemStack(Material.GOLD_INGOT);
        inv.setItem(10, movableItem);

        return inv;
    }

    private void setButtonType(ItemStack button, String type) {
        NamespacedKey key = new NamespacedKey(pluginInstance, "button-type");
        ItemMeta meta = button.getItemMeta();
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, type);
        button.setItemMeta(meta);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        // Проверяем что клик в нашем инвентаре
        if (!event.getView().getTitle().equals(Color.RED + "Custom Inventory")) return;

        // Отменяем событие по умолчанию
        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        // Проверяем что это наша кнопка
        NamespacedKey staticKey = new NamespacedKey(pluginInstance, "static-button");
        if (!clicked.hasItemMeta() ||
                !clicked.getItemMeta().getPersistentDataContainer().has(staticKey, PersistentDataType.BYTE)) {
            return;
        }

        // Получаем ID кнопки
        NamespacedKey buttonIdKey = new NamespacedKey(pluginInstance, "button-id");
        String buttonId = clicked.getItemMeta()
                .getPersistentDataContainer()
                .get(buttonIdKey, PersistentDataType.STRING);

        // Обрабатываем нажатие в зависимости от ID кнопки
        switch (buttonId) {
            case "close":
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Инвентарь закрыт!");
                break;

            case "action":
                player.sendMessage(ChatColor.GOLD + "Вы нажали на кнопку действия!");
                player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0, 2, 0), 5);
                break;

            case "reward":
                if (checkCooldown(player)) {
                    player.sendMessage(ChatColor.RED + "Подождите перед получением следующей награды!");
                } else {
                    player.getInventory().addItem(new ItemStack(Material.DIAMOND, 5));
                    player.sendMessage(ChatColor.GREEN + "Вы получили 5 алмазов!");
                    setCooldown(player);
                }
                break;
        }
    }
    private void handleButtonClick(InventoryClickEvent event, ItemStack button) {
        Player player = (Player) event.getWhoClicked();

        // Проверяем тип кнопки по другому NBT-тегу
        NamespacedKey buttonTypeKey = new NamespacedKey(pluginInstance, "button-type");
        if (button.getItemMeta().getPersistentDataContainer().has(buttonTypeKey, PersistentDataType.STRING)) {
            String buttonType = button.getItemMeta().getPersistentDataContainer().get(buttonTypeKey, PersistentDataType.STRING);

            switch (buttonType) {
                case "close":
                    player.closeInventory();
                    player.sendMessage(ChatColor.GREEN + "Инвентарь закрыт!");
                    break;

                case "teleport-spawn":
                    player.teleport(player.getWorld().getSpawnLocation());
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "Вы телепортированы на спавн!");
                    break;

                case "get-reward":
                    player.getInventory().addItem(new ItemStack(Material.DIAMOND, 5));
                    player.sendMessage(ChatColor.GOLD + "Вы получили 5 алмазов!");
                    break;

                case "cooldown-button":
                    // Пример кнопки с задержкой
                    if (checkCooldown(player)) {
                        player.sendMessage(ChatColor.RED + "Подождите перед следующим использованием!");
                    } else {
                        player.sendMessage(ChatColor.GREEN + "Кнопка сработала!");
                        setCooldown(player);
                        // Анимация кнопки
                        animateButton(event.getInventory(), event.getSlot(), button);
                    }
                    break;
            }
        }
    }


    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getView().getTitle().equals(Color.RED + "Custom Inventory")) return;

        // Проверяем, не перетаскивается ли статичная кнопка
        NamespacedKey staticKey = new NamespacedKey(pluginInstance, "static-button");
        for (ItemStack item : event.getNewItems().values()) {
            if (item != null && item.hasItemMeta() &&
                    item.getItemMeta().getPersistentDataContainer().has(staticKey, PersistentDataType.BYTE)) {
                event.setCancelled(true);
                return;
            }
        }

        // Разрешаем перетаскивание для обычных предметов
        event.setCancelled(false);
    }


    public ItemStack createStaticButton(String buttonId, Material material, String name, String... lore) {
        ItemStack button = new ItemStack(material);
        ItemMeta meta = button.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        if (lore != null && lore.length > 0) {
            List<String> coloredLore = new ArrayList<>();
            for (String line : lore) {
                coloredLore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            meta.setLore(coloredLore);
        }

        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

        // Добавляем идентификатор кнопки
        NamespacedKey buttonIdKey = new NamespacedKey(pluginInstance, "button-id");
        meta.getPersistentDataContainer().set(buttonIdKey, PersistentDataType.STRING, buttonId);

        // Метка что это статичная кнопка
        NamespacedKey staticKey = new NamespacedKey(pluginInstance, "static-button");
        meta.getPersistentDataContainer().set(staticKey, PersistentDataType.BYTE, (byte) 1);

        button.setItemMeta(meta);
        return button;
    }

    // Анимация кнопки при нажатии
    private void animateButton(Inventory inventory, int slot, ItemStack originalButton) {
        ItemStack pressedButton = originalButton.clone();
        pressedButton.setType(Material.REDSTONE_BLOCK);
        inventory.setItem(slot, pressedButton);

        Bukkit.getScheduler().runTaskLater(pluginInstance, () -> {
            inventory.setItem(slot, originalButton);
        }, 20L); // Возвращаем обратно через 1 секунду (20 тиков)
    }

    // Система задержки для кнопок
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    private boolean checkCooldown(Player player) {
        if (cooldowns.containsKey(player.getUniqueId())) {
            long secondsLeft = ((cooldowns.get(player.getUniqueId()) / 1000) + 10 - (System.currentTimeMillis() / 1000));
            return secondsLeft > 0;
        }
        return false;
    }

    private void setCooldown(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }

    // Метод для открытия инвентаря игроку
    public void openInventory(Player player) {
        player.openInventory(this.createInventory(player));
    }
}