package com.omerygouw.stargazer.Entity;

import com.omerygouw.stargazer.Controller.RPiCommunication;
import com.omerygouw.stargazer.DTO.Message;
import com.omerygouw.stargazer.DTO.Status;
import com.omerygouw.stargazer.Service.PiToWebBridgeService;

import java.io.*;
import java.net.Socket;

public class RPiConnection extends Thread{
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final PiToWebBridgeService piToWebBridgeService;
    private final RPiCommunication rPiCommunication;
    private final Socket socket;
    private String sessionId;
    private boolean isConnected;

    public RPiConnection(Socket socket, PiToWebBridgeService piToWebBridgeService, RPiCommunication rPiCommunication) throws IOException {
        this.piToWebBridgeService = piToWebBridgeService;
        this.rPiCommunication = rPiCommunication;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.socket = socket;
        this.isConnected = true;
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

            if(receivedMessage.startsWith("Unsolicited:")){
                receivedMessage = receivedMessage.replaceAll("Unsolicited:", "");
                String[] split = receivedMessage.split("sessionId:");
                String warning = split[0];
                String id = split[1];
                sessionId = id;
                Message message;

                if(receivedMessage.startsWith("Bad Calibration:")){
                    message = new Message(Status.CALIBRATION_WARNING, warning);
                    piToWebBridgeService.warnBadCalibration(message, id);
                }
                else if(receivedMessage.startsWith("Bad Orientation:")){
                    message = new Message(Status.ORIENTATION_WARNING, warning);
                    piToWebBridgeService.warnBadOrientation(message, id);
                }
            }
        }
    }

    private void throwErrorIfNotConnected(){
        if(!isConnected){
            throw new RuntimeException("Not connected to Raspberry Pi.");
        }
    }
    public String instructToPointToObject(AstronomicalObject astronomicalObject) throws IOException {
        throwErrorIfNotConnected();
        String message = "{\"point\": {\"Azimuth\":" + astronomicalObject.getAzimuth() + ", \"Altitude:\":" + astronomicalObject.getAltitude() + "}}";
        writer.write(message);
        writer.flush();
        return reader.readLine();
    }

    public String instructToTurnOnLaser() throws IOException {
        throwErrorIfNotConnected();
        String message = "Laser On";
        writer.write(message);
        writer.flush();
        return reader.readLine();
    }

    public String instructToTurnOffLaser() throws IOException {
        throwErrorIfNotConnected();
        String message = "Laser Off";
        writer.write(message);
        writer.flush();
        return reader.readLine();
    }


    public String changeSessionId(String newSessionId) throws IOException {
        throwErrorIfNotConnected();
        sessionId = newSessionId;
        String message = "New Session Id: " + newSessionId;
        writer.write(message);
        writer.flush();
        return reader.readLine();
    }
}
