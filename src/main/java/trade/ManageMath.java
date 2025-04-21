package trade;

public class ManageMath {

    public static String addStringFloats(String a, String b) {
        // Проверка на пустые строки
        if (a.isEmpty()) return b;
        if (b.isEmpty()) return a;

        // Разбиваем числа на целую и дробную части
        String[] partsA = a.split("\\.");
        String[] partsB = b.split("\\.");

        // Целая часть
        String intPartA = partsA[0];
        String intPartB = partsB[0];

        // Дробная часть (может отсутствовать)
        String fracPartA = (partsA.length > 1) ? partsA[1] : "";
        String fracPartB = (partsB.length > 1) ? partsB[1] : "";

        // Выравниваем дробные части по длине, дополняя нулями справа
        int maxFracLength = Math.max(fracPartA.length(), fracPartB.length());
        fracPartA = padRight(fracPartA, maxFracLength, '0');
        fracPartB = padRight(fracPartB, maxFracLength, '0');

        // Складываем дробные части
        String sumFrac = addStrings(fracPartA, fracPartB);
        int carry = 0;

        // Если сумма дробных частей превысила разрядность, переносим единицу в целую часть
        if (sumFrac.length() > maxFracLength) {
            carry = 1;
            sumFrac = sumFrac.substring(1);
        }

        // Складываем целые части с учетом переноса
        String sumInt = addStrings(intPartA, intPartB);
        if (carry > 0) {
            sumInt = addStrings(sumInt, "1");
        }

        // Собираем результат
        if (sumFrac.isEmpty()) {
            return sumInt;
        } else {
            return sumInt + "." + sumFrac;
        }
    }

    /**
     * Складывает два числа в строковом представлении (целые числа).
     * @param a Первое число (строка)
     * @param b Второе число (строка)
     * @return Результат сложения в виде строки
     */
    private static String addStrings(String a, String b) {
        int i = a.length() - 1;
        int j = b.length() - 1;
        int carry = 0;
        StringBuilder result = new StringBuilder();

        while (i >= 0 || j >= 0 || carry > 0) {
            int digitA = (i >= 0) ? (a.charAt(i--) - '0') : 0;
            int digitB = (j >= 0) ? (b.charAt(j--) - '0') : 0;

            int sum = digitA + digitB + carry;
            carry = sum / 10;
            result.insert(0, sum % 10);
        }

        return result.toString();
    }

    /**
     * Дополняет строку справа указанным символом до заданной длины.
     */
    private static String padRight(String s, int length, char padChar) {
        while (s.length() < length) {
            s += padChar;
        }
        return s;
    }

}
