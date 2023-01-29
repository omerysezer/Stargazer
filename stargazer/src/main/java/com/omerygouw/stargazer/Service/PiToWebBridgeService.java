package com.omerygouw.stargazer.Service;

import com.omerygouw.stargazer.Entity.UnsolicitedMessage;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class PiToWebBridgeService {
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    public void warnBadCalibration(UnsolicitedMessage message, String sessionId){
        simpMessagingTemplate.convertAndSend("/user/queue/chat-" + sessionId, message);
    }

    public void warnBadOrientation(UnsolicitedMessage message, String sessionId){
        simpMessagingTemplate.convertAndSend("/user/queue/chat-" + sessionId, message);
    }
}