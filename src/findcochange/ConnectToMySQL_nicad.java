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

public class ConnectToMySQL_nicad {

    public static void main(String[] args) throws Exception {

        Connection conn = null;
        try {

            // Load the MySQL JDBC driver
            String driverName = "com.mysql.cj.jdbc.Driver";

            Class.forName(driverName);

            // Create a connection to the database
            String serverName = "localhost";

            String schema = "monoosc";

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
            for (i = 1; i <=355; i++) {
                //version-2_blocks-blind-clones-0.3.xml
                //version-13_blocks-blind-clones-0.30.xml
                f_nicad = new File("data_files/nicad4_block_blind_clones_monoosc/version-" + i + "_blocks-blind-clones-0.30.xml");
                if (f_nicad.exists()) {
                    br2 = new BufferedReader(new FileReader(f_nicad));                
                    cur_clone_class=-1;
                    while ((st2 = br2.readLine()) != null) {
                        if (st2.length() > 6 && st2.substring(1, 7).equals("source")) {
                            Matcher m = p.matcher(st2);
                            m.find();
                            fileloc = m.group(1);
                            //fileparts = fileloc.split("/");
                            filename = fileloc.replaceAll("/media/manishankar/research/systems/monoosc/repository/version-" + i + "/", "");
                            filename = filename.replaceAll(".ifdefed", "");
                            filename = filename.replaceAll("/", "");
                            
                            m.find();
                            startline = Integer.parseInt(m.group(1));
                            m.find();
                            endline = Integer.parseInt(m.group(1));
                            //System.out.println(filename+", "+startline+", "+endline);   
                            query = "INSERT INTO `nicad_result` (`version`, `CloneClass`, `file_name`, `startline`, `endline`)"
                                    + " VALUES ('" + i + "', '" + cur_clone_class + "', '" + filename + "', '" + startline + "', '" + endline + "');";
                            System.out.println(query);
                            st.executeUpdate(query);

                        } else {
                            cur_clone_class++;
                        }

                        /*
                        splited1 = st2.split("\\s+");
                        if (isInteger(splited1[1])) {
                            query = "INSERT INTO `iclones_result` (`version`, `CloneClass`, `file_name`, `startline`, `endline`)"
                                    + " VALUES ('" + i + "', '" + cur_clone_class + "', '" + splited1[2] + "', '" + splited1[3] + "', '" + splited1[4] + "');";
                            //System.out.println(query);
                            st.executeUpdate(query);
                        } else {
                            cur_clone_class = Integer.parseInt(splited1[2]);
                        }*/
                    }
                } else {
                    System.out.println("Not found NiCAD Version-" + i + ".txt");
                }
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
