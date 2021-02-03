package ServerCBox;





/*
CREATE TABLE Clients (
        id       INTEGER PRIMARY KEY AUTOINCREMENT
        UNIQUE
        NOT NULL,
        login    STRING  UNIQUE
        NOT NULL,
        password STRING  NOT NULL
        );
*/

import java.sql.*;

public class SqlHandler {

    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement psInsert;

    synchronized static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:Server/ClientsBase.db");
            statement = connection.createStatement();
           /* statement.execute("CREATE TABLE Clients (\n" +
                    "        id       INTEGER PRIMARY KEY AUTOINCREMENT\n" +
                    "        UNIQUE\n" +
                    "        NOT NULL,\n" +
                    "        login    STRING  UNIQUE\n" +
                    "        NOT NULL,\n" +
                    "        password STRING  NOT NULL\n" +
                    "        )");*/
            System.out.println("connection.");
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    synchronized static String getLogin(String login, String password) {
        String query = String.format("select login from Clients where login='%s' and password='%s'", login, password);
        try (ResultSet set = statement.executeQuery(query)) {
            if (set.next())
                return set.getString(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    synchronized static boolean setLogin (String login, String password){
        try {
            /*psInsert = connection.prepareStatement("INSERT INTO Clients (login, password) VALUES (?, ?)");
            psInsert.setString(1, login);
            psInsert.setString(1, password);
            psInsert.executeUpdate();*/
            statement.executeUpdate("INSERT into Clients (login, password) values ('" + login + "', '" + password + "')");
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }


    }


    synchronized static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
