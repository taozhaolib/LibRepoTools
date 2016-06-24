/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.redis.job;

import java.util.LinkedList;

/**
 * JobQueue name would be the name of the server together with job type
 * @author Tao Zhao
 */
public class JobQueue {
    private String name;
    private LinkedList<String> queue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedList<String> getQueue() {
        return queue;
    }

    public void setQueue(LinkedList<String> queue) {
        this.queue = queue;
    }
    
}
