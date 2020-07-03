package findcochange;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectToMySQL {

    public static void main(String[] args) throws Exception {

        Connection conn = null;
        try {

            // Load the MySQL JDBC driver
            String driverName = "com.mysql.cj.jdbc.Driver";

            Class.forName(driverName);

            // Create a connection to the database
            String serverName = "127.0.0.1:3308";

            String schema = "qmailadmin";

            String url = "jdbc:mysql://" + serverName + "/cochange_" + schema;

            String username = "root";

            String password = "";

            /**
             * ****************************************************************
             */
            File ctag_changes;
            BufferedReader br1 = null, br2 = null;

            String st1;
            String[] splited1;
            String query;
            ctag_changes = new File("data_files/freecol_changes.txt");

            br1 = new BufferedReader(new FileReader(ctag_changes));
            conn = DriverManager.getConnection(url, username, password);
            Statement st = conn.createStatement();
            while ((st1 = br1.readLine()) != null) {
                //System.out.println("# "+ st1);
                splited1 = st1.split("\\s+");
                query="INSERT INTO `change_info` (`version`, `file_name`, `change_type`, `startline`, `endline`)"
                        +" VALUES ('"+splited1[0]+"', '"+splited1[1]+"', '"+splited1[2]+"', '"+splited1[3]+"', '"+splited1[4]+"');";
                System.out.println(query);
                st.executeUpdate(query);
            }
            
            
            // iterate through the java resultset
            /*query="INSERT INTO `change_info` (`version`, `file_name`, `change_type`, `startline`, `endline`) VALUES ('15', 'general.h', 'c', '3', '3')";
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                int id = rs.getInt("id");
                int firstName = rs.getInt("version");
                String lastName = rs.getString("file_name");
                String dateCreated = rs.getString("change_type");
                int isAdmin = rs.getInt("startline");
                int numPoints = rs.getInt("endline");

                // print the results
                System.out.format("%s, %s, %s, %s, %s, %s\n", id, firstName, lastName, dateCreated, isAdmin, numPoints);
            }*/
            st.close();

            System.out.println("Successfully Connected to the database!");

        } catch (ClassNotFoundException e) {

            System.out.println("Could not find the database driver " + e.getMessage());
        } catch (SQLException e) {

            System.out.println("Could not connect to the database " + e.getMessage());
        }

    }
}
