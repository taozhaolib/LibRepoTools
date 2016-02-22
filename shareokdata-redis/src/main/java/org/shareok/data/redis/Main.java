/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.redis;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Tao Zhao
 */
public class Main {
    public static void main(String[] args){
        ApplicationContext context = new ClassPathXmlApplicationContext("redisContext.xml");
        UserRedisImpl example = (UserRedisImpl) context.getBean("redisExample");
//        example.addLink("80", "OK");
        example.test2();
        System.out.println("Redis template is working!");
    }
}
