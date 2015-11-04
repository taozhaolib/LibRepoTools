/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.shareok.data.documentProcessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.BreakClear;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.LineSpacingRule;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TextAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.VerticalAlign;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.shareok.data.documentProcessor.exceptions.*;

/**
 * Handles Word files
 * @author Tao Zhao
 */
public class WordHandler implements FileHandler {
      
    private String fileName;
    private FileRouter router;
    private HashMap data;

    /**
     *
     * @return
     */
    public FileRouter getRouter() {
        return router;
    }

    /**
     *
     * @return
     */
    @Override
    public HashMap getData() {
        return data;
    }

    /**
     *
     * @return
     */
    public String getFileName() {
        return fileName;
    }

    /**
     *
     * @param fileName
     */
    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     *
     * @param router
     */
    public void setRouter(FileRouter router) {
        this.router = router;
    }

    /**
     *
     * @param data
     */
    public void setData(HashMap data) {
        this.data = data;
    }
    
    /**
     * Based on the file extension to create corresponding workbook object and
     * return the Sheet object
     * 
     * @param extension : file extension name of the excel file
     * @param file : FileInputStream
     * @return Sheet object
     * @throws IOException : IO exception handler
     * 
     */
    private String[] getWordParagraphs(String extension, FileInputStream fs) throws IOException {
        String[] paragraphs = null;
        if("doc".equals(extension)){
            paragraphs = readDocFile(fs);
        }
        if("docx".equals(extension)){
            paragraphs = readDocxFile(fs);
        }
        return paragraphs;
    }
    
    private String[] readDocFile(FileInputStream fs) throws IOException {
 
        String[] paragraphs = null;
        try {
            HWPFDocument doc = new HWPFDocument(fs);
            WordExtractor we = new WordExtractor(doc);
            paragraphs = we.getParagraphText();                     
        } 
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            fs.close();
        }
        return paragraphs;
    }
 
    private String[] readDocxFile(FileInputStream fs) throws IOException {

        String[] paragraphs = null;
        try {
//            XWPFDocument doc = new XWPFDocument();
//            XWPFParagraph p1 = doc.createParagraph();
//        p1.setAlignment(ParagraphAlignment.CENTER);
//        p1.setBorderBottom(Borders.DOUBLE);
//        p1.setBorderTop(Borders.DOUBLE);
//
//        p1.setBorderRight(Borders.DOUBLE);
//        p1.setBorderLeft(Borders.DOUBLE);
//        p1.setBorderBetween(Borders.SINGLE);
//
//        p1.setVerticalAlignment(TextAlignment.TOP);
//
//        XWPFRun r1 = p1.createRun();
//        r1.setBold(true);
//        r1.setText("The quick brown fox");
//        r1.setBold(true);
//        r1.setFontFamily("Courier");
//        r1.setUnderline(UnderlinePatterns.DOT_DOT_DASH);
//        r1.setTextPosition(100);
//
//        XWPFParagraph p2 = doc.createParagraph();
//        p2.setAlignment(ParagraphAlignment.RIGHT);
//
//        //BORDERS
//        p2.setBorderBottom(Borders.DOUBLE);
//        p2.setBorderTop(Borders.DOUBLE);
//        p2.setBorderRight(Borders.DOUBLE);
//        p2.setBorderLeft(Borders.DOUBLE);
//        p2.setBorderBetween(Borders.SINGLE);
//
//        XWPFRun r2 = p2.createRun();
//        r2.setText("jumped over the lazy dog");
//        r2.setStrike(true);
//        r2.setFontSize(20);
//
//        XWPFRun r3 = p2.createRun();
//        r3.setText("and went away");
//        r3.setStrike(true);
//        r3.setFontSize(20);
//        r3.setSubscript(VerticalAlign.SUPERSCRIPT);
//
//
//        XWPFParagraph p3 = doc.createParagraph();
//        p3.setWordWrap(true);
//        p3.setPageBreak(true);
//                
//        //p3.setAlignment(ParagraphAlignment.DISTRIBUTE);
//        p3.setAlignment(ParagraphAlignment.BOTH);
//        p3.setSpacingLineRule(LineSpacingRule.EXACT);
//
//        p3.setIndentationFirstLine(600);
//        
//
//        XWPFRun r4 = p3.createRun();
//        r4.setTextPosition(20);
//        r4.setText("To be, or not to be: that is the question: "
//                + "Whether 'tis nobler in the mind to suffer "
//                + "The slings and arrows of outrageous fortune, "
//                + "Or to take arms against a sea of troubles, "
//                + "And by opposing end them? To die: to sleep; ");
//        r4.addBreak(BreakType.PAGE);
//        r4.setText("No more; and by a sleep to say we end "
//                + "The heart-ache and the thousand natural shocks "
//                + "That flesh is heir to, 'tis a consummation "
//                + "Devoutly to be wish'd. To die, to sleep; "
//                + "To sleep: perchance to dream: ay, there's the rub; "
//                + ".......");
//        r4.setItalic(true);
////This would imply that this break shall be treated as a simple line break, and break the line after that word:
//
//        XWPFRun r5 = p3.createRun();
//        r5.setTextPosition(-10);
//        r5.setText("For in that sleep of death what dreams may come");
//        r5.addCarriageReturn();
//        r5.setText("When we have shuffled off this mortal coil,"
//                + "Must give us pause: there's the respect"
//                + "That makes calamity of so long life;");
//        r5.addBreak();
//        r5.setText("For who would bear the whips and scorns of time,"
//                + "The oppressor's wrong, the proud man's contumely,");
//        
//        r5.addBreak(BreakClear.ALL);
//        r5.setText("The pangs of despised love, the law's delay,"
//                + "The insolence of office and the spurns" + ".......");
//
//        FileOutputStream out = new FileOutputStream("simple.docx");
//        doc.write(out);
//        out.close();
            XWPFDocument document = new XWPFDocument(OPCPackage.open("simple.docx"));
            List<XWPFParagraph> paragraphList = document.getParagraphs();            
            paragraphs = new String[paragraphList.size()];
            int i = 0;
            for (XWPFParagraph para : paragraphList) {
                paragraphs[i] = para.getText();
            }
        } catch (Exception e) {
                e.printStackTrace();
        } finally {
            fs.close();
        }
        return paragraphs;
    }
    
    /**
     * Check if the cells are date type
     * 
     * @param cell
     * @return : bool
     * @throws Exception 
     */
    private boolean isCellDateFormatted(Cell cell) throws Exception {
        try{
            return DateUtil.isCellDateFormatted(cell);
        }
        catch(Exception ex){
            ex.printStackTrace();
            throw new Exception ("The cell type data formatted cannot be decided!");
        }
    }
    
    /**
     * Reads out the data in a word file and stores data in a hashmap<p>
     * 
     * 
     */
    @Override
    public void readData() {
        
        String name = fileName;
        
        try {
            if(null == name || "".equals(name)) {
                throw new FileNameException("The file types are empty!");
            }
            
            String extension = FileUtil.getFileExtension(name);

            String[] wordTypes = router.loadOfficeFileType("word");
            
            if(null == wordTypes || wordTypes.length == 0){
                throw new FileTypeException("The file types are empty!");
            }

            HashMap<String,String> typeMap = new HashMap<>();
            for(String s : wordTypes){
                typeMap.put(s, s);
            }

            if(!typeMap.containsKey(extension)){
                throw new FileTypeException("Unrecognized file types");
            }
            
            File docFile = new File(name);
            FileInputStream fs = new FileInputStream(docFile);    
            
            String[] paragraphs = getWordParagraphs(extension, fs);

            for(String para : paragraphs) {
                System.out.print("paragraph here: " + para + "\n\n");
            }
            System.exit(0);
        }
        catch (IOException ioex) {
            Logger.getLogger(WordHandler.class.getName()).log(Level.SEVERE, null, ioex);
        }
        catch (Exception ex) {
            Logger.getLogger(WordHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Export data to an Xml file
     * 
     * @param map
     * @param filePath 
     */
    @Override
    public void exportMapDataToXml(HashMap map, String filePath) {
        try{
            
        }
        catch (Exception ex) {
            Logger.getLogger(ExcelHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

