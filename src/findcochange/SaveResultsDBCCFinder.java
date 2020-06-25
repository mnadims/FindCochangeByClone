package findcochange;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SaveResultsDBCCFinder {

    public static void main(String[] args) throws Exception {
        String[] subject_systems = {"brlcad", "carol", "ctags", "freecol", "jabref", "jedit"};
        String[] versions = {"2-2115", "1-1700", "1-774", "1000-2000", "1-1545", "3787-4001"};
        String filepath="", tempfilepath="";
        Connection conn = null;
        for (int ss_id = 5; ss_id < 6; ss_id++) {//Subject System ID=ss_id   
//            if(ss_id==1)//Carol is already done!!!
//                continue;

            try {

                // Load the MySQL JDBC driver
                String driverName = "com.mysql.cj.jdbc.Driver";

                Class.forName(driverName);

                // Create a connection to the database
                String serverName = "localhost";

                String schema = "cochange_"+subject_systems[ss_id];

                String url = "jdbc:mysql://" + serverName + "/" + schema;

                String username = "root";

                String password = "";

                /**
                 * ****************************************************************
                 */
                File clone_file;
                BufferedReader br = null;

                String result_line, query, filename;
                String[] result_line_parts;
                int i, clone_pair_id = 0, cur_line=0;

                conn = DriverManager.getConnection(url, username, password);
                Statement st = conn.createStatement();
                String v_range[] = versions[ss_id].split("-");
                for (int v = Integer.parseInt(v_range[0]); v <= Integer.parseInt(v_range[1]); v++) {
                    System.out.printf("Working for %s, Version: %d\n", subject_systems[ss_id], v);
                    filepath="G:\\detected_clone_results\\ccfinder_" + subject_systems[ss_id] + "\\final_results\\version-" + v + ".txt";
                    clone_file = new File(filepath);

                    if (clone_file.exists()) {
                        br = new BufferedReader(new FileReader(clone_file));
                        result_line = br.readLine();
                        while ( result_line != null) {                            
                            result_line=result_line.trim();
                            if(!(result_line.equals("pair start") || result_line.equals("pair end") || result_line.equals(""))){
                                result_line_parts = result_line.split("\\s+");
                                tempfilepath=result_line_parts[0].replace("\\","/").replace("H:/" + subject_systems[ss_id] + "/repository/version-" + v + "/","");
                                //System.out.printf("%d, %d, %s, %s, %s\n", cur_line, clone_pair_id, tempfilepath, result_line_parts[1], result_line_parts[2]);
                                query = "INSERT INTO `clones_ccfinder` (`version`, `ClonePair`, `file_name`, `startline`, `endline`)"
                                        + " VALUES ('" + v + "', '" + clone_pair_id + "', '" + tempfilepath + "', '" + result_line_parts[1] + "', '" + result_line_parts[2] + "');";
                                //System.out.println(query);
                                st.executeUpdate(query);
                                                               
                                cur_line++;
                                if(cur_line%2==0)
                                    clone_pair_id++;
                            }
                          result_line = br.readLine();
                        }
                    } else {
                        System.out.println("Not found: " + filepath);
                    }
                    //break;
                }

                st.close();

                System.out.println("Successfully Completed....");

            } catch (ClassNotFoundException e) {

                System.out.println("Could not find the database driver " + e.getMessage());
            } catch (SQLException e) {

                System.out.println("Could not connect to the database " + e.getMessage());
            }
            //break;
        }

    }
}
