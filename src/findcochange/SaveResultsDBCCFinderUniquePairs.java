package findcochange;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SaveResultsDBCCFinderUniquePairs {

    public static void main(String[] args) throws Exception {
//        String[] subject_systems = {"brlcad", "carol", "ctags", "freecol", "jabref", "jedit"};
//        String[] versions = {"2-2115", "1-1700", "1-774", "1000-2000", "1-1545", "3787-4001"};
        
        String[] subject_systems = {"camellia", "qmailadmin"};
        String[] versions = {"1-301", "1-317"};

        Connection conn = null;
        for (int ss_id = 0; ss_id < 2; ss_id++) {//Subject System ID=ss_id   
//            if(ss_id==1)//Carol is already done!!!
//                continue;

            try {

                // Load the MySQL JDBC driver
                String driverName = "com.mysql.cj.jdbc.Driver";

                Class.forName(driverName);

                // Create a connection to the database
                String serverName = "localhost";

                String schema = "cochange_" + subject_systems[ss_id];

                String url = "jdbc:mysql://" + serverName + "/" + schema;

                String username = "root";

                String password = "";

                /**
                 * ****************************************************************
                 */
                File clone_file;
                BufferedReader br = null;

                String result_line, pair_line1, pair_line2, query, filename, tempfilepath1, tempfilepath2, filepath, pr_file1 = "", pr_startline1 = "", pr_endline1 = "", pr_file2 = "", pr_startline2 = "", pr_endline2 = "";
                String[] pair_line1_parts, pair_line2_parts;
                int i, clone_pair_id = 0, cur_line = 0;

                conn = DriverManager.getConnection(url, username, password);
                Statement st = conn.createStatement();
                String v_range[] = versions[ss_id].split("-");
                for (int v = Integer.parseInt(v_range[0]); v <= Integer.parseInt(v_range[1]); v++) {
                    System.out.printf("Working for %s, Version: %d\n", subject_systems[ss_id], v);
                    filepath = "G:\\detected_clone_results\\ccfinder_" + subject_systems[ss_id] + "\\final_results\\version-" + v + ".txt";
                    clone_file = new File(filepath);

                    if (clone_file.exists()) {
                        br = new BufferedReader(new FileReader(clone_file));
                        result_line = br.readLine();
                        pr_file1 = "";
                        pr_startline1 = "";
                        pr_endline1 = "";
                        pr_file2 = "";
                        pr_startline2 = "";
                        pr_endline2 = "";
                        while (result_line != null) {
                            //System.out.println(result_line);
                            if (result_line.trim().equals("pair start")) {
                                pair_line1 = br.readLine().trim();
                                pair_line2 = br.readLine().trim();
                                pair_line1_parts = pair_line1.split("\\s+");
                                pair_line2_parts = pair_line2.split("\\s+");

                                tempfilepath1 = pair_line1_parts[0].replace("\\", "/").replace("H:/" + subject_systems[ss_id] + "/repository/version-" + v + "/", "");
                                tempfilepath2 = pair_line2_parts[0].replace("\\", "/").replace("H:/" + subject_systems[ss_id] + "/repository/version-" + v + "/", "");
                                //System.out.printf("%d, %d, %s, %s, %s\n", cur_line, clone_pair_id, tempfilepath, result_line_parts[1], result_line_parts[2]);
                                query = "INSERT INTO `clones_ccfinder` (`version`, `ClonePair`, `file_name`, `startline`, `endline`)"
                                        + " VALUES ('" + v + "', '" + clone_pair_id + "', '" + tempfilepath1 + "', '" + pair_line1_parts[1] + "', '" + pair_line1_parts[2] + "'), ";

                                query += "('" + v + "', '" + clone_pair_id + "', '" + tempfilepath2 + "', '" + pair_line2_parts[1] + "', '" + pair_line2_parts[2] + "');";

                                //System.out.println(query);
                                if (!(tempfilepath1.equals(pr_file2) && pair_line1_parts[1].equals(pr_startline2) && pair_line1_parts[2].equals(pr_endline2) && tempfilepath2.equals(pr_file1) && pair_line2_parts[1].equals(pr_startline1) && pair_line2_parts[2].equals(pr_endline1))) {
                                    //System.out.println(query);
                                    st.executeUpdate(query);
                                    clone_pair_id++;
                                    pr_file1 = tempfilepath1;
                                    pr_startline1 = pair_line1_parts[1];
                                    pr_endline1 = pair_line1_parts[2];
                                    pr_file2 = tempfilepath2;
                                    pr_startline2 = pair_line2_parts[1];
                                    pr_endline2 = pair_line2_parts[2];
                                }
                            }

                            result_line = br.readLine();
                        }
                    } else {
                        System.out.println("Not found: " + filepath);
                    }
                    //System.out.println("\n\n");
//                    if (v > 5) {
//                        break;
//                    }
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
