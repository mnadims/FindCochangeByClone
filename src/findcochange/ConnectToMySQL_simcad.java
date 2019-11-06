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

public class ConnectToMySQL_simcad {

    public static void main(String[] args) throws Exception {

        Connection conn = null;
        try {

            // Load the MySQL JDBC driver
            String driverName = "com.mysql.cj.jdbc.Driver";

            Class.forName(driverName);

            // Create a connection to the database
            String serverName = "127.0.0.1";

            String schema = "cochange_carol";

            String url = "jdbc:mysql://" + serverName + "/" + schema;

            String username = "root";

            String password = "";

            /**
             * ****************************************************************
             */
            File f_nicad;
            BufferedReader br2 = null;

            String st2, query, fileloc, filename;
            String[] splited1, fileparts;
            int i, startline, endline, b1, e1, b2, e2, cur_clone_class;
            Pattern p = Pattern.compile("\"([^\"]*)\"");
            conn = DriverManager.getConnection(url, username, password);
            Statement st = conn.createStatement();
            for (i = 1; i <= 1700; i++) {
                //version-2_blocks-blind-clones-0.3.xml
                f_nicad = new File("H:/Detected_Clone_Results/simcad_carol_output/version-"+i+"/simcad_block_clone-groups_type-1-2-3_generous.xml");
                if (f_nicad.exists()) {
                    br2 = new BufferedReader(new FileReader(f_nicad));                
                    cur_clone_class=0;
                    while ((st2 = br2.readLine()) != null) {
                        if (st2.length() > 21 && st2.substring(1, 20).equals("CloneFragment file=")) {
                            //System.out.println(st2);
                            Matcher m = p.matcher(st2);
                            m.find();
                            fileloc = m.group(1);
                            filename = fileloc.replaceAll("/home/mnadims/subject_system/carol/repository/version-"+i+"/", "");                            
                            //filename = filename.replaceAll(".ifdefed", "");
                            m.find();
                            startline = Integer.parseInt(m.group(1));
                            m.find();
                            endline = Integer.parseInt(m.group(1));
                            //System.out.println(filename+", "+startline+", "+endline);   
                            query = "INSERT INTO `clones_simcad` (`version`, `CloneClass`, `file_name`, `startline`, `endline`)"
                                    + " VALUES ('" + i + "', '" + cur_clone_class + "', '" + filename + "', '" + startline + "', '" + endline + "');";
                            System.out.println(query);
                            st.executeUpdate(query);

                        } else {
                            cur_clone_class++;
                        }

                        
                    }
                } else {
                    System.out.println("Not found SimCAD Version-" + i + ".txt");
                }
            }

            st.close();

            System.out.println("Successfully Connected to the database!");

        } catch (ClassNotFoundException e) {

            System.out.println("Could not find the database driver " + e.getMessage());
        } catch (SQLException e) {

            System.out.println("Could not connect to the database " + e.getMessage());
        }

    }

    public static boolean isInteger(String s) {
        boolean isValidInteger = false;
        try {
            Integer.parseInt(s);

            // s is a valid integer
            isValidInteger = true;
        } catch (NumberFormatException ex) {
            // s is not an integer
        }

        return isValidInteger;
    }
}
