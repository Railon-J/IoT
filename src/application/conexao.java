package application;

import java.sql.Connection;
import java.sql.DriverManager;

public class conexao {

    private static final String URL = "jdbc:mysql://localhost:3306/arduino";
    private static final String USER = "root";
    private static final String PASS = "Casa34124826.";

    public static Connection conectar() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}