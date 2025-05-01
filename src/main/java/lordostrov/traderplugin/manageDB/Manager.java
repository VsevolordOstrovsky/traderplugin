package lordostrov.traderplugin.manageDB;

import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.*;

public class Manager {



    Connection connection;
    Statement statement;
    ResultSet resultSet;

    String req = "";

    public void getConnection() {

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
        String sql = "CREATE TABLE player(\n" +
                "  uuid text not null primary key,\n" +
                "  name text not null,\n" +
                "    usdt TEXT default '0',\n" +
                "    rating int primary key\n" +
                ")";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Таблица создана или уже существует");
        }
    }

    private static void createTableCryptoPlayer(Connection conn) throws SQLException {
        String sql = "CREATE TABLE cryptoPlayer(\n" +
                "    uuid text not null primary key,\n" +
                "    BTC int default 0,\n" +
                "    ETH int default 0,\n" +
                "    SOL int default 0,\n" +
                "    XRP int default 0,\n" +
                "    foreign key (uuid) references player(uuid)\n" +
                "\n" +
                ")";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Таблица создана или уже существует");
        }
    }

    private static void createTableMarketPlayer(Connection conn) throws SQLException {
        String sql = "CREATE TABLE marketPlayer(\n" +
                "  uuid text not null primary key,\n" +
                "    foreign key (uuid) references player(uuid)\n" +
                ")";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Таблица создана или уже существует");
        }
    }

    private static void createTableRating(Connection conn) throws SQLException {
        String sql = "CREATE TABLE rating(\n" +
                "    rating int not null,\n" +
                "    uuid text not null,\n" +
                "    usdt text,\n" +
                "    foreign key (uuid) references player(uuid),\n" +
                "    foreign key (usdt) references player(usdt)\n" +
                "    foreign key (rating) references player(rating)\n" +
                ")";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Таблица создана или уже существует");
        }
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
            ps = connection.prepareStatement("INSERT INTO player (uuid, username) VALUES (?,?)");
            ps.setString(1, player.getPlayer().getUniqueId().toString());
            ps.setString(2, player.getPlayer().getName());
            ps.executeUpdate();
            closeConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        closeConnection();


    }


}
