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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author john
 */
public class Cssc {

    public List<String[]> CsvToTable(String csv) {
        String line = null;
        String[] lineArray = null;
        List<String[]> table = new ArrayList<String[]>();

        FileReader fileReader = null;
        try {
            fileReader = new FileReader(csv);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Cssc.class.getName()).log(Level.SEVERE, null, ex);
        }
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        try {
            while ((line = bufferedReader.readLine()) != null) {
                lineArray = line.split(",");
                for (int i = 0; i < lineArray.length; i++) {
                    lineArray[i] = lineArray[i].trim();
                }
                table.add(lineArray);
            }
            bufferedReader.close();
        } catch (IOException ex) {
            Logger.getLogger(Cssc.class.getName()).log(Level.SEVERE, null, ex);
        }

        return table;
    }

    public boolean ItemInArray(String item, String[] array) {
        boolean returnValue = false;

        for (String val : array) {
            if (item.equals(val)) {
                returnValue = true;
                break;
            }
        }
        return returnValue;
    }

    public void GenerateSAF(String csv, String ImgPath) {

    }

    public List<String> GenerateContents(List<String> list) {
        List<String> contents = new ArrayList<String>();
        String lineLicense = "license.txt	bundle:LICENSE";
        contents.add(lineLicense);

        for (int i = 0; i < list.size(); i++) {
            String line = null;
            line = list.get(i) + "	bundle:ORIGINAL";
            contents.add(line);
        }
        return contents;
    }

    public Path CreateDirectory(String path, String directory) {
        Path dict = null;
        File dic1 = new File(path);
        File dic2 = new File(dic1, directory);

        boolean success = dic2.mkdirs();
        if (success) {
            dict = Paths.get(path, directory);
        }
        return dict;
    }

    public String GenerateDWC(String[] elements) {
        int size = elements.length;
        StringBuilder sb = new StringBuilder();

        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>");
        sb.append(System.getProperty("line.separator"));
        sb.append("<dublin_core schema=\"dwc\">");
        sb.append(System.getProperty("line.separator"));
        sb.append("<dcvalue element=\"npdg\" qualifier=\"sampleid\" language=\"\">" + elements[13] + "</dcvalue>");
        sb.append(System.getProperty("line.separator"));
        sb.append("<dcvalue element=\"npdg\" qualifier=\"internalcode\" language=\"\">" + elements[10] + "</dcvalue>");
        sb.append(System.getProperty("line.separator"));
        sb.append("<dcvalue element=\"npdg\" qualifier=\"datecollected\" language=\"\">" + elements[4] + "</dcvalue>");
        sb.append(System.getProperty("line.separator"));
        sb.append("<dcvalue element=\"npdg\" qualifier=\"isolatesRBM\" language=\"\">" + elements[11] + "</dcvalue>");
        sb.append(System.getProperty("line.separator"));
        sb.append("<dcvalue element=\"npdg\" qualifier=\"isolatesTV8\" language=\"\">" + elements[12] + "</dcvalue>");
        sb.append(System.getProperty("line.separator"));
        sb.append("<dcvalue element=\"npdg\" qualifier=\"detail\" language=\"\">" + elements[5].replace(";", ",") + "</dcvalue>");
        sb.append(System.getProperty("line.separator"));

        sb.append("<dcvalue element=\"npdg\" qualifier=\"spatial\" language=\"\">" + elements[14].replace(";", ",") + "</dcvalue>");
        sb.append(System.getProperty("line.separator"));
        sb.append("<dcvalue element=\"npdg\" qualifier=\"homecity\" language=\"\">" + elements[6] + "</dcvalue>");
        sb.append(System.getProperty("line.separator"));
        sb.append("<dcvalue element=\"npdg\" qualifier=\"homestate\" language=\"\">" + elements[7] + "</dcvalue>");
        sb.append(System.getProperty("line.separator"));
        String zip = elements[8];
        if (zip.length() == 3) {
            zip = "00" + zip;
        }
        if (zip.length() == 4) {
            zip = "0" + zip;
        }
        sb.append("<dcvalue element=\"npdg\" qualifier=\"homezip\" language=\"\">" + zip + "</dcvalue>");
        sb.append(System.getProperty("line.separator"));
        sb.append("<dcvalue element=\"npdg\" qualifier=\"imagestatus\" language=\"\">" + elements[9] + "</dcvalue>");
        sb.append(System.getProperty("line.separator"));
        sb.append("</dublin_core>");

        return sb.toString();
    }

    public String GenerateDWCFromNew(String[] elements) {
        int size = elements.length;
        StringBuilder sb = new StringBuilder();

        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>");
        sb.append(System.getProperty("line.separator"));
        sb.append("<dublin_core schema=\"dwc\">");
        sb.append(System.getProperty("line.separator"));
        sb.append("<dcvalue element=\"npdg\" qualifier=\"sampleid\" language=\"\">" + elements[1] + "</dcvalue>");
        sb.append(System.getProperty("line.separator"));
        sb.append("<dcvalue element=\"npdg\" qualifier=\"internalcode\" language=\"\">" + elements[0] + "</dcvalue>");
        sb.append(System.getProperty("line.separator"));
        sb.append("<dcvalue element=\"npdg\" qualifier=\"datecollected\" language=\"\">" + elements[2] + "</dcvalue>");
        sb.append(System.getProperty("line.separator"));
        sb.append("<dcvalue element=\"npdg\" qualifier=\"isolatesRBM\" language=\"\">" + elements[7] + "</dcvalue>");
        sb.append(System.getProperty("line.separator"));
        sb.append("<dcvalue element=\"npdg\" qualifier=\"isolatesTV8\" language=\"\">" + elements[8] + "</dcvalue>");
        sb.append(System.getProperty("line.separator"));
        sb.append("<dcvalue element=\"npdg\" qualifier=\"detail\" language=\"\">" + elements[9].replace("\"", "").replace(";", ",") + "</dcvalue>");
        sb.append(System.getProperty("line.separator"));

        sb.append("<dcvalue element=\"npdg\" qualifier=\"spatial\" language=\"\">" + elements[11] + "," + elements[12] + "</dcvalue>");
        sb.append(System.getProperty("line.separator"));
        sb.append("<dcvalue element=\"npdg\" qualifier=\"homecity\" language=\"\">" + elements[3] + "</dcvalue>");
        sb.append(System.getProperty("line.separator"));
        sb.append("<dcvalue element=\"npdg\" qualifier=\"homestate\" language=\"\">" + elements[4] + "</dcvalue>");
        sb.append(System.getProperty("line.separator"));
        String zip = elements[5];
        if (zip.length() == 3) {
            zip = "00" + zip;
        }
        if (zip.length() == 4) {
            zip = "0" + zip;
        }
        sb.append("<dcvalue element=\"npdg\" qualifier=\"homezip\" language=\"\">" + zip + "</dcvalue>");
        sb.append(System.getProperty("line.separator"));
        sb.append("<dcvalue element=\"npdg\" qualifier=\"imagestatus\" language=\"\">" + elements[10] + "</dcvalue>");
        sb.append(System.getProperty("line.separator"));
        sb.append("</dublin_core>");

        return sb.toString();
    }

    public String GenerateDublinCore(String handle, String wikilink) {
        StringBuilder sb = new StringBuilder();
        String uribase = "http://hdl.handle.net/11244/";

        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>");
        sb.append(System.getProperty("line.separator"));
        sb.append("<dublin_core schema=\"dc\">");
        sb.append(System.getProperty("line.separator"));
        sb.append("<dcvalue element=\"identifier\" qualifier=\"uri\">" + uribase + handle + "</dcvalue>");
        sb.append(System.getProperty("line.separator"));
        sb.append("<dcvalue element=\"relation\" qualifier=\"wiki\" language=\"\">" + wikilink + "</dcvalue>");
        sb.append(System.getProperty("line.separator"));
        sb.append("</dublin_core>");

        return sb.toString();
    }

    public String GenerateDublinCoreForCorrections(String handle, String[] wikilinks) {
        StringBuilder sb = new StringBuilder();
        String uribase = "http://hdl.handle.net/11244/";

        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>");
        sb.append(System.getProperty("line.separator"));
        sb.append("<dublin_core schema=\"dc\">");
        sb.append(System.getProperty("line.separator"));
        sb.append("<dcvalue element=\"identifier\" qualifier=\"uri\">" + uribase + handle + "</dcvalue>");
        sb.append(System.getProperty("line.separator"));
        for (int i = 0; i < wikilinks.length; i++) {
            sb.append("<dcvalue element=\"relation\" qualifier=\"wiki\" language=\"\">" + wikilinks[i] + "</dcvalue>");
            sb.append(System.getProperty("line.separator"));
        }

        sb.append("</dublin_core>");

        return sb.toString();
    }

    public void WriteCSV(List<String[]> table, String filename) {
        try {
            BufferedWriter br = new BufferedWriter(new FileWriter(filename));

            for (int i = 0; i < table.size(); i++) {
                String[] lineArray = table.get(i);
                StringBuilder sb = new StringBuilder();
                for (String element : lineArray) {
                    sb.append(element);
                    sb.append(",");
                }
                sb.append(System.getProperty("line.separator"));
                br.write(sb.toString());
            }
        } catch (IOException ex) {
            Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void WriteFile(List<String> list, String filename) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(filename, "UTF-8");
            for (String element : list) {
                writer.println(element);
            }
            writer.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getLatLon(List<String[]> latlons, String zipcode) {
        String latLon = null;
        for (int i = 0; i < latlons.size(); i++) {
            if (zipcode.equals(latlons.get(i)[0])) {
                latLon = latlons.get(i)[1] + "," + latlons.get(i)[2];
            }
        }
        return latLon;
    }

    public String getWholeStateName(List<String[]> states, String shortname) {
        String state = "";
        for (int i = 0; i < states.size(); i++) {
            String abbrename = states.get(i)[0].substring(0, 2);
            String longname = states.get(i)[0].substring(5);
            if (shortname.equals(abbrename)) {
                state = longname + " - " + shortname;
            }
        }

        return state;
    }

    public File CreateFile(Path path, String filename) {
        File file;
        file = new File(path.toString(), filename);
        return file;
    }

    public List<String> GetImgList(String path) {
        List<String> list = new ArrayList<String>();

        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                list.add(listOfFiles[i].getName());
            }
        }
        return list;
    }

    public List<Integer> GetImgIDs(List<String> list, String sampleid) {
        List<Integer> idlist = new ArrayList<Integer>();
        sampleid = sampleid + "_";

        for (int i = 0; i < list.size(); i++) {
            String str = list.get(i);
            if (str.startsWith(sampleid)) {
                idlist.add(i);
            }
        }
        return idlist;
    }

    public void CopyFileTo(Path src, Path dst) {
        try {
            Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String GetHandle(String sampleid, List<String[]> table_collection) {
        String handle = null;

        for (int i = 0; i < table_collection.size(); i++) {
            String existingSampleid = table_collection.get(i)[13];
            if (sampleid.equals(existingSampleid)) {
                handle = table_collection.get(i)[2].substring(22);
                break;
            }
        }

        return handle;
    }

    public String GetHandleFromCollection(String intercode, List<String[]> table_collection) {
        String handle = null;

        for (int i = 0; i < table_collection.size(); i++) {
            String existingIntercode = table_collection.get(i)[12];
            if (intercode.equals(existingIntercode)) {
                String handleCol = table_collection.get(i)[2];
                if (!handleCol.equals("")) {
                    handle = table_collection.get(i)[2].substring(28);
                } else {
                    handle = table_collection.get(i)[3].substring(28);
                }
                break;
            }
        }

        return handle;
    }

    public String GetHandleFromIntercodeHandle(String intercode, List<String[]> table_IntercodeHandle) {
        String handle = null;

        for (int i = 0; i < table_IntercodeHandle.size(); i++) {
            String internalcode = table_IntercodeHandle.get(i)[0];
            if (intercode.equals(internalcode)) {
                handle = table_IntercodeHandle.get(i)[1];
                break;
            }
        }
        return handle;
    }

    public List<String[]> GetPointsJSData(List<String[]> table_Collection) {
        List<String[]> points = new ArrayList<>();

        for (int i = 1; i < table_Collection.size(); i++) {
            String title = null;
            String coordinates = null;
            String handle = null;
            String location = null;
            String handleValue = null;
            String homeCity = null;
            String homeState = null;
            String spatialValue = null;
            List<String> point = new ArrayList<>();

            title = table_Collection.get(i)[15];
            spatialValue = table_Collection.get(i)[16];
            if (!spatialValue.equals("")) {
                coordinates = spatialValue.replace(";", ",");
            }

            handleValue = table_Collection.get(i)[2];
            if (handleValue.equals("")) {
                handleValue = table_Collection.get(i)[3];
            }
            if (!handleValue.equals("")) {
                handle = "/handle/" + handleValue.substring(22);
            }

            homeCity = table_Collection.get(i)[8];
            homeState = table_Collection.get(i)[9];
            if (!(homeCity.equals("") || homeState.equals(""))) {
                if (homeState.split(Pattern.quote(" - ")).length == 2) {
                    location = homeCity + ", " + homeState.split(Pattern.quote(" - "))[1];
                }
            }

            if (!(title.equals("") || location == null || coordinates == null || handle == null)) {
                point.add(title);
                point.add(coordinates);
                point.add(handle);
                point.add(location);
                String[] pointElements = point.toArray(new String[0]);
                points.add(pointElements);
            }
        }
        return points;
    }
}
