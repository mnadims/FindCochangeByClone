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

public class clone_result_intersect {

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

            String query1, query2, tab1, tab2, col_name;
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
            tab1="`iclones_result`";
            tab2="`nicad5_result`";
            col_name="simcad_simian";
            for(i=2;i<775;i++){
                System.out.println(i);
                query1="SELECT "+tab1+".`version` work_v, COUNT("+tab1+".`version`) cnt_inter  FROM "+tab1+", "+tab2+" \n" +
                        "WHERE "+tab1+".`file_name`="+tab2+".`file_name`\n" +
                        "AND (("+tab1+".`endline`>="+tab2+".`startline`\n" +
                        "AND "+tab1+".`endline`<="+tab2+".`endline`) \n" +
                        "OR ("+tab1+".`startline`>="+tab2+".`startline`\n" +
                        "AND "+tab1+".`startline`<="+tab2+".`endline`) \n" +
                        "OR ("+tab1+".`startline`<"+tab2+".`startline`\n" +
                        "AND "+tab1+".`endline`>"+tab2+".`endline`)) \n" +
                        "AND "+tab1+".`version`='"+i+"'\n" +
                        "AND "+tab2+".`version`='"+i+"'";
                //System.out.println(query);
                ResultSet rs = st.executeQuery(query1);
                while (rs.next()) {
                    int work_v = rs.getInt("work_v");
                    int cnt_inter = rs.getInt("cnt_inter");

                    // print the results                
                    System.out.format("%d, %d\n", work_v, cnt_inter);
                    query2="UPDATE `clone_result_intersect` SET `"+col_name+"`='"+cnt_inter+"' WHERE `version`='"+work_v+"'";
                    //System.out.println(query2);
                    //st2.executeUpdate(query2);


                }
                
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
