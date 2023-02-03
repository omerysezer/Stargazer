package com.omerygouw.stargazer.Entity;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.omerygouw.stargazer.Controller.RPiCommunication;
import com.omerygouw.stargazer.DTO.*;
import com.omerygouw.stargazer.Service.PiToWebBridgeService;
import lombok.Builder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.HashMap;
import java.util.Map;

public class RPiConnection extends Thread{
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final PiToWebBridgeService piToWebBridgeService;
    private final RPiCommunication rPiCommunication;
    private String sessionId;
    private final Map<String, FromPiToServerMessage> awaitingResponses;

    @Builder
    public RPiConnection(PiToWebBridgeService piToWebBridgeService, RPiCommunication rPiCommunication, BufferedReader reader, BufferedWriter writer, String sessionId) {
        this.piToWebBridgeService = piToWebBridgeService;
        this.rPiCommunication = rPiCommunication;
        this.reader = reader;
        this.writer = writer;
        this.sessionId = sessionId;
        this.awaitingResponses = new HashMap<>();
        this.start();
    }

    public void run(){
        String receivedMessage;

        while(true){
            try {
                receivedMessage = reader.readLine();
            }
            catch (Exception e){
                throw new RuntimeException(e);
            }

            if(receivedMessage == null){
                rPiCommunication.handleLostConnection(sessionId);
                return;
            }

            FromPiToServerMessage messageFromPi = null;
            try{
                messageFromPi = new Gson().fromJson(receivedMessage, FromPiToServerMessage.class);
            } catch (JsonSyntaxException e){
                FromServerToPiMessage errorResponse = FromServerToPiMessage.builder()
                        .instruction("FIX_INVALID_MESSAGE")
                        .instructionData(receivedMessage)
                        .instructionId(String.valueOf(System.nanoTime()))
                        .build();
                try {
                    write(new Gson().toJson(errorResponse));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            if(messageFromPi == null){
                continue;
            }

            // if there is a null reference tied to the same id as messageFromPi
            // a thread is waiting for a response to a message sent to the pi with the same ID
            // this wakes that thread and gives it the response it needs
            if(awaitingResponses.containsKey(messageFromPi.instructionId())){
                synchronized (awaitingResponses){
                    awaitingResponses.put(messageFromPi.instructionId(), messageFromPi);
                    awaitingResponses.notifyAll();
                }
                continue;
            }

            Message messageForClient = null;
            switch (messageFromPi.messageType()) {
                case "CALIBRATION_WARNING" -> {
                    messageForClient = new Message(Status.CALIBRATION_WARNING, "", "");
                }
                case "ORIENTATION_WARNING" -> {
                    messageForClient = new Message(Status.ORIENTATION_WARNING, "", "");
                }
                case "LEVEL_WARNING" -> {
                    messageForClient = new Message(Status.LEVEL_WARNING, "", "");
                }
                case "CALIBRATION_OK" -> {
                    messageForClient = new Message(Status.CALIBRATION_OK, "", "");
                }
                case "LEVEL_OK" -> {
                    messageForClient = new Message(Status.LEVEL_OK, "", "");
                }
                case "ORIENTATION_OK" -> {
                    messageForClient = new Message(Status.ORIENTATION_OK, "", "");
                }
            }

            if(messageForClient != null){
                piToWebBridgeService.sendMessageToClient(messageForClient, messageFromPi.sessionId());
            }
        }
    }

    public FromPiToServerMessage instructToPointToObject(AstronomicalObject astronomicalObject) throws IOException {
        ObjectCoordinates coordinates = ObjectCoordinates.builder()
                .azimuth(astronomicalObject.getAzimuth())
                .altitude(astronomicalObject.getAltitude())
                .build();

        String jsonCoordinatesString = new Gson().toJson(coordinates);

        FromServerToPiMessage message = FromServerToPiMessage.builder()
                .instruction("POINT")
                .instructionData(jsonCoordinatesString)
                .instructionId(String.valueOf(System.nanoTime()))
                .build();

       return sendMessageToPiAndGetResponse(message);
    }

    public FromPiToServerMessage instructToTurnOnLaser() throws IOException {
        FromServerToPiMessage message = FromServerToPiMessage.builder()
                .instruction("LASER_ON")
                .instructionData("")
                .instructionId(String.valueOf(System.nanoTime()))
                .build();

        return sendMessageToPiAndGetResponse(message);
    }

    public FromPiToServerMessage instructToTurnOffLaser() throws IOException {
        FromServerToPiMessage message = FromServerToPiMessage.builder()
        .instruction("LASER_OFF")
        .instructionData("")
        .instructionId(String.valueOf(System.nanoTime()))
        .build();

        return sendMessageToPiAndGetResponse(message);
    }


    public FromPiToServerMessage changeSessionId(String newSessionId) throws IOException {
        FromServerToPiMessage message = FromServerToPiMessage.builder()
            .instruction("CHANGE_SESSION")
            .instructionData(newSessionId)
            .instructionId(String.valueOf(System.nanoTime()))
            .build();

        this.sessionId = newSessionId;

        return sendMessageToPiAndGetResponse(message);
    }

    private FromPiToServerMessage sendMessageToPiAndGetResponse(FromServerToPiMessage message) throws IOException {
        write(new Gson().toJson(message));
        awaitingResponses.put(message.instructionId(), null);

        // this waits for the synchronizer object to have a response inserted into in the run() function
        // other methods of receiving responses lead to race conditions and other issues
        synchronized (awaitingResponses){
            try{
                while(awaitingResponses.get(message.instructionId()) == null){
                    awaitingResponses.wait();
                }
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }

        FromPiToServerMessage response = awaitingResponses.get(message.instructionId());
        awaitingResponses.remove(message.instructionId());
        return response;
    }

    private void write(String message) throws IOException {
        writer.write(message + "\n");
        writer.flush();
    }
}
