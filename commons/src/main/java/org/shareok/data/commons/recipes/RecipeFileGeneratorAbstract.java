/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.commons.recipes;

import exceptions.EmptyCsvDataException;
import org.shareok.data.commons.uuid.ObjectUUIDGenerator;
import java.util.Map;
import org.shareok.data.documentProcessor.CsvHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Tao Zhao
 */
public abstract class RecipeFileGeneratorAbstract implements RecipeFileGenerator{

    protected CsvHandler csvHandler;
    protected ObjectUUIDGenerator generator;

    public CsvHandler getCsvHandler() {
        return csvHandler;
    }

    public ObjectUUIDGenerator getGenerator() {
        return generator;
    }

    public void setGenerator(ObjectUUIDGenerator generator) {
        this.generator = generator;
    }

    @Autowired
    public void setCsvHandler(CsvHandler csvHandler) {
        this.csvHandler = csvHandler;
    }
    
    /**
     * Generate the JSON string from a CSV input file
     * Format of the CSV file can be seen in the resource folder
     * 
     * @param inputCsv
     * @return 
     */
    @Override
    public String getRecipeJasonString(String inputCsv) {
        try{
            csvHandler.setFileName(inputCsv);
            csvHandler.readData();
            Map data = csvHandler.getData();
            return getJsonByInputData(data);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
//        Iterator it = data.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry pair = (Map.Entry)it.next();
//            System.out.println(pair.getKey() + " = " + pair.getValue());
//            it.remove(); 
//        }
        return null;
    }

    /**
     * Generates JSON string from csv file data
     * 
     * @param data extracted from the input csv file
     * @return json string
     * @throws exceptions.EmptyCsvDataException
     */
    public abstract String getJsonByInputData(Map data) throws EmptyCsvDataException;
}
