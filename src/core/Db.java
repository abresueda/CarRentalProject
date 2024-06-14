package core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Db {
    //Singleton Design Pattern özelliğidir.Db'deki connectionı çağırırken hafızada bir kere oluşturuyoruz.
    private static Db instance = null;
    private Connection connection = null;
    private final String DB_URL = "jdbc:postgresql://localhost:5432/rentacar";
    private final String DB_USERNAME = "postgres";
    private final String DB_PASS = "selam123.";

    private Db() {
        try {
            this.connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASS);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }

    //Db sınıfının örneği bu metodda tutulur.Diğer sınıflarda tekrar kullanılmasını sağlar.
    public static Connection getInstance() {
       try {
           //Bağlantı null veya açık olup olmadığını kontrol eder.Eğer böyleyse yeni bir Db nesnesi oluşturup bağlantıyı kurar.
           if (instance == null || instance.getConnection().isClosed()) {
               instance = new Db();
           }
       }catch (SQLException e) {
           System.out.println(e.getMessage());
       }
        return instance.getConnection();
    }
}
