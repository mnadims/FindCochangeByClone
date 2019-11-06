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

public class refresh_result_deckard {

    public static void main(String[] args) throws Exception {

        Connection conn = null;
        try {

            // Load the MySQL JDBC driver
            String driverName = "com.mysql.cj.jdbc.Driver";

            Class.forName(driverName);

            // Create a connection to the database
            String serverName = "localhost";

            String schema = "freecol";

            String url = "jdbc:mysql://" + serverName + "/" + schema;

            String username = "root";

            String password = "";

            /**
             * ****************************************************************
             */
            File f_nicad;
            BufferedReader br2 = null;

            String query, in_ids="", filename, cur_clone_class, cur_filename, nxt_filename="";
            String[] splited1, fileparts;
            int i, startline, endline, b1, e1, b2=0, e2=0, cur_version, cur_class, nxt_class=0, nxt_version=0, cur_id, nxt_id;
            Pattern p = Pattern.compile("\"([^\"]*)\"");
            conn = DriverManager.getConnection(url, username, password);
            Statement st = conn.createStatement();
            Statement st2 = conn.createStatement();
            /*for(i=1;i<775;i++){
                query="INSERT INTO `clone_count_versions` (`version`, `nicad_5`, `iclones_0_2`, `deckard_2_0`, `simcad_2_2`, `simian_2_5_10`) VALUES ('"+i+"', '0', '0', '0', '0', '0')";
                //System.out.println(query);
                st.executeUpdate(query);                
            }*/
            
            query="SELECT `id`, `version`, `CloneClass`, `file_name`, `startline`, `endline` FROM `deckard_2_0_result` ORDER BY `version`, `CloneClass`, `file_name`, `startline`, `endline`";
            System.out.println(query);
            ResultSet rs = st.executeQuery(query);
            i=1;
            rs.next();
            cur_id=rs.getInt("id");
            cur_version=rs.getInt("version");
            cur_class=rs.getInt("CloneClass");
            cur_filename=rs.getString("file_name");
            b1=rs.getInt("startline");
            e1=rs.getInt("endline");
           
            while(rs.next()){ 
                nxt_id=rs.getInt("id");
                nxt_version=rs.getInt("version");
                nxt_class=rs.getInt("CloneClass");
                nxt_filename=rs.getString("file_name");
                b2=rs.getInt("startline");
                e2=rs.getInt("endline");
                if(nxt_filename.equals(cur_filename) && nxt_version==cur_version && nxt_class==cur_class && ((e2>=b1 && e2<=e1) || (b2>=b1 && b2<=e1) || b2<b1 && e2>e1)){
                    in_ids+=cur_id+", "+nxt_id;
                } 
                cur_id=nxt_id;
                cur_version=nxt_version;
                cur_class=nxt_class;
                cur_filename=nxt_filename;
                b1=b2;
                e1=e2;

            }
            System.out.println(in_ids);
            st.close();

            System.out.println("Successfully Connected to the database!");

        } catch (ClassNotFoundException e) {

            System.out.println("Could not find the database driver " + e.getMessage());
        } catch (SQLException e) {

            System.out.println("Could not connect to the database " + e.getMessage());
        }

    }    
}
