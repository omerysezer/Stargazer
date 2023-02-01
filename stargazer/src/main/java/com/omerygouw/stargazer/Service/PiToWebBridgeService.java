package com.omerygouw.stargazer.Service;

import com.omerygouw.stargazer.DTO.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class PiToWebBridgeService {
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    public void warnBadCalibration(Message message, String sessionId){
        simpMessagingTemplate.convertAndSend("/user/queue/chat-" + sessionId, message);
    }

    public void warnBadOrientation(Message message, String sessionId){
        simpMessagingTemplate.convertAndSend("/user/queue/chat-" + sessionId, message);
    }
}