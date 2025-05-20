package lordostrov.traderplugin.menu;

// Класс конфигурации обработчика
public class BannerHandlerConfig {
    private static String prefix;
    private static long maxValue;
    private static long minValue = 0;
    private static String titleTemplate;
    private static boolean deleteLastDigit = true;

    // Геттеры и сеттеры
    public static String getPrefix() { return prefix; }
    public void setPrefix(String prefix) { this.prefix = prefix; }

    public static long getMaxValue() { return maxValue; }
    public void setMaxValue(long maxValue) { this.maxValue = maxValue; }

    public static long getMinValue() { return minValue; }
    public void setMinValue(long minValue) { this.minValue = minValue; }

    public static String getTitleTemplate() { return titleTemplate; }
    public void setTitleTemplate(String titleTemplate) { this.titleTemplate = titleTemplate; }

    public static boolean isDeleteLastDigit() { return deleteLastDigit; }
    public void setDeleteLastDigit(boolean deleteLastDigit) { this.deleteLastDigit = deleteLastDigit; }
}