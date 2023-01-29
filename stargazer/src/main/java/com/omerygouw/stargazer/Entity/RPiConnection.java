package com.omerygouw.stargazer.Entity;

import com.omerygouw.stargazer.Controller.RPiCommunication;
import com.omerygouw.stargazer.Service.PiToWebBridgeService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.net.Socket;

public class RPiConnection extends Thread{
    private Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private PiToWebBridgeService piToWebBridgeService;
    private final String sessionId;

    public RPiConnection(Socket socket, PiToWebBridgeService piToWebBridgeService, RPiCommunication rPiCommunication, String sessionId) throws IOException {
        this.sessionId = sessionId;
        this.piToWebBridgeService = piToWebBridgeService;
        this.socket = socket;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
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
                continue;
            }

            if(receivedMessage.startsWith("Unsolicited:")){
                receivedMessage = receivedMessage.replaceAll("Unsolicited:", "");
                String[] split = receivedMessage.split("sessionId");
                receivedMessage = split[0];
                String id = split[1];
                UnsolicitedMessage message = new UnsolicitedMessage(receivedMessage);

                if(receivedMessage.startsWith("Bad Calibration:")){
                    piToWebBridgeService.warnBadCalibration(message, id);
                }
                else if(receivedMessage.startsWith("Bad Orientation:")){
                    piToWebBridgeService.warnBadOrientation(message, id);
                }
            }
        }
    }

    public String instructToPointToObject(AstronomicalObject astronomicalObject) throws IOException {
        String message = "{\"point\": {\"Azimuth\":" + astronomicalObject.getAzimuth() + ", \"Altitude:\":" + astronomicalObject.getAltitude() + "}}";
        writer.write(message);
        writer.flush();
        return reader.readLine();
    }

    public String instructToTurnOnLaser() throws IOException {
        String message = "Laser On";
        writer.write(message);
        writer.flush();
        return reader.readLine();
    }

    public String instructToTurnOffLaser() throws IOException {
        String message = "Laser Off";
        writer.write(message);
        writer.flush();
        return reader.readLine();
    }

    public String instructToResetLaserPosition() throws IOException {
        String message = "reset";
        writer.write(message);
        writer.flush();
        return reader.readLine();
    }

    public String changeSessionId(String newSessionId) throws IOException {
        writer.write("New Session Id: " + newSessionId);
        writer.flush();
        return reader.readLine();
    }
}
