/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package findcochange;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

/**
 *
 * @author MD NADIM
 */
public class CCFinderFindLineNumbers {

    public static void main(String[] args) throws Exception {
        String[] subject_systems = {"brlcad", "carol", "ctags", "freecol", "jabref", "jedit"};
        //String[] versions = {"1-2115", "1-1700", "1-774", "1000-2000", "1-1545", "3787-4001"};
        String[] versions = {"1-2115", "1-1700", "1-774", "1000-2000", "1-1545", "3930-4001"};
        String[] language = {"cpp", "java", "cpp", "java", "java", "java"};

        for (int ss_id = 5; ss_id < 6; ss_id++) {//Subject System ID=ss_id   
//            if(ss_id==1)//Carol is already done!!!
//                continue;
            String v_range[] = versions[ss_id].split("-");
            for (int v = Integer.parseInt(v_range[0]); v <= Integer.parseInt(v_range[1]); v++) {
                System.out.printf("Working for: %s, version: %d of %s\n", subject_systems[ss_id], v, v_range[1]);
                ParseCloneFile parse = new ParseCloneFile();
                parse.getCloneFile("G:\\detected_clone_results\\ccfinder_" + subject_systems[ss_id] + "\\pretty_results\\version-" + v + ".txt");
                parse.getFiles();
                parse.getClonePairs();
                parse.writeClonePairs("G:\\detected_clone_results\\ccfinder_" + subject_systems[ss_id] + "\\final_results\\version-" + v + ".txt");

            }

        }
        System.out.println("Completed all Executions.....");

    }

}

class ClonePair {

    String file1 = "";
    int sline1 = 0, eline1 = 0;
    String file2 = "";
    int sline2 = 0, eline2 = 0;
}

class ParseCloneFile {

    String basepath = "", clonefile = "";

    String[] files = new String[20000];
    int filecount = 0;

    ClonePair[] pairs = new ClonePair[50000];
    int pcount = 0;

    String pextension = "";

    public void writeClonePairs(String path) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            for (int i = 0; i < pcount; i++) {
                writer.write("\n\npair start ");
                writer.write("\n" + pairs[i].file1 + "    " + pairs[i].sline1 + "    " + pairs[i].eline1);
                writer.write("\n" + pairs[i].file2 + "    " + pairs[i].sline2 + "    " + pairs[i].eline2);
                writer.write("\npair end");
            }
            writer.close();
        } catch (Exception e) {
        }
    }

    public void getCloneFile(String cfile) {
        clonefile = cfile;
    }

    public void getFiles() {
        try {
            String str = "", path = "";
            int got = 0, serial = 0;
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(clonefile)));
            while ((str = br.readLine()) != null) {
                if (str.contains("source_files {")) {
                    got = 1;
                    continue;
                }
                if (got == 0) {
                    continue;
                }
                if (got == 1 && str.contains("}")) {
                    break;
                }

                serial = Integer.parseInt(str.split("[ \t]+")[0].trim());
                path = str.split("[ \t]+")[1].trim();
                files[serial] = path;
            }
        } catch (Exception e) {
        }
    }

    public void getClonePairs() {
        try {
            String str = "", pair = "", bpath = "";
            int got = 0, fileno1 = 0, sline1 = 0, eline1 = 0, fileno2 = 0, sline2 = 0, eline2 = 0;

            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(clonefile)));
            while ((str = br.readLine()) != null) {
                if (str.contains("option: -preprocessed_file_postfix ")) {
                    pextension = str.split("[ ]+")[2].trim();
                }
                if (str.contains("option: -n ")) {
                    bpath = str.split("[ ]+")[2].trim();
                }

                if (str.contains("clone_pairs {")) {
                    got = 1;
                    continue;
                }
                if (got == 0) {
                    continue;
                }
                if (got == 1 && str.contains("}")) {
                    break;
                }

                fileno1 = Integer.parseInt(str.split("[ \t]+")[1].trim().split("[.]+")[0].trim());
                sline1 = Integer.parseInt(str.split("[ \t]+")[1].trim().split("[.]+")[1].trim().split("[-]+")[0].trim());
                eline1 = Integer.parseInt(str.split("[ \t]+")[1].trim().split("[.]+")[1].trim().split("[-]+")[1].trim());

                String f = bpath;
                String r = files[fileno1].substring(f.length() + 1);
                String ccfxpath = bpath + "\\.ccfxprepdir\\" + r + pextension;
                int rln1 = getRealLineNumber(ccfxpath, sline1);
                int rln2 = getRealLineNumber(ccfxpath, eline1);

                pairs[pcount] = new ClonePair();
                pairs[pcount].file1 = files[fileno1];
                pairs[pcount].sline1 = rln1;
                pairs[pcount].eline1 = rln2;

                fileno2 = Integer.parseInt(str.split("[ \t]+")[2].trim().split("[.]+")[0].trim());
                sline2 = Integer.parseInt(str.split("[ \t]+")[2].trim().split("[.]+")[1].trim().split("[-]+")[0].trim());
                eline2 = Integer.parseInt(str.split("[ \t]+")[2].trim().split("[.]+")[1].trim().split("[-]+")[1].trim());

                f = bpath;
                r = files[fileno2].substring(f.length() + 1);
                ccfxpath = bpath + "\\.ccfxprepdir\\" + r + pextension;
                rln1 = getRealLineNumber(ccfxpath, sline2);
                rln2 = getRealLineNumber(ccfxpath, eline2);

                pairs[pcount].file2 = files[fileno2];
                pairs[pcount].sline2 = rln1;
                pairs[pcount].eline2 = rln2;

                pcount++;
            }
        } catch (Exception e) {
        }
    }

    public int getRealLineNumber(String ccfxfile, int line) {
        try {
            String str = "";
            int l = 0, lineno = 0;
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(ccfxfile)));
            while ((str = br.readLine()) != null) {
                l++;
                if (l < line) {
                    continue;
                }
                //Integer.valueOf(hex_value, 16)
                lineno = Integer.valueOf(str.split("[.]+")[0], 16);
                if (2 > 0) {
                    break;
                }
            }
            return lineno;
        } catch (Exception e) {
            System.out.println(e);
        }
        return -1;
    }

}
