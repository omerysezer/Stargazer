package com.omerygouw.stargazer.Controller;

import com.omerygouw.stargazer.Entity.AstronomicalObject;
import com.omerygouw.stargazer.Entity.UnsolicitedMessage;
import com.omerygouw.stargazer.Service.PiToWebBridgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


@Controller
public class RPiCommunication extends Thread {
    @Autowired
    PiToWebBridgeService piToWebBridgeService;

    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private boolean rPiIsConnected;

    public RPiCommunication(){
        rPiIsConnected = false;
        this.start();
    }

    @Override
    public void run(){
        try {
            initializeSocket();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while(true){
            String recievedMessage;

            try {
                recievedMessage = reader.readLine();
            }
            catch (Exception e){
                throw new RuntimeException(e);
            }

            if(recievedMessage == null){
                continue;
            }

            if(recievedMessage.startsWith("Unsolicited:")){
                recievedMessage = recievedMessage.replaceAll("Unsolicited:", "");
                UnsolicitedMessage message = new UnsolicitedMessage(recievedMessage);

                if(recievedMessage.startsWith("Bad Calibration:")){
                    piToWebBridgeService.warnBadCalibration(message);
                }
                else if(recievedMessage.startsWith("Bad Orientation:")){
                    piToWebBridgeService.warnBadOrientation(message);
                }
            }
        }
    }

    private void initializeSocket() throws IOException {
        try{
            serverSocket = new ServerSocket(5000);
            socket = serverSocket.accept();
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            rPiIsConnected = true;
        }
        catch (Exception e){
            throw new RuntimeException("Could not establish a socket connection with raspberry pi");
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

    public boolean theRaspberryPiIsConnected(){
        return rPiIsConnected;
    }
}

