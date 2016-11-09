/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.commons;

import org.shareok.data.commons.recipes.BookRecipe;
import org.shareok.data.commons.recipes.PageRecipe;
import org.shareok.data.commons.uuid.S3BookUUIDGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Tao Zhao
 */
public class CommonsUtil {
    public static BookRecipe getBookRecipeInstance(){
        ApplicationContext context = new ClassPathXmlApplicationContext("commonsContext.xml");
        return (BookRecipe) context.getBean("bookRecipe");
    }
    
    public static PageRecipe getPageRecipeInstance(){
        ApplicationContext context = new ClassPathXmlApplicationContext("commonsContext.xml");
        return (PageRecipe) context.getBean("pageRecipe");
    }
    
    public static S3BookUUIDGenerator getS3BookUUIDGeneratorInstance(){
        ApplicationContext context = new ClassPathXmlApplicationContext("commonsContext.xml");
        return (S3BookUUIDGenerator) context.getBean("s3BookUUIDGenerator");
    }
}
