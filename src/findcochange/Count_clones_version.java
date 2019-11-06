package findcochange;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Count_clones_version {

    public static void main(String[] args) throws Exception {

        Connection conn = null;
        try {

            // Load the MySQL JDBC driver
            String driverName = "com.mysql.cj.jdbc.Driver";

            Class.forName(driverName);

            // Create a connection to the database
            String serverName = "localhost";

            String schema = "nadim";

            String url = "jdbc:mysql://" + serverName + "/" + schema;

            String username = "root";

            String password = "";

            /**
             * ****************************************************************
             */
            File f_nicad;
            BufferedReader br2 = null;

            String query, fileloc, filename, cur_clone_class;
            String[] splited1, fileparts;
            int i, startline, endline, b1, e1, b2, e2;
            Pattern p = Pattern.compile("\"([^\"]*)\"");
            conn = DriverManager.getConnection(url, username, password);
            Statement st = conn.createStatement();
            Statement st2 = conn.createStatement();
            /*for(i=1;i<775;i++){
                query="INSERT INTO `clone_count_versions` (`version`, `nicad_5`, `iclones_0_2`, `deckard_2_0`, `simcad_2_2`, `simian_2_5_10`) VALUES ('"+i+"', '0', '0', '0', '0', '0')";
                //System.out.println(query);
                st.executeUpdate(query);                
            }*/
            
            query="SELECT `version`, COUNT(`version`) AS clones FROM `simian_result` GROUP BY `version`";
            System.out.println(query);
            ResultSet rs = st.executeQuery(query);
            i=1;
            while (rs.next()) {
                int version = rs.getInt("version");
                int clones = rs.getInt("clones");

                // print the results                
                //System.out.format("%d, %d\n", version, clones);
                query="UPDATE `clone_count_versions` SET `simian_2_5_10`='"+clones+"' WHERE `version`='"+version+"'";
                System.out.println(query);
                st2.executeUpdate(query);
                
                
            }
                
           

            st.close();
            st2.close();

            System.out.println("Successfully Connected to the database!");

        } catch (ClassNotFoundException e) {

            System.out.println("Could not find the database driver " + e.getMessage());
        } catch (SQLException e) {

            System.out.println("Could not connect to the database " + e.getMessage());
        }

    }    
}
