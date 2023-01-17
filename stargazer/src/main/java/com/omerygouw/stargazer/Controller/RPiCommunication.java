package com.omerygouw.stargazer.Controller;

import com.omerygouw.stargazer.Entity.AstronomicalObject;
import org.springframework.stereotype.Controller;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLOutput;

@Controller
public class RPiCommunication extends Thread {
    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    public RPiCommunication(){
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

                if(recievedMessage.startsWith("Bad Calibration:")){
                    String calibrationLevels = recievedMessage.replaceAll("Bad Calibration:", "");
                    System.out.println("Call bridge service bad calibration function");
                }
                else if(recievedMessage.startsWith("Bad Orientation:")){
                    System.out.println("Call bridge service bad orientation function");
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
        }
        catch (Exception e){
            throw new RuntimeException("Could not establish a socket connection with raspberry pi");
        }
    }

    public String instructToPointToObject(AstronomicalObject astronomicalObject) throws IOException {
        String message = "{\"point\": {\"Azimuth\":" + astronomicalObject.getAzimuth() + ", \"Altitude:\":" + astronomicalObject.getAltitude() + "}}";
        writer.write(message);
        return reader.readLine();
    }

    public String instructToTurnOnLaser() throws IOException {
        String message = "Laser On";
        writer.write(message);
        return reader.readLine();
    }

    public String stopLaser() throws IOException {
        String message = "Laser Off";
        writer.write(message);
        return reader.readLine();
    }

    public String reset() throws IOException {
        String message = "reset";
        writer.write(message);
        return reader.readLine();
    }

    public boolean isTheRPiConnected(){
        return false;
    }
}

