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

public class ConnectToMySQL_nicad5 {

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
            File f_nicad;
            BufferedReader br2 = null;

            String st2, query, fileloc, filename, cur_clone_class;
            String[] splited1, fileparts;
            int i, startline, endline, b1, e1, b2, e2;
            Pattern p = Pattern.compile("\"([^\"]*)\"");
            conn = DriverManager.getConnection(url, username, password);
            Statement st = conn.createStatement();
            for (i = 1; i <= 317; i++) {
                //f_nicad = new File("data_files/nicad5_block_clones_class_results/version-"+i+"_blocks-blind-clones-0.30-classes.xml");
                f_nicad = new File("H:\\detected_clone_results\\nicad5_"+schema+"\\revision-"+i+"_blocks-blind-clones\\revision-"+i+"_blocks-blind-clones-0.30-classes.xml");
                if (f_nicad.exists()) {
                    br2 = new BufferedReader(new FileReader(f_nicad));                
                    cur_clone_class="-1";
                    while ((st2 = br2.readLine()) != null) {
                        if (st2.length() > 6 && st2.substring(1, 7).equals("source")) {
                            Matcher m = p.matcher(st2);
                            m.find();
                            fileloc = m.group(1);
                            filename = fileloc.replace("../subject_systems/"+schema+"/revision-"+i+"/", "").replace(".ifdefed", "");
                            /*if(i<10)
                                filename=fileloc.substring(55);
                            else if(i<100)
                                filename=fileloc.substring(56);
                            else if(i<1000)
                                filename=fileloc.substring(57);
                            else 
                                filename=fileloc.substring(58);
                            */
                            //filename = filename.replaceAll(".ifdefed", "");
                            m.find();
                            startline = Integer.parseInt(m.group(1));
                            m.find();
                            endline = Integer.parseInt(m.group(1));
                            //System.out.println(filename+", "+startline+", "+endline);   
                            query = "INSERT INTO `clones_nicad5` (`version`, `CloneClass`, `file_name`, `startline`, `endline`)"
                                    + " VALUES ('" + i + "', '" + cur_clone_class + "', '" + filename + "', '" + startline + "', '" + endline + "');";
                            System.out.println(query);
                            st.executeUpdate(query);

                        } else if (st2.length() > 5 && st2.substring(1, 6).equals("class")) {
                            Matcher m = p.matcher(st2);
                            m.find();
                            cur_clone_class = m.group(1);
                        }

                        
                    }
                } else {
                    System.out.println("Not found NiCAD Version-" + i + ".txt");
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
