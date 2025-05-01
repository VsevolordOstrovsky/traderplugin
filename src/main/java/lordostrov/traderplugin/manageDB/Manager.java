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

    private void closeConnection() {
        try {
            connection.close();
            statement.close();
            resultSet.close();
            req = "";
            System.out.println("Connection closed");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createTablePlayer(Connection conn) throws SQLException {
        String sql = "CREATE TABLE player(" +
                "    uuid TEXT NOT NULL PRIMARY KEY," +
                "    name TEXT NOT NULL," +
                "    usdt TEXT DEFAULT '0'," +
                "    rating INTEGER NOT NULL UNIQUE" +
                ");";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Таблица [player] создана или уже существует");
        }
    }

    private static void createTableCryptoPlayer(Connection conn) throws SQLException {
        String sql = "CREATE TABLE cryptoPlayer(" +
                "    uuid TEXT NOT NULL PRIMARY KEY," +
                "    BTC INTEGER DEFAULT 0," +
                "    ETH INTEGER DEFAULT 0," +
                "    SOL INTEGER DEFAULT 0," +
                "    XRP INTEGER DEFAULT 0," +
                "    FOREIGN KEY (uuid) REFERENCES player(uuid)" +
                ");";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Таблица [cryptoPlayer] создана или уже существует");
        }
    }

    private static void createTableMarketPlayer(Connection conn) throws SQLException {
        String sql = "CREATE TABLE marketPlayer(" +
                "    uuid TEXT NOT NULL PRIMARY KEY," +
                "    material TEXT NOT NULL," +
                "    quantity int not null" +
                "    FOREIGN KEY (uuid) REFERENCES player(uuid)" +
                ");";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Таблица [marketPlayer] создана или уже существует");
        }
    }

    private static void createTableRating(Connection conn) throws SQLException {
        String sql = "CREATE TABLE rating(" +
                "    rating INTEGER NOT NULL," +
                "    uuid TEXT NOT NULL," +
                "    usdt TEXT NOT null ," +
                "    FOREIGN KEY (uuid) REFERENCES player(uuid)," +
                "    FOREIGN KEY (rating) REFERENCES player(rating)" +
                "    FOREIGN KEY (usdt) REFERENCES player(usdt)" +
                ");";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Таблица [rating] создана или уже существует");
        }
    }

    public void createTables() throws SQLException {
        getConnection();
        createTablePlayer(connection);
        createTableCryptoPlayer(connection);
        createTableMarketPlayer(connection);
        createTableRating(connection);
        closeConnection();

    }

    public void addInTablePlayer(PlayerJoinEvent player){
        getConnection();
        PreparedStatement ps = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select * from player");
            while (resultSet.next()) {
//                получение данных из колонок.
                if(resultSet.getString("uuid").equals(player.getPlayer().getUniqueId().toString())){
                    closeConnection();
                    return;
                }
            }
            ps = connection.prepareStatement("INSERT INTO player (uuid, name, rating) VALUES (?,?,?)");
            ps.setString(1, player.getPlayer().getUniqueId().toString());
            ps.setString(2, player.getPlayer().getName());
            ps.setString(3, "COALESCE(" +
                    "        (SELECT MAX(ваш_столбец) FROM ваша_таблица) + 1," +
                    "        1" +
                    "    )");
            ps.executeUpdate();
            closeConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        closeConnection();


    }


}
