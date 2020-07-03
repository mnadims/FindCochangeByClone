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

public class ConnectToMySQL_conqat {

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

            String st2, query, fileloc, filename, filein, fileid,filedel, startLine,endLine, sourceFileId, clonein ;
            String[] file_holder=null, fileparts;
            int i, startline, endline, b1, e1, b2, e2, cur_clone_class;
            Pattern p = Pattern.compile("\"([^\"]*)\"");
            conn = DriverManager.getConnection(url, username, password);
            Statement st = conn.createStatement();
            for (i = 1; i <= 317; i++) {
                //f_nicad = new File("data_files/nicad5_block_clones_class_results/version-"+i+"_blocks-blind-clones-0.30-classes.xml");
                f_nicad = new File("H:\\detected_clone_results\\conqat_qmailadmin\\revision-18\\clones.xml");
                if (f_nicad.exists()) {
                    filedel = "DELETE FROM `temp_files`";
                    System.out.println(filedel);
                    st.executeUpdate(filedel);
                    br2 = new BufferedReader(new FileReader(f_nicad));                
                    cur_clone_class=0;
                    while ((st2 = br2.readLine()) != null) {
                        //System.out.println(st2);
                        //System.out.println(st2.substring(3, 13));
                        if(st2.length() > 6 && st2.substring(3, 13).equals("sourceFile")) {
                            System.out.println(st2.substring(3, 13));
                            Matcher m = p.matcher(st2);
                            m.find();
                            fileid = m.group(1);                                              
                            m.find();
                            fileloc = m.group(1);                            
                            //System.out.println(fileid+", "+fileloc); 
                            //file_holder[Integer.parseInt(fileid)]=fileloc;
                            filein = "INSERT INTO `temp_files` (`id`, `filename`)"
                                    + " VALUES ('" + fileid + "', '" + fileloc.replace("project/", "") + "')";
                            System.out.println(filein);
                            st.executeUpdate(filein);                            

                        } 
                        else if(st2.length() > 5 && st2.substring(5, 10).equals("clone")) {
                            System.out.println(st2);
                            Matcher m = p.matcher(st2);
                            m.find();
                            m.find();
                            m.find();
                            startLine = m.group(1);
                            m.find();
                            endLine = m.group(1);
                            m.find();
                            m.find();
                            m.find();
                            sourceFileId = m.group(1);
                            System.out.println(startLine+", "+endLine+", "+sourceFileId); 
                            
                            clonein = "INSERT INTO `clones_conqat` (`version`, `CloneClass`, `file_name`, `startline`, `endline`)"
                                    + " VALUES ('" + i + "', '" + cur_clone_class + "', (SELECT `filename` FROM `temp_files` WHERE `id`='" + sourceFileId + "'), '" + startLine + "', '" + endLine + "')";
                            System.out.println(clonein);
                            st.executeUpdate(clonein);
                        }
                        else cur_clone_class++;
                       
                        
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
