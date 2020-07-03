package findcochange;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//class Store_change {
//
//    String filename;
//    int startline;
//    int endline;
//}

public class FindCochange_all_temp {

    public static void main(String[] args) throws Exception {

        Connection conn = null;
        File list_changes;
        BufferedReader br2 = null;
        Store_change[] stored_change = new Store_change[1000];
        String st2, qr_detected, qr_actual, qr_change_count, pr_filename, cur_filename, clone_results_table, cochange_table, qr_insert, change_id = "", list_cochange = "";
        String[] splited1;
        int b1, e1, ac_b1, ac_e1, pr_startline, pr_endline, cur_startline, cur_endline, chng_detect, chng_actual, used_clone, total_clone, counted, change_ptr;
        int k, duplicate = 0, comma_flg = 0;
        //Name of the Subject Systems    
        //String[] subject_systems = {"brlcad", "camellia", "carol", "ctags", "freecol", "jabref", "jedit", "qmailadmin"};
        String[] subject_systems = {"freecol", "jedit"};
        int[] total_changes = {22327, 11780};
        //total_changes: "brlcad=9805", "camellia=4333", "carol=12929", "ctags=3517", "freecol=22327", "jabref=19349", "jedit=11780", "qmailadmin=892"
        //int[] total_changes = {22327, 11780};
       String[] clone_detector = {"cloneworks"};
        //String[] clone_detector = {"ccfinder", "cloneworks", "conqat", "deckard_2_0", "iclones", "nicad5", "simcad", "simian"};   
        String[] clone_types = {"type3pattern", "type3token"};
        //String[] clone_types = {"type1", "type2blind", "type3pattern", "type3token"};
        //String[] clone_types = {"all"};
        String resultGrouping = "CloneClass"; //CloneClass or ClonePair
        int ss_sl = 0;
        for (String ss : subject_systems) {
            //ss = subject_systems[ss_sl];
            //for (String ss : subject_systems) {
            //for (int num : numbers)  
            for (String cd : clone_detector) {
                for (String clone_type : clone_types) {
                    if (clone_type.equals("all")) {
                        clone_results_table = "clones_" + cd;
                        cochange_table="cc_" + cd;
                    } else {
                        clone_results_table = "clones_" + cd + "_" + clone_type;
                        cochange_table="cc_" + cd+ "_" + clone_type;
                    }

                    try {

                        // Load the MySQL JDBC driver
                        String driverName = "com.mysql.cj.jdbc.Driver";

                        Class.forName(driverName);

                        // Create a connection to the database
                        String serverName = "127.0.0.1";

                        String schema = "cochange_" + ss;

                        String url = "jdbc:mysql://" + serverName + "/" + schema + "?serverTimezone=UTC";

                        String username = "root";

                        String password = "";

                        /**
                         * ****************************************************************
                         */
                        conn = DriverManager.getConnection(url, username, password);
                        Statement stm1 = conn.createStatement();
                        Statement stm2 = conn.createStatement();
                        Statement stm3 = conn.createStatement();
                        Statement stm4 = conn.createStatement();

                        list_changes = new File("data_files/" + ss + "_changes.txt");
                        br2 = new BufferedReader(new FileReader(list_changes));
                        int pr_version = 0, total_change = -1;
                        int change_sl = 1;
                        while ((st2 = br2.readLine()) != null) {
                            comma_flg = 0;
                            list_cochange = "";
                            splited1 = st2.split("\\s+");
                            b1 = Integer.parseInt(splited1[3]);
                            e1 = Integer.parseInt(splited1[4]);
                            if (pr_version != Integer.parseInt(splited1[0])) {
                                qr_change_count = "SELECT COUNT(*) AS num_change FROM `change_info` WHERE `version`=" + splited1[0];
                                ResultSet rs_change_count = stm1.executeQuery(qr_change_count);
                                if (rs_change_count.next()) {
                                    total_change = rs_change_count.getInt("num_change");
                                    //System.out.println(total_change);
                                } else {
                                    total_change = -1;
                                }
                                pr_version = Integer.parseInt(splited1[0]);
                            }

                            qr_detected = "SELECT * FROM `" + clone_results_table + "` WHERE "
                                    + "`version`=" + splited1[0] + " AND `" + resultGrouping + "` IN (SELECT `" + resultGrouping + "` FROM `" + clone_results_table + "` WHERE "
                                    + "`version`=" + splited1[0] + " AND `file_name`='" + splited1[1] + "' AND ((`endline`>=" + b1 + " AND `endline`<=" + e1 + ") "
                                    + " OR (`startline`>=" + b1 + " AND `startline`<=" + e1 + ") OR (`startline`<" + b1 + " AND `endline`>" + e1 + "))) AND "
                                    + " `id` NOT IN (SELECT `id` FROM `" + clone_results_table + "` WHERE `version`=" + splited1[0] + " AND `file_name`='" + splited1[1] + "' AND "
                                    + " ((`endline`>=" + b1 + " AND `endline`<=" + e1 + ") OR (`startline`>=" + b1 + " AND `startline`<=" + e1 + ") OR "
                                    + " (`startline`<" + b1 + " AND `endline`>" + e1 + "))) ORDER BY `file_name`, `startline`, `endline`";

                            //System.out.println(qr_detected);
                            ResultSet rs_detected = stm1.executeQuery(qr_detected);
                            pr_filename = "";
                            pr_startline = 0;
                            pr_endline = 0;
                            total_clone = 0;
                            chng_detect = 0;
                            used_clone = 0;
                            change_ptr = 0;
                            while (rs_detected.next()) {
                                //System.out.println(rs_detected.getString("file_name")+", "+rs_detected.getInt("startline")+", "+rs_detected.getInt("endline"));
                                cur_filename = rs_detected.getString("file_name");
                                cur_startline = rs_detected.getInt("startline");
                                cur_endline = rs_detected.getInt("endline");
                                if (!(pr_filename.equals(cur_filename) && pr_startline == cur_startline && pr_endline == cur_endline)) {
                                    total_clone++;
                                    qr_actual = "SELECT * FROM `change_info` WHERE `version`=" + splited1[0] + " AND `file_name`='" + cur_filename + "'";
                                    //System.out.println(qr_actual);
                                    ResultSet rs_actual = stm2.executeQuery(qr_actual);
                                    counted = 0;
                                    while (rs_actual.next()) {
                                        change_id = rs_actual.getString("id");
                                        ac_b1 = rs_actual.getInt("startline");
                                        ac_e1 = rs_actual.getInt("endline");
                                        if ((cur_endline >= ac_b1 && cur_endline <= ac_e1) || (cur_startline >= ac_b1 && cur_startline <= ac_e1) || (cur_startline < ac_b1 && cur_endline > ac_e1)) {
                                            duplicate = 0;
                                            if (change_ptr > 0) {
                                                for (k = 0; k < change_ptr; k++) {
                                                    if (stored_change[k].filename.equals(cur_filename) && stored_change[k].startline == ac_b1 && stored_change[k].endline == ac_e1) {
                                                        duplicate = 1;
                                                        break;
                                                    }

                                                }
                                            }
                                            if (duplicate == 0) {
                                                stored_change[change_ptr] = new Store_change();
                                                stored_change[change_ptr].filename = cur_filename;
                                                stored_change[change_ptr].startline = ac_b1;
                                                stored_change[change_ptr].endline = ac_e1;
                                                change_ptr++;
                                                chng_detect++;
                                                //System.out.println(cur_filename+", "+ac_b1+", "+ac_e1+", "+change_id);
                                                if (comma_flg > 0) {
                                                    list_cochange += ", ";
                                                }
                                                list_cochange += change_id;
                                                comma_flg = 1;
                                            }
                                            if (counted == 0) {
                                                used_clone++;
                                                counted = 1;
                                            }
                                        }
                                    }
                                    pr_filename = cur_filename;
                                    pr_startline = cur_startline;
                                    pr_endline = cur_endline;

                                }
                            }
                            qr_insert = "INSERT INTO `" + cochange_table + "` (`change_id`, `version`, `change_detect`, `change_total`, `clone_used`, `clone_pr_total`, `list_cochange`) "
                                    + " VALUES ((SELECT `id` FROM `change_info` WHERE `version`='" + splited1[0] + "' AND `file_name`='" + splited1[1] + "' AND `startline`='" + splited1[3] + "' AND `endline`='" + splited1[4] + "'),"
                                    + "'" + splited1[0] + "', '" + chng_detect + "', '" + (total_change - 1) + "', '" + used_clone + "', '" + total_clone + "', '" + list_cochange + "')";
                            if (chng_detect > 0) {
                                System.out.println("Working: " + ss + ", " + cd + ", " + clone_type + ", " + change_sl + "/" + total_changes[ss_sl] + "\nNumber of Cochanges: " + chng_detect + "\nList of Cochanges: " + list_cochange);
                                //System.out.println(qr_insert);
                                //System.out.println("\n-------\n"+qr_detected);
                            }
                            stm4.executeUpdate(qr_insert);
                            //System.out.println(splited1[0]+", "+splited1[1]+", "+splited1[3]+", "+splited1[4]+": "+"Detected Change: "+chng_detect+" Total Change: "+(total_change-1)+" Used Clone(s): "+used_clone+" Predicted Clone(s): "+total_clone);
                            //System.out.println("*****************");
                            change_sl++;
                        }

                        stm1.close();
                        stm2.close();
                        stm3.close();
                        stm4.close();

                    } catch (ClassNotFoundException e) {

                        System.out.println("Could not find the database driver " + e.getMessage());
                    } catch (SQLException e) {

                        System.out.println("Could not connect to the database " + e.getMessage());
                    }
                }
            }
            ss_sl++;
        }

    }

}
