package findcochange;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectToMySQL_deckard_2_0 {

    public static void main(String[] args) throws Exception {

        Connection conn = null;
        try {

            // Load the MySQL JDBC driver
            String driverName = "com.mysql.cj.jdbc.Driver";

            Class.forName(driverName);

            // Create a connection to the database
            String serverName = "127.0.0.1";

            String schema = "freecol";

            String url = "jdbc:mysql://" + serverName + "/" + schema;

            String username = "root";

            String password = "";

            /**
             * ****************************************************************
             */
            File f_clones;
            BufferedReader br2 = null;

            String st2, query, lines, filename;
            String[] splited1, linespart;
            int i, startline, endline, b1, e1, b2, e2,cur_clone_class;
            conn = DriverManager.getConnection(url, username, password);
            Statement st = conn.createStatement();
            for (i = 1000; i <= 2000; i++) {
                //version-2_blocks-blind-clones-0.3.xml
                File folder = new File("H:\\Detected_Clone_Results\\deckard_freecol_output\\version-"+i+"\\");
                if(folder.isDirectory() && folder.list().length>0){
                    File[] listOfFiles = folder.listFiles();
                    //f_clones = new File("F:\\01Programming_Data\\Detected_Clone_output\\deckard_brlcad_output\\version-"+i+"\\cluster_vdb_30_5_g7_2.12132_30_100000");
                    //                     F:\01Programming_Data\Detected_Clone_output\deckard_brlcad_output\version-50\\cluster_vdb_30_5_g8_2.12132_30_100000
                    if (listOfFiles[0].isFile()) {
                        br2 = new BufferedReader(new FileReader(listOfFiles[0]));                
                        cur_clone_class=1;
                        while ((st2 = br2.readLine()) != null) {
                            //System.out.println(st2);
                            if(!st2.equals("")){
                                splited1 = st2.split("\\s+");
                                if(splited1.length>8 && splited1[2].equals("FILE")){
                                    filename = splited1[3].replaceAll("/home/mnadims/subject_system/freecol/repository/version-"+i+"/", "");
                                    lines=splited1[4].replaceAll("LINE:", "");
                                    linespart=lines.split(":");
                                    startline=Integer.parseInt(linespart[0]);
                                    endline=startline+Integer.parseInt(linespart[1]);
                                    //System.out.println("Clone Class: "+cur_clone_class+", "+filename+", "+startline+", "+endline);
                                    query = "INSERT INTO `deckard_2_0_result` (`version`, `CloneClass`, `file_name`, `startline`, `endline`)"
                                            + " VALUES ('" + i + "', '" + cur_clone_class + "', '" + filename + "', '" + startline + "', '" + endline + "')";
                                    System.out.println(st2.trim().length()+": "+query);
                                    st.executeUpdate(query);
                                }
                            }
                            else {
                                System.out.println(st2.trim().length());                        
                                cur_clone_class++;
                            }

                        }
                    } else {
                        System.out.println("Not found Clones File Version-" + i + ".txt");
                    }
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
