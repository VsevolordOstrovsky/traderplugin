package lordostrov.traderplugin.manageDB;

import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Manager {



    private Connection connection;
    Statement statement;
    ResultSet resultSet;

    String req = "";

    private void getConnection() {

//        // Указываем путь к БД (если файла нет, он будет создан)
        String url = "jdbc:sqlite:theGame.db";

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

        // Не забыть удалить строку после отладки!!!

        getConnection();
        createTablePlayer(connection);
        createTableCryptoPlayer(connection);
        createTableMarketPlayer(connection);
        createTableRating(connection);
        closeConnection();

    }

    public void addPlayerIfNotExists(PlayerJoinEvent event) throws SQLException {
        getConnection();
        try {
            String uuid = event.getPlayer().getUniqueId().toString();
            String name = event.getPlayer().getName();

            // Проверяем существование игрока
            try (PreparedStatement checkStmt = connection.prepareStatement(
                    "SELECT uuid FROM player WHERE uuid = ?")) {
                checkStmt.setString(1, uuid);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    // Игрок уже существует
                    return;
                }
            }

            // Получаем максимальный рейтинг
            int nextRating;
            try (Statement maxRatingStmt = connection.createStatement();
                 ResultSet maxRatingRs = maxRatingStmt.executeQuery(
                         "SELECT COALESCE(MAX(rating), 0) + 1 AS next_rating FROM player")) {
                nextRating = maxRatingRs.next() ? maxRatingRs.getInt("next_rating") : 1;
            }

            // Добавляем нового игрока
            try (PreparedStatement insertStmt = connection.prepareStatement(
                    "INSERT INTO player (uuid, name, rating, usdt) VALUES (?, ?, ?, ?)")) {
                insertStmt.setString(1, uuid);
                insertStmt.setString(2, name);
                insertStmt.setInt(3, nextRating);
                insertStmt.setString(4, "0"); // Начальный баланс USDT
                insertStmt.executeUpdate();
            }

            // Добавляем в cryptoPlayer
            try (PreparedStatement cryptoStmt = connection.prepareStatement(
                    "INSERT INTO cryptoPlayer (uuid) VALUES (?)")) {
                cryptoStmt.setString(1, uuid);
                cryptoStmt.executeUpdate();
            }

            // Добавляем в rating
            try (PreparedStatement ratingStmt = connection.prepareStatement(
                    "INSERT INTO rating (rating, uuid, usdt) VALUES (?, ?, ?)")) {
                ratingStmt.setInt(1, nextRating);
                ratingStmt.setString(2, uuid);
                ratingStmt.setString(3, "0"); // Начальный баланс USDT
                ratingStmt.executeUpdate();
            }

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
        getConnection();

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
            closeConnection();
        }
    }

    /**
     * Подсчитывает количество записей с указанным UUID в таблице marketPlayer
     * @param uuid UUID для проверки
     * @return количество найденных записей (0 если не найдено)
     */
    public int countRecordsByUuid(String uuid) {
        getConnection();
        try {
            String sql = "SELECT COUNT(*) AS count FROM marketPlayer WHERE uuid = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, uuid);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    return rs.getInt("count");
                }
                return 0;
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при подсчёте записей для UUID " + uuid + ": " + e.getMessage());
            return 0;
        } finally {
            closeConnection();
        }
    }

    /**
     * Выполняет параметризованный SQL-запрос на обновление данных
     * @param sql SQL-запрос с параметрами (?)
     * @param params параметры для подстановки в запрос
     * @return количество измененных строк или -1 при ошибке
     */
    public int executeUpdate(String sql, Object... params) {
        getConnection();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            // Устанавливаем параметры
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка при выполнении параметризованного запроса: " + e.getMessage());
            System.err.println("Запрос: " + sql);
            e.printStackTrace();
            return -1;
        } finally {
            // Автоматически закрывает PreparedStatement благодаря try-with-resources
            closeConnection();
        }
    }

    public int countPlayerMarketRecords(String uuid) {
        String sql = "SELECT COUNT(*) AS player_total FROM marketPlayer WHERE uuid != ?";

        getConnection();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            // Устанавливаем параметр и таймаут
            pstmt.setString(1, uuid);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("player_total");
                }
                return 0;
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при подсчёте записей игрока: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Добавляет или списывает USDT у игрока
     * @param uuid UUID игрока
     * @param amount Сумма для изменения (может быть отрицательной)
     * @return true если операция успешна, false при ошибке
     */
    public boolean addUsdtToPlayer(String uuid, String amount) {
        // Проверяем валидность суммы
        try {
            new java.math.BigDecimal(amount);
        } catch (NumberFormatException e) {
            System.err.println("Некорректная сумма USDT: " + amount);
            return false;
        }

        Connection conn = null;
        PreparedStatement checkStmt = null;
        PreparedStatement updatePlayerStmt = null;
        PreparedStatement updateRatingStmt = null;
        ResultSet rs = null;

        try {
            // 1. Получаем соединение и начинаем транзакцию
            getConnection();
            conn = this.connection;
            conn.setAutoCommit(false);

            // 2. Проверяем существование игрока (без FOR UPDATE)
            checkStmt = conn.prepareStatement(
                    "SELECT usdt FROM player WHERE uuid = ?");
            checkStmt.setString(1, uuid);
            rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.err.println("Игрок с UUID " + uuid + " не найден");
                return false;
            }

            // 3. Получаем текущий баланс
            String currentUsdt = rs.getString("usdt");
            java.math.BigDecimal current = new java.math.BigDecimal(currentUsdt);
            java.math.BigDecimal change = new java.math.BigDecimal(amount);
            java.math.BigDecimal newBalance = current.add(change);

            // 4. Проверяем, что баланс не станет отрицательным
            if (newBalance.compareTo(java.math.BigDecimal.ZERO) < 0) {
                System.err.println("Недостаточно USDT у игрока " + uuid);
                return false;
            }

            // 5. Обновляем баланс в player
            updatePlayerStmt = conn.prepareStatement(
                    "UPDATE player SET usdt = ? WHERE uuid = ?");
            updatePlayerStmt.setString(1, newBalance.toPlainString());
            updatePlayerStmt.setString(2, uuid);
            updatePlayerStmt.executeUpdate();

            // 6. Обновляем баланс в rating
            updateRatingStmt = conn.prepareStatement(
                    "UPDATE rating SET usdt = ? WHERE uuid = ?");
            updateRatingStmt.setString(1, newBalance.toPlainString());
            updateRatingStmt.setString(2, uuid);
            updateRatingStmt.executeUpdate();

            conn.commit();



            return true;

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Ошибка при откате: " + ex.getMessage());
            }
            System.err.println("Ошибка изменения баланса: " + e.getMessage());
            return false;
        } finally {
            // Закрываем ресурсы в правильном порядке
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* игнорируем */ }
            try { if (checkStmt != null) checkStmt.close(); } catch (SQLException e) { /* игнорируем */ }
            try { if (updatePlayerStmt != null) updatePlayerStmt.close(); } catch (SQLException e) { /* игнорируем */ }
            try { if (updateRatingStmt != null) updateRatingStmt.close(); } catch (SQLException e) { /* игнорируем */ }

            // Восстанавливаем autoCommit
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                System.err.println("Ошибка восстановления autoCommit: " + e.getMessage());
            }
            boolean success = updatePlayerRatings();
            if (success) {
                System.out.println("Рейтинг игроков успешно обновлен");
            } else {
                System.out.println("Ошибка при обновлении рейтинга");
            }
        }
    }

    public boolean updatePlayerRatings() {
        Connection conn = null;
        PreparedStatement playerStmt = null;
        PreparedStatement ratingStmt = null;
        ResultSet rs = null;

        try {
            // 1. Получаем соединение и начинаем транзакцию
            getConnection();
            conn = this.connection;
            conn.setAutoCommit(false);

            // 2. Получаем всех игроков с их USDT, отсортированных по убыванию USDT
            List<PlayerRating> players = new ArrayList<>();
            String selectSql = "SELECT uuid, usdt FROM player ORDER BY CAST(usdt AS DECIMAL) DESC";

            statement = conn.createStatement();
            resultSet = statement.executeQuery(selectSql);

            while (resultSet.next()) {
                players.add(new PlayerRating(
                        resultSet.getString("uuid"),
                        resultSet.getString("usdt")
                ));
            }

            // 3. Обновляем рейтинг в таблице player
            String updatePlayerSql = "UPDATE player SET rating = ? WHERE uuid = ?";
            playerStmt = conn.prepareStatement(updatePlayerSql);

            for (int i = 0; i < players.size(); i++) {
                playerStmt.setInt(1, i + 1); // Рейтинг начинается с 1
                playerStmt.setString(2, players.get(i).uuid);
                playerStmt.addBatch();
            }
            playerStmt.executeBatch();

            // 4. Полностью перезаписываем таблицу rating
            // Сначала очищаем
            try (Statement clearStmt = conn.createStatement()) {
                clearStmt.execute("DELETE FROM rating");
            }

            // Затем вставляем новые данные
            String insertRatingSql = "INSERT INTO rating (rating, uuid, usdt) VALUES (?, ?, ?)";
            ratingStmt = conn.prepareStatement(insertRatingSql);

            for (int i = 0; i < players.size(); i++) {
                PlayerRating player = players.get(i);
                ratingStmt.setInt(1, i + 1);
                ratingStmt.setString(2, player.uuid);
                ratingStmt.setString(3, player.usdt);
                ratingStmt.addBatch();
            }
            ratingStmt.executeBatch();

            conn.commit();
            return true;

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Ошибка при откате: " + ex.getMessage());
            }
            System.err.println("Ошибка обновления рейтинга: " + e.getMessage());
            return false;
        } finally {
            // Закрываем ресурсы
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* игнорируем */ }
            try { if (playerStmt != null) playerStmt.close(); } catch (SQLException e) { /* игнорируем */ }
            try { if (ratingStmt != null) ratingStmt.close(); } catch (SQLException e) { /* игнорируем */ }

            // Восстанавливаем autoCommit
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                System.err.println("Ошибка восстановления autoCommit: " + e.getMessage());
            }

            // Закрываем соединение (вызываем closeConnection())
            closeConnection();
        }
    }

    // Вспомогательный класс для хранения данных игрока
    private static class PlayerRating {
        String uuid;
        String usdt;

        PlayerRating(String uuid, String usdt) {
            this.uuid = uuid;
            this.usdt = usdt;
        }
    }
}
