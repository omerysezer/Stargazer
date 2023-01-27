package com.omerygouw.stargazer.Service;

import com.omerygouw.stargazer.Entity.UnsolicitedMessage;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class PiToWebBridgeService {
//    @Autowired
//    SimpMessagingTemplate simpMessagingTemplate;

    // simpMessagingTemplate.convertAndSend("url to send to", "object to send");

    public void warnBadCalibration(UnsolicitedMessage message){
//        simpMessagingTemplate.convertAndSend("/topic/response", message);
    }

    public void warnBadOrientation(UnsolicitedMessage message){
//        simpMessagingTemplate.convertAndSend("/topic/response", message);
    }
}