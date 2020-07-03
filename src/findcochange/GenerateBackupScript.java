/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package findcochange;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author MD NADIM
 */
public class GenerateBackupScript {

    public static void main(String[] args) throws Exception {
        Connection conn = null;
        try {
            String[] subject_systems = {"brlcad", "carol", "ctags", "freecol", "jabref", "jedit", "camellia", "qmailadmin"};

            for (String ss : subject_systems) {

                // Load the MySQL JDBC driver
                String driverName = "com.mysql.cj.jdbc.Driver";

                Class.forName(driverName);
                // Create a connection to the database
                String db_name = "cochange_" + ss;
                String serverName = "127.0.0.1:3306";
                String url = "jdbc:mysql://" + serverName + "/" + db_name;
                String username = "root";
                String password = "";

                /**
                 * ****************************************************************
                 */
                String query;
                conn = DriverManager.getConnection(url, username, password);
                Statement st = conn.createStatement();
                query = "SELECT table_name FROM information_schema.tables WHERE table_schema = '" + db_name + "';";
                ResultSet rs_detected = st.executeQuery(query);
                while (rs_detected.next()) {
                    System.out.println("mysqldump -uroot -p " + db_name + " " + rs_detected.getString("table_name") + " > H:/ResearchDBBackup/cochange/" + db_name + "_" + rs_detected.getString("table_name") + ".sql\n");
                }

                st.close();
                System.out.println("\n***************\n");

            }
            System.out.println("Successfully Completed!!!");

        } catch (ClassNotFoundException e) {

            System.out.println("Could not find the database driver " + e.getMessage());
        } catch (SQLException e) {

            System.out.println("Could not connect to the database " + e.getMessage());
        }

    }
}
