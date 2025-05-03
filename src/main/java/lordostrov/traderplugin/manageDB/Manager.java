package lordostrov.traderplugin.manageDB;

import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.*;

public class Manager {



    private Connection connection;
    Statement statement;
    ResultSet resultSet;

    String req = "";

    private void getConnection() {

//        // Указываем путь к БД (если файла нет, он будет создан)
        String url = "jdbc:sqlite:theGame.db";
//
//        try (Connection conn = DriverManager.getConnection(url)) {
//            if (conn != null) {
//                System.out.println("Подключение к SQLite установлено!");
//
//            }
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }


        String text;
        try {
            connection = DriverManager.getConnection(url);
            text = "Подключение к базе данных успешно установлено!";

            System.out.println(text);
        } catch (SQLException e) {
            text = "Ошибка при подключении к базе данных:";
            e.printStackTrace();
            System.out.println(text);
        }
    }

    public void closeConnection() {
        try {
            if (resultSet != null) {
                resultSet.close();
                resultSet = null;
            }
            if (statement != null) {
                statement.close();
                statement = null;
            }
            if (connection != null) {
                connection.close();
                connection = null;
            }
            req = "";
            System.out.println("Connection closed");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTablePlayer(Connection conn) throws SQLException {

        if (tableExists(conn, "player")) {
            System.out.println("Таблица [player] уже существует, пропускаем создание");
            return;
        }


        String sql = "CREATE TABLE IF NOT EXISTS player(" +
                "    uuid TEXT NOT NULL PRIMARY KEY," +
                "    name TEXT NOT NULL," +
                "    usdt TEXT DEFAULT '0'," +
                "    rating INTEGER NOT NULL UNIQUE" +
                ");";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static void createTableCryptoPlayer(Connection conn) throws SQLException {

        if (tableExists(conn, "cryptoPlayer")) {
            System.out.println("Таблица [cryptoPlayer] уже существует, пропускаем создание");
            return;
        }

        String sql = "CREATE TABLE IF NOT EXISTS cryptoPlayer(" +
                "    uuid TEXT NOT NULL PRIMARY KEY," +
                "    BTC INTEGER DEFAULT 0," +
                "    ETH INTEGER DEFAULT 0," +
                "    SOL INTEGER DEFAULT 0," +
                "    XRP INTEGER DEFAULT 0," +
                "    FOREIGN KEY (uuid) REFERENCES player(uuid)" +
                ");";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static void createTableMarketPlayer(Connection conn) throws SQLException {

        if (tableExists(conn, "marketPlayer")) {
            System.out.println("Таблица [marketPlayer] уже существует, пропускаем создание");
            return;
        }


        String sql = "CREATE TABLE IF NOT EXISTS marketPlayer(" +
                "    uuid TEXT NOT NULL," +
                "    material TEXT NOT NULL," +
                "    quantity int not null," +
                "    cost int not null," +
                "    FOREIGN KEY (uuid) REFERENCES player(uuid)" +
                ");";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static void createTableRating(Connection conn) throws SQLException {

        if (tableExists(conn, "rating")) {
            System.out.println("Таблица [rating] уже существует, пропускаем создание");
            return;
        }

        String sql = "CREATE TABLE IF NOT EXISTS rating(" +
                "    rating INTEGER NOT NULL," +
                "    uuid TEXT NOT NULL," +
                "    usdt TEXT NOT null," +
                "    FOREIGN KEY (uuid) REFERENCES player(uuid)," +
                "    FOREIGN KEY (rating) REFERENCES player(rating)," +
                "    FOREIGN KEY (usdt) REFERENCES player(usdt)" +
                ");";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void createTables() throws SQLException {
        getConnection();
        // Не забыть удалить строку после отладки!!!
        dropTable("marketPlayer");
        createTablePlayer(connection);
        createTableCryptoPlayer(connection);
        createTableMarketPlayer(connection);
        createTableRating(connection);
        closeConnection();

    }

    public void addPlayerIfNotExists(PlayerJoinEvent event) throws SQLException {
        getConnection();
        try {
            // Проверяем существование игрока
            PreparedStatement checkStmt = connection.prepareStatement(
                    "SELECT uuid FROM player WHERE uuid = ?");
            checkStmt.setString(1, event.getPlayer().getUniqueId().toString());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Игрок уже существует
                return;
            }

            // Получаем максимальный рейтинг
            Statement maxRatingStmt = connection.createStatement();
            ResultSet maxRatingRs = maxRatingStmt.executeQuery(
                    "SELECT COALESCE(MAX(rating), 0) + 1 AS next_rating FROM player");
            int nextRating = maxRatingRs.next() ? maxRatingRs.getInt("next_rating") : 1;

            // Добавляем нового игрока
            PreparedStatement insertStmt = connection.prepareStatement(
                    "INSERT INTO player (uuid, name, rating) VALUES (?, ?, ?)");
            insertStmt.setString(1, event.getPlayer().getUniqueId().toString());
            insertStmt.setString(2, event.getPlayer().getName());
            insertStmt.setInt(3, nextRating);
            insertStmt.executeUpdate();

            // Создаем записи в связанных таблицах
            PreparedStatement cryptoStmt = connection.prepareStatement(
                    "INSERT INTO cryptoPlayer (uuid) VALUES (?)");
            cryptoStmt.setString(1, event.getPlayer().getUniqueId().toString());
            cryptoStmt.executeUpdate();

            // Аналогично для других таблиц...

        } finally {
            closeConnection();
        }
    }



    private static boolean tableExists(Connection conn, String tableName) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getTables(null, null, tableName, null)) {
            return rs.next();
        }
    }


    public ResultSet executeQuery(String sql) throws SQLException {
        getConnection();
        statement = connection.createStatement();
        resultSet = statement.executeQuery(sql);
        // Не закрываем соединение здесь - его нужно закрыть после работы с ResultSet
        return resultSet;
    }

//    public boolean insertOrUpdateMarketPlayer(String uuid, String material, int quantity, int cost) {
//        getConnection();
//        try {
//            String sql = "INSERT INTO marketPlayer(uuid, material, quantity, cost) VALUES(?,?,?,?) " +
//                    "ON CONFLICT(uuid) DO UPDATE SET " +
//                    "material = excluded.material, " +
//                    "quantity = excluded.quantity, " +
//                    "cost = excluded.cost";
//
//            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
//                pstmt.setString(1, uuid);
//                pstmt.setString(2, material);
//                pstmt.setInt(3, quantity);
//                pstmt.setInt(4, cost);
//
//                int affectedRows = pstmt.executeUpdate();
//                return affectedRows > 0;
//            }
//        } catch (SQLException e) {
//            if (e.getMessage().contains("FOREIGN KEY constraint failed")) {
//                System.err.println("Ошибка: игрок с UUID " + uuid + " не существует в таблице player");
//            } else {
//                System.err.println("SQL ошибка при работе с marketPlayer: " + e.getMessage());
//            }
//            return false;
//        } finally {
//            closeConnection();
//        }
//    }

    public boolean insertMarketPlayer(String uuid, String material, int quantity, int cost) {
        getConnection();
        try {
            String sql = "INSERT INTO marketPlayer(uuid, material, quantity, cost) VALUES(?,?,?,?)";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, uuid);
                pstmt.setString(2, material);
                pstmt.setInt(3, quantity);
                pstmt.setInt(4, cost);

                int affectedRows = pstmt.executeUpdate();
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("FOREIGN KEY constraint failed")) {
                System.err.println("Ошибка: игрок с UUID " + uuid + " не существует в таблице player");
            } else if (e.getMessage().contains("UNIQUE constraint failed")) {
                System.err.println("Ошибка: запись с UUID " + uuid + " уже существует");
            } else {
                System.err.println("SQL ошибка при добавлении в marketPlayer: " + e.getMessage());
            }
            return false;
        } finally {
            closeConnection();
        }
    }
    public boolean dropTable(String tableName) {

        try {
            // Проверяем существование таблицы перед удалением
            if (!tableExists(connection, tableName)) {
                System.out.println("Таблица " + tableName + " не существует, удаление не требуется");
                return true;
            }

            String sql = "DROP TABLE IF EXISTS " + tableName;
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(sql);
                System.out.println("Таблица " + tableName + " успешно удалена");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении таблицы " + tableName + ": " + e.getMessage());
            return false;
        } finally {

        }
    }


}
