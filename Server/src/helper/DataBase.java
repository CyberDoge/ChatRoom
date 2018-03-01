package helper;

import models.Message;

import java.sql.*;
import java.text.SimpleDateFormat;

public final class DataBase {
    /*
        User{login, password, messagesList}, messagesList{data, etc}
     */

    private static final String url = "jdbc:mysql://localhost:3306/chat_room";
    private static final String user = "root";
    private static final String password = "123";

    private static Connection connection;
    private static Statement statement;
    private static ResultSet resultSet;
    private static SimpleDateFormat dateFormat =  new SimpleDateFormat("HH:mm:ss");
    static {
        try {
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int register(String login, String password) {
        String task = "SELECT * FROM users WHERE name='" + login + "';";
        int res = 0;
        try {
            resultSet = statement.executeQuery(task);
            if (!resultSet.next()) {
                resultSet.first();
                task = "INSERT INTO users (name, password) VALUES ('" + login + "','" + password + "');";
                statement.executeUpdate(task);

                task = "SELECT id_user FROM users WHERE name='" + login + "';";
                resultSet = statement.executeQuery(task);
                if (resultSet.next())
                    res = resultSet.getInt(1);
            } else System.out.println("already exist");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("reg user with id = " + res);
        return res;
    }

    public static int login(String login, String password) {
        String query = "SELECT password, id_user FROM users WHERE name='" + login + "';";
        try {
            if ((resultSet = statement.executeQuery(query)).next()) {
                if (resultSet.getRow() != 0 && resultSet.getString(1).equals(password)) {
                    System.out.println("add user");
                    return resultSet.getInt(2);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void addMessage(Integer idUser, Date date, String message){
        try {
            String query = "INSERT  INTO messages (id_user, time, message) VALUES ("+idUser+ ", " + dateFormat.format(date.getTime()) + ", " + message+");";
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addMessage(Message message){
        try {
            String query = "INSERT  INTO messages (id_user, time, message) VALUES ("+message.userID+ ", '" + dateFormat.format(message.date.getTime()) + "', '" + message.mes+"');";
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
