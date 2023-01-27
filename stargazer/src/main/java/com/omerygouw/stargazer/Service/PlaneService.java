package com.omerygouw.stargazer.Service;

import org.springframework.stereotype.Service;

@Service
public class PlaneService extends Thread{
    public PlaneService(){
        this.start();
    }

    public void run(){
        // stuff
    }
}
