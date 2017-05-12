/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reader.cssc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author john
 */
public class CsscTest {
    
    public static void main(String[] args) {
        String csvfile_new = "/Users/john/Projects/V/ups_new/cssc/cssc-4-18-17/4-18-17.csv";
        String csvfile_collection = "/Users/john/Projects/V/ups_new/cssc/cssc-4-18-17/11244-28096-2.csv";
        String csvfile_old = "/Users/john/Projects/V/ups_new/cssc/old.csv";
        String statesfile = "states.csv";
        String zipfile = "zipcode.csv";
        String imgpath = "/Users/john/Projects/V/ups_new/cssc/cssc-4-18-17/photos";
        String csvfile_existing = "/Users/john/Projects/V/ups_new/cssc/cssc-4-18-17/4-18-existing.csv";
        String csvfile_newsamples = "/Users/john/Projects/V/ups_new/cssc/cssc-4-18-17/4-18-new.csv";

        Cssc cs = new Cssc();

        List<String[]> table_new = cs.CsvToTable(csvfile_new);
        List<String[]> table_collection = cs.CsvToTable(csvfile_collection);
        List<String[]> table_old = cs.CsvToTable(csvfile_old);
        List<String[]> table_zip = cs.CsvToTable(zipfile);
        List<String[]> table_state = cs.CsvToTable(statesfile);
        List<String[]> existingList = new ArrayList<>();
        List<String[]> newList = new ArrayList<>();
        List<String[]> table_existing = cs.CsvToTable(csvfile_existing);
        List<String[]> table_newsamples = cs.CsvToTable(csvfile_newsamples);

        Path src_lic = FileSystems.getDefault().getPath("license.txt");
        Path src_dc = FileSystems.getDefault().getPath("dublin_core.xml");

        String workingdirectory = System.getProperty("user.dir");
        String folder = "test-041817";
        String dest = (new File(workingdirectory, folder)).getPath();
                
        // get existing ones
        for (int i = 1; i < table_new.size(); i++) {
            String sampleid = table_new.get(i)[1];
            String internalid = table_new.get(i)[0];
            for (int j = 1; j < table_collection.size(); j++) {
                String sampleid2 = table_collection.get(j)[13].replace("\"", "");
                String internalid2 = table_collection.get(j)[10].replace("\"", "");
                if (sampleid.equals(sampleid2)) {
                    System.out.println(Arrays.toString(table_collection.get(j)));
                    String[] arrayTableNew = table_new.get(i);
                    List<String> listTableNew = Arrays.asList(arrayTableNew);
                    ArrayList<String> updatableList = new ArrayList<String>();
                    String geo = table_collection.get(j)[14];
                    updatableList.addAll(listTableNew);
                    updatableList.add(geo);
                    String[] array = updatableList.toArray(new String[0]);
                    existingList.add(array);
                    break;
                }
            }
        }

        // get new ones
        for (int i = 1; i < table_new.size(); i++) {
            String sampleid = table_new.get(i)[1];
            String shortState = table_new.get(i)[4];
            String wholeName = cs.getWholeStateName(table_state, shortState);        
            boolean flag = false;
            for (int j = 1; j < table_collection.size(); j++) {
                String sampleid2 = table_collection.get(j)[13].replace("\"", "");
                if (sampleid.equals(sampleid2)) {
                    flag = true;
                    break;
                }
            }
            if (flag == false) {
                String zip = table_new.get(i)[5];
                String latlon = cs.getLatLon(table_zip, zip);
                String[] arrayTableNew = table_new.get(i);
                List<String> listTableNew = Arrays.asList(arrayTableNew);
                ArrayList<String> updatableList = new ArrayList<String>();
                updatableList.addAll(listTableNew);
                updatableList.add(latlon);
                String[] array = updatableList.toArray(new String[0]);
                System.out.println(Arrays.toString(array));
                newList.add(array);
            }
        }
        //generate SAF from existing list
        List<String> imglist = cs.GetImgList(imgpath);
        for (int i = 0; i < table_existing.size(); i++) {
            String test = Integer.toString(i + 1);

            Path dict = cs.CreateDirectory(dest, test);

            File destfile_license = cs.CreateFile(dict, "license.txt");
            File destfile_dc = cs.CreateFile(dict, "dublin_core.xml");
            File destfile_dwc = cs.CreateFile(dict, "metadata_dwc.xml");
            File destfile_contents = cs.CreateFile(dict, "contents");
            File destfile_handle = cs.CreateFile(dict, "handle");

            Path dest_license = destfile_license.toPath();
            Path dest_dc = destfile_dc.toPath();
            Path dest_dwc = destfile_dwc.toPath();
            Path dest_contents = destfile_contents.toPath();
            Path dest_handle = destfile_handle.toPath();

            String sampleid = table_existing.get(i)[13];
            List<Integer> selected = cs.GetImgIDs(imglist, sampleid);
            List<String> imgnamelist = new ArrayList<String>();

            for (int j = 0; j < selected.size(); j++) {
                String imgname = imglist.get(selected.get(j));
                File destfile_img = cs.CreateFile(dict, imgname);
                Path dest_img = destfile_img.toPath();
                Path src_img = FileSystems.getDefault().getPath(imgpath + "/" + imgname);

                cs.CopyFileTo(src_img, dest_img);

                imgnamelist.add(imgname);
            }

            // generate dublin_core.xml
            String handleid_dub = table_existing.get(i)[2].substring(28);
            String wikilink_S = table_existing.get(i)[3];
            String[] wikilinks = wikilink_S.split(Pattern.quote("||"));

            String wikidc = cs.GenerateDublinCoreForCorrections(handleid_dub, wikilinks);
            List<String> wikidclist = new ArrayList<String>();
            wikidclist.add(wikidc);
            Path wikidest_dc = destfile_dc.toPath();

            cs.WriteFile(wikidclist, wikidest_dc.toString());

            String dwc = cs.GenerateDWC(table_existing.get(i));
            List<String> dwclist = new ArrayList<String>();
            dwclist.add(dwc);
            cs.WriteFile(dwclist, dest_dwc.toString());

            List<String> contents = cs.GenerateContents(imgnamelist);
            cs.CopyFileTo(src_lic, dest_license);
            cs.WriteFile(contents, dest_contents.toString());

            // map file for existing samples
            List<String> handle = new ArrayList<String>();
            String handleid = table_existing.get(i)[2].substring(22);
            handle.add(handleid);
            cs.WriteFile(handle, dest_handle.toString());
            System.out.println(test + " " + handleid);
        }
        //generate SAF from new list
        for (int i = 0; i < table_newsamples.size(); i++) {
            String test = Integer.toString(i + 1);

            Path dict = cs.CreateDirectory(dest, test);

            File destfile_license = cs.CreateFile(dict, "license.txt");
            File destfile_dc = cs.CreateFile(dict, "dublin_core.xml");
            File destfile_dwc = cs.CreateFile(dict, "metadata_dwc.xml");
            File destfile_contents = cs.CreateFile(dict, "contents");

            Path dest_license = destfile_license.toPath();
            Path dest_dc = destfile_dc.toPath();
            Path dest_dwc = destfile_dwc.toPath();
            Path dest_contents = destfile_contents.toPath();

            String sampleid = table_newsamples.get(i)[1];
            List<Integer> selected = cs.GetImgIDs(imglist, sampleid);
            List<String> imgnamelist = new ArrayList<String>();
            for (int j = 0; j < selected.size(); j++) {
                String imgname = imglist.get(selected.get(j));
                File destfile_img = cs.CreateFile(dict, imgname);
                Path dest_img = destfile_img.toPath();
                Path src_img = FileSystems.getDefault().getPath(imgpath + "/" + imgname);

                cs.CopyFileTo(src_img, dest_img);

                imgnamelist.add(imgname);
            }

            String dwc = cs.GenerateDWCFromNew(table_newsamples.get(i));
            List<String> dwclist = new ArrayList<String>();
            dwclist.add(dwc);
            cs.WriteFile(dwclist, dest_dwc.toString());

            List<String> contents = cs.GenerateContents(imgnamelist);
            cs.CopyFileTo(src_lic, dest_license);
            cs.CopyFileTo(src_dc, dest_dc);
            cs.WriteFile(contents, dest_contents.toString());
        }

//        // for tqxonomy
        String csvfile_Wiki = "/Users/john/Projects/V/ups_new/cssc/cssc-4-18-17/internalcode_wiki.csv";
        String wikifolder = "test-041817-wiki2";
        String csvfile_Wikiq = "/Users/john/Projects/V/ups_new/cssc/cssc-4-18-17/11244-28096-wiki.csv";
        String wikifolderq = "test-041817-wiki2";

        String wikidest = (new File(workingdirectory, wikifolder)).getPath();

        List<String[]> table_wiki = cs.CsvToTable(csvfile_Wiki);
        List<String[]> table_wikiq = cs.CsvToTable(csvfile_Wikiq);

        for (int i = 0; i < table_wiki.size(); i++) {
            String testdir = Integer.toString(i + 1);

            String handle = null;
            String wikilink = table_wiki.get(i)[1];
            String intercode = table_wiki.get(i)[0];

            handle = cs.GetHandleFromCollection(intercode, table_wikiq);

            if (handle != null) {
                Path wikidict = cs.CreateDirectory(wikidest, testdir);

                File wikidestfile_dc = cs.CreateFile(wikidict, "dublin_core.xml");
                Path wikidest_dc = wikidestfile_dc.toPath();

                String wikidc = cs.GenerateDublinCore(handle, wikilink);
                List<String> wikidclist = new ArrayList<String>();
                wikidclist.add(wikidc);
                cs.WriteFile(wikidclist, wikidest_dc.toString());

            }
        }

//        for points.js
        List<String> pointjs = new ArrayList<>();
        List<String[]> table_Collection = cs.CsvToTable(csvfile_Wikiq);
        List<String[]> points = cs.GetPointsJSData(table_Collection);
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        StringBuilder sb3 = new StringBuilder();
        StringBuilder sb4 = new StringBuilder();
        sb1.append("var titlelist = [");
        sb2.append("var spatiallist = [");
        sb3.append("var urllist = [");
        sb4.append("var placelist = [");
        int length = points.size();
        for (int i = 0; i < length - 1; i++) {
            sb1.append("\"" + points.get(i)[0] + "\",");
            sb2.append("\"" + points.get(i)[1] + "\",");
            sb3.append("\"" + points.get(i)[2] + "\",");
            sb4.append("\"" + points.get(i)[3] + "\",");
        }
        sb1.append("\"" + points.get(length - 1)[0] + "\"]");
        sb2.append("\"" + points.get(length - 1)[1] + "\"]");
        sb3.append("\"" + points.get(length - 1)[2] + "\"]");
        sb4.append("\"" + points.get(length - 1)[3] + "\"]");
        pointjs.add(sb1.toString());
        pointjs.add(sb2.toString());
        pointjs.add(sb3.toString());
        pointjs.add(sb4.toString());
        cs.WriteFile(pointjs, "points.js");
    }
}
