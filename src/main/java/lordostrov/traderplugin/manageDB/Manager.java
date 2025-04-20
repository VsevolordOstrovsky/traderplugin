package lordostrov.traderplugin.manageDB;

import java.sql.*;

public class Manager {
    public static void getConnection() {

        // Указываем путь к БД (если файла нет, он будет создан)
        String url = "jdbc:sqlite:theGame.db";

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                System.out.println("Подключение к SQLite установлено!");

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void createTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "age INTEGER);";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Таблица создана или уже существует");
        }
    }

}
