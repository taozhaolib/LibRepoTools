/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.sagedata;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.shareok.data.htmlrequest.HtmlParser;
import org.shareok.data.htmlrequest.HtmlRequest;
import org.shareok.data.sagedata.exceptions.EmptyFilePathException;
import org.shareok.data.sagedata.exceptions.EmptyJournalDataException;
import org.springframework.beans.BeansException;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Tao Zhao
 */
public abstract class SageJournalDataProcessorAbstract implements SageJournalDataProcessor {

    protected String id;
    protected Map data;
    protected String journalName;
    protected HtmlRequest htmlRequest;
    protected SageJournalData journalData;
    protected ArrayList<SageJournalData> sageJournalDataList;

    public Map getData() {
        return data;
    }

    public String getJournalName() {
        return journalName;
    }

    public SageJournalData getJournalData() {
        return journalData;
    }

    public ArrayList<SageJournalData> getSageJournalDataList() {
        return sageJournalDataList;
    }

    public String getId() {
        return id;
    }

    @Override
    public void setData(Map data) {
        this.data = data;
    }

    public void setJournalName(String journalName) {
        this.journalName = journalName;
    }

    public HtmlRequest getHtmlRequest() {
        return htmlRequest;
    }

    public void setHtmlRequest(HtmlRequest htmlRequest) {
        this.htmlRequest = htmlRequest;
    }

    public void setJournalData(SageJournalData journalData) {
        this.journalData = journalData;
    }

    public void setSageJournalDataList(ArrayList<SageJournalData> sageJournalDataList) {
        this.sageJournalDataList = sageJournalDataList;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void getOutput(String fileName) {
        try{
            if(null == fileName || "".equals(fileName)){
                throw new EmptyFilePathException("File path is NOT specified!");
            }
            
            // Every article needs to have an id from DOI
            setProcessorId();
            
            StringBuffer sb = getArticleResponse();
            if(null != sb && sb.length() != 0){
                processArticleResponse(sb.toString());
            }
            convertDataToJournalData();
            exportXmlByJournalData(fileName);
        }
        catch(Exception ex){
            Logger.getLogger(SageJournalDataProcessorAbstract.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String getArticleTitle() {
        String title = null;
        try {
            if(null == data)
                throw new EmptyJournalDataException("Journal data is empty!");
            title = (String)data.get("title");
            
        } catch (EmptyJournalDataException ex) {
            Logger.getLogger(SageJournalDataProcessorAbstract.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return title;
    }

    @Override
    public String getArticleVolume() {
        String volume = null;
        try {
            if(null == data)
                throw new EmptyJournalDataException("Journal data is empty!");
            volume = (String)data.get("volume");
            
        } catch (EmptyJournalDataException ex) {
            Logger.getLogger(SageJournalDataProcessorAbstract.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return volume;
    }

    @Override
    public String getArticleIssue() {
        String issue = null;
        try {
            if(null == data)
                throw new EmptyJournalDataException("Journal data is empty!");
            issue = (String)data.get("issue");
            
        } catch (EmptyJournalDataException ex) {
            Logger.getLogger(SageJournalDataProcessorAbstract.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return issue;
    }

    @Override
    public String getArticlePages() {
        String pages = null;
        try {
            if(null == data)
                throw new EmptyJournalDataException("Journal data is empty!");
            pages = (String)data.get("pages");
            if(null == pages || "".equals(pages)){
                
            }
            
        } catch (EmptyJournalDataException ex) {
            Logger.getLogger(SageJournalDataProcessorAbstract.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return pages;
    }

    @Override
    public String getArticleYear() {
        String year = null;
        try {
            if(null == data)
                throw new EmptyJournalDataException("Journal data is empty!");
            year = (String)data.get("year");
            
        } catch (EmptyJournalDataException ex) {
            Logger.getLogger(SageJournalDataProcessorAbstract.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return year;
    }

    @Override
    public String getArticleCitation() {
        String citation = null;
        try {
            if(null == data)
                throw new EmptyJournalDataException("Journal data is empty!");
            citation = (String)data.get("citation");
            
        } catch (EmptyJournalDataException ex) {
            Logger.getLogger(SageJournalDataProcessorAbstract.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return citation;
    }

    @Override
    public Date getArticlePubDate() {
        String date = null;
        try {
            if(null == data)
                throw new EmptyJournalDataException("Journal data is empty!");
            date = (String)data.get("pubdate");

            
        } catch (EmptyJournalDataException ex) {
            Logger.getLogger(SageJournalDataProcessorAbstract.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    @Override
    public String getArticleDoi() {
        String doi = null;
        try {
            if(null == data)
                throw new EmptyJournalDataException("Journal data is empty!");
            doi = (String)data.get("doi");
            
        } catch (EmptyJournalDataException ex) {
            Logger.getLogger(SageJournalDataProcessorAbstract.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return doi;
    }
    
    @Override
    public String getArticleAbstract(String html) {
        String[] abstracts = HtmlParser.metaDataParserWithElementProperty(html, "p", "id", "p-1");
        if(null != abstracts && abstracts.length > 0){
            return abstracts[0];
        }
        else{
            return null;
        }
    }
    
    @Override
    public String[] getArticleSubjects(String html) {
        String[] subjects = null;
        subjects = HtmlParser.metaDataParserWithElementProperty(html, "a", "class", "kwd-search");
        return subjects;
    }
    
    @Override
    public void convertDataToJournalData() {
        try{
            if(null == data){
                throw new EmptyJournalDataException("Journal data is empty!");
            }
            Set keys = data.keySet();
            Iterator it = keys.iterator();

            while(it.hasNext()){
                String key = (String)it.next();
                if(null != key){
                    String value = (String)data.get(key);
                    if(null == value){
                        continue;
                    }
                    if(key.equalsIgnoreCase("journal")){
                        journalData.setPublisher(value);
                    }
                    else if(key.equalsIgnoreCase("peerReviewNotes")){
                        journalData.setPeerReview(value);
                    }
                    else if(key.equalsIgnoreCase("doi")){
                        journalData.setDoi(value);
                    }
                    else if(key.equalsIgnoreCase("citation")){
                        journalData.setCitation(value);
                    }
                    else if(key.equalsIgnoreCase("subjects")){
                        String[] valStr = value.split(",");
                        journalData.setSubjects(valStr);
                    }
                    else if(key.equalsIgnoreCase("abstract")){
                        journalData.setAbstractText(value);
                    }
                    else if(key.equalsIgnoreCase("title")){
                        journalData.setTitle(value);
                    }
                    else if(key.equalsIgnoreCase("url")){
                        journalData.setUri(value);
                    }
                    else if(key.equalsIgnoreCase("pubdate")){
                        // The date string looks like this: mm/dd/yyyy, which needs to be converted to Data type
                        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                        Date date = formatter.parse(value);
                        journalData.setDateIssued(date);
                    }
                    else if(key.equalsIgnoreCase("authors")){
                        String[] valStr = value.split(",");
                        journalData.setAuthors(valStr);
                    }
                    else{
                        continue;
                    }
                }
            }
        }
        catch(Exception ex){
            Logger.getLogger(SageJournalDataProcessorAbstract.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    /**
     * Generate the metadata xml file
     * @param fileName 
     */
    public void exportXmlByJournalData(String fileName) {
                    
        try{
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("dublin_core");
            doc.appendChild(rootElement);

            // Add the type node:
            Element element = doc.createElement("dcvalue");
            element.appendChild(doc.createTextNode("Research Article"));
            rootElement.appendChild(element);
            
            Attr attr = doc.createAttribute("element");
            attr.setValue("type");
            element.setAttributeNode(attr);
            
            attr = doc.createAttribute("language");
            attr.setValue("en_US");
            element.setAttributeNode(attr);
            
            attr = doc.createAttribute("qualifier");
            attr.setValue("none");
            element.setAttributeNode(attr);
            
            // Add the abstract node:
            String abs = journalData.getAbstractText();
            if(null != abs){
                Element elementAbs = doc.createElement("dcvalue");
                elementAbs.appendChild(doc.createTextNode(abs));
                rootElement.appendChild(elementAbs);
                
                attr = doc.createAttribute("element");
                attr.setValue("description");
                elementAbs.setAttributeNode(attr);

                attr = doc.createAttribute("language");
                attr.setValue("en_US");
                elementAbs.setAttributeNode(attr);

                attr = doc.createAttribute("qualifier");
                attr.setValue("abstract");
                elementAbs.setAttributeNode(attr);
            }
            
            // Add the language node:
            String lang = journalData.getLanguage();
            if(null != lang){
                Element elementLang = doc.createElement("dcvalue");
                elementLang.appendChild(doc.createTextNode(lang));
                rootElement.appendChild(elementLang);

                attr = doc.createAttribute("element");
                attr.setValue("language");
                elementLang.setAttributeNode(attr);

                attr = doc.createAttribute("language");
                attr.setValue("en_US");
                elementLang.setAttributeNode(attr);

                attr = doc.createAttribute("qualifier");
                attr.setValue("iso");
                elementLang.setAttributeNode(attr);
            }
            
            // Add the title node:
            String tit = journalData.getTitle();
            if(null != tit){
                Element elementTitle = doc.createElement("dcvalue");
                elementTitle.appendChild(doc.createTextNode(tit));
                rootElement.appendChild(elementTitle);

                attr = doc.createAttribute("element");
                attr.setValue("title");
                elementTitle.setAttributeNode(attr);

                attr = doc.createAttribute("language");
                attr.setValue("en_US");
                elementTitle.setAttributeNode(attr);

                attr = doc.createAttribute("qualifier");
                attr.setValue("none");
                elementTitle.setAttributeNode(attr);
            }
            
            // Add the available date node:
//            Element elementAvailable = doc.createElement("dcvalue");
//            elementAvailable.appendChild(doc.createTextNode(getDateAvailable().toString()));
//            rootElement.appendChild(elementAvailable);
//            
//            attr = doc.createAttribute("element");
//            attr.setValue("date");
//            elementAvailable.setAttributeNode(attr);
//            
//            attr = doc.createAttribute("qualifier");
//            attr.setValue("available");
//            elementAvailable.setAttributeNode(attr);
            
            // Add the issued date node:
            Date issueDate = journalData.getDateIssued();
            if(null != issueDate){
                SimpleDateFormat format_issuedDate = new SimpleDateFormat ("yyyy-MM-dd"); 
                Element elementIssued = doc.createElement("dcvalue");
                elementIssued.appendChild(doc.createTextNode(format_issuedDate.format(issueDate)));
                rootElement.appendChild(elementIssued);

                attr = doc.createAttribute("element");
                attr.setValue("date");
                elementIssued.setAttributeNode(attr);

                attr = doc.createAttribute("qualifier");
                attr.setValue("issued");
                elementIssued.setAttributeNode(attr);
            }
            
            // Add the author nodes:
            String[] authorSet = journalData.getAuthors();
            if(null != authorSet && authorSet.length > 0){
                for(String author : authorSet){
                    Element elementAuthor = doc.createElement("dcvalue");
                    elementAuthor.appendChild(doc.createTextNode(author));
                    rootElement.appendChild(elementAuthor);

                    attr = doc.createAttribute("element");
                    attr.setValue("contributor");
                    elementAuthor.setAttributeNode(attr);

                    attr = doc.createAttribute("qualifier");
                    attr.setValue("author");
                    elementAuthor.setAttributeNode(attr);
                }
            }
            
            // Add the acknowledgements node:
            String ack = journalData.getAcknowledgements();
            if(null != ack){
                Element elementAck = doc.createElement("dcvalue");
                elementAck.appendChild(doc.createTextNode(ack));
                rootElement.appendChild(elementAck);

                attr = doc.createAttribute("element");
                attr.setValue("description");
                elementAck.setAttributeNode(attr);

                attr = doc.createAttribute("language");
                attr.setValue("en_US");
                elementAck.setAttributeNode(attr);

                attr = doc.createAttribute("qualifier");
                attr.setValue("none");
                elementAck.setAttributeNode(attr);
            }
            
            // Add the author contributions node:
            String contrib = journalData.getAuthorContributions();
            if(null != contrib){
                Element elementContribution = doc.createElement("dcvalue");
                elementContribution.appendChild(doc.createTextNode(contrib));
                rootElement.appendChild(elementContribution);

                attr = doc.createAttribute("element");
                attr.setValue("description");
                elementContribution.setAttributeNode(attr);

                attr = doc.createAttribute("language");
                attr.setValue("en_US");
                elementContribution.setAttributeNode(attr);

                attr = doc.createAttribute("qualifier");
                attr.setValue("none");
                elementContribution.setAttributeNode(attr);
            }
            
            // Add the publisher node:
            String puber = journalData.getPublisher();
            if(null != puber){
                Element elementPublisher = doc.createElement("dcvalue");
                elementPublisher.appendChild(doc.createTextNode(puber));
                rootElement.appendChild(elementPublisher);

                attr = doc.createAttribute("element");
                attr.setValue("publisher");
                elementPublisher.setAttributeNode(attr);

                attr = doc.createAttribute("qualifier");
                attr.setValue("none");
                elementPublisher.setAttributeNode(attr);
            }
            
            // Add the citation node:
            String cit = journalData.getCitation();
            if(null != cit){
                Element elementCitation = doc.createElement("dcvalue");
                elementCitation.appendChild(doc.createTextNode(cit));
                rootElement.appendChild(elementCitation);

                attr = doc.createAttribute("element");
                attr.setValue("identifier");
                elementCitation.setAttributeNode(attr);

                attr = doc.createAttribute("language");
                attr.setValue("en_US");
                elementCitation.setAttributeNode(attr);

                attr = doc.createAttribute("qualifier");
                attr.setValue("citation");
                elementCitation.setAttributeNode(attr);
            }
            
            // Add the rights node:
            String rit = journalData.getRights();
            if(null != rit){
                Element elementRights = doc.createElement("dcvalue");
                elementRights.appendChild(doc.createTextNode(rit));
                rootElement.appendChild(elementRights);

                attr = doc.createAttribute("element");
                attr.setValue("rights");
                elementRights.setAttributeNode(attr);

                attr = doc.createAttribute("qualifier");
                attr.setValue("none");
                elementRights.setAttributeNode(attr);
            }
            
            // Add the rights URI node:
            String ritUri = journalData.getRightsUri();
            if(null != ritUri){
                Element elementRightsUri = doc.createElement("dcvalue");
                elementRightsUri.appendChild(doc.createTextNode(ritUri));
                rootElement.appendChild(elementRightsUri);

                attr = doc.createAttribute("element");
                attr.setValue("rights");
                elementRightsUri.setAttributeNode(attr);

                attr = doc.createAttribute("qualifier");
                attr.setValue("uri");
                elementRightsUri.setAttributeNode(attr);
            }
            
            // Add the rights requestable node:
            Element elementRightsRequestable = doc.createElement("dcvalue");
            elementRightsRequestable.appendChild(doc.createTextNode(Boolean.toString(journalData.isRightsRequestable())));
            rootElement.appendChild(elementRightsRequestable);
            
            attr = doc.createAttribute("element");
            attr.setValue("rights");
            elementRightsRequestable.setAttributeNode(attr);
            
            attr = doc.createAttribute("language");
            attr.setValue("en_US");
            elementRightsRequestable.setAttributeNode(attr);
            
            attr = doc.createAttribute("qualifier");
            attr.setValue("requestable");
            elementRightsRequestable.setAttributeNode(attr);
            
            // Add the is part of node:
            String partOf = journalData.getIsPartOfSeries();
            if(null != partOf){
                Element elementIsPartOf = doc.createElement("dcvalue");
                elementIsPartOf.appendChild(doc.createTextNode(partOf));
                rootElement.appendChild(elementIsPartOf);

                attr = doc.createAttribute("element");
                attr.setValue("relation");
                elementIsPartOf.setAttributeNode(attr);

                attr = doc.createAttribute("qualifier");
                attr.setValue("ispartofseries");
                elementIsPartOf.setAttributeNode(attr);
            }
            
            // Add the relation uri node:
            String reUri = journalData.getRelationUri();
            if(null != reUri){
                Element elementRelationUri = doc.createElement("dcvalue");
                elementRelationUri.appendChild(doc.createTextNode(reUri));
                rootElement.appendChild(elementRelationUri);

                attr = doc.createAttribute("element");
                attr.setValue("relation");
                elementRelationUri.setAttributeNode(attr);

                attr = doc.createAttribute("qualifier");
                attr.setValue("uri");
                elementRelationUri.setAttributeNode(attr);
            }
            
            // Add the subject nodes:
            String[] subjectSet = journalData.getSubjects();
            if(null != subjectSet && subjectSet.length > 0){
                for(String subject : subjectSet){
                    Element elementSubject = doc.createElement("dcvalue");
                    elementSubject.appendChild(doc.createTextNode(subject));
                    rootElement.appendChild(elementSubject);

                    attr = doc.createAttribute("element");
                    attr.setValue("subject");
                    elementSubject.setAttributeNode(attr);

                    attr = doc.createAttribute("language");
                    attr.setValue("en_US");
                    elementSubject.setAttributeNode(attr);

                    attr = doc.createAttribute("qualifier");
                    attr.setValue("none");
                    elementSubject.setAttributeNode(attr);
                }
            }
            
            // Add the peerReview node:
            String review = journalData.getPeerReview();
            if(null != review){
                Element elementPeerReview = doc.createElement("dcvalue");
                elementPeerReview.appendChild(doc.createTextNode(review));
                rootElement.appendChild(elementPeerReview);

                attr = doc.createAttribute("element");
                attr.setValue("description");
                elementPeerReview.setAttributeNode(attr);

                attr = doc.createAttribute("language");
                attr.setValue("en_US");
                elementPeerReview.setAttributeNode(attr);

                attr = doc.createAttribute("qualifier");
                attr.setValue("peerreview");
                elementPeerReview.setAttributeNode(attr);
            }
            
            // Add the peer review notes node:
            String peer = journalData.getPeerReviewNotes();
            if(null != peer){
                Element elementPeerReviewNotes = doc.createElement("dcvalue");
                elementPeerReviewNotes.appendChild(doc.createTextNode(peer));
                rootElement.appendChild(elementPeerReviewNotes);

                attr = doc.createAttribute("element");
                attr.setValue("description");
                elementPeerReviewNotes.setAttributeNode(attr);

                attr = doc.createAttribute("language");
                attr.setValue("en_US");
                elementPeerReviewNotes.setAttributeNode(attr);

                attr = doc.createAttribute("qualifier");
                attr.setValue("peerreviewnotes");
                elementPeerReviewNotes.setAttributeNode(attr);
            }
            
            // Add the doi node:
            String doi = journalData.getDoi();
            if(null != doi){
                Element elementDoi = doc.createElement("dcvalue");
                elementDoi.appendChild(doc.createTextNode(doi));
                rootElement.appendChild(elementDoi);

                attr = doc.createAttribute("element");
                attr.setValue("identifier");
                elementDoi.setAttributeNode(attr);

                attr = doc.createAttribute("language");
                attr.setValue("en_US");
                elementDoi.setAttributeNode(attr);

                attr = doc.createAttribute("qualifier");
                attr.setValue("doi");
                elementDoi.setAttributeNode(attr);
            }
            
            
            // Generate the xml file:
            String id = getId();
            if(null == id || "".equals(id)){
                setProcessorId();
                id = getId();
            }
            String folderPath = fileName + "/" + id;
            File folder = new File(folderPath);
            if(!folder.exists()){
                if(folder.mkdir()){
                    System.out.print("The folder for loading article" + id + " has been created.\n");
                }
            }
            
            String filePath = folderPath + "/dublin_core.xml";
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filePath));
            
            String contentsFilePath = folderPath + "/contents";
            File file = new File(contentsFilePath);
            if(!file.exists()){
                file.createNewFile();
            }

            transformer.transform(source, result);
        }       
        catch (ParserConfigurationException | TransformerException pce) {
            pce.printStackTrace();System.exit(0);
        }
        catch(DOMException | BeansException e){
            e.printStackTrace();System.exit(0);
        } catch (IOException ex) {
            Logger.getLogger(SageJournalDataProcessorAbstract.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public String getFullTextLink() {
        String link = "";
        return link;
    }
}
