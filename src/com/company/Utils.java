package com.company;

import message.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;

//Functions used 
public class Utils {
    public static boolean isValidIpAddr(String ipAddr) {
        String[] frags = ipAddr.split("\\.");
        return frags.length == 4 && Arrays.stream(frags).allMatch(Utils::isInt);
    }
    
    
    //Checks if input str is an int
    public static boolean isInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException n) {
            return false;
        }
    }
    
    //Sends messages out to all the
    public static Boolean sendToAll(Message message) throws IOException {
        //Init Variables
        Socket socket;
        ObjectOutputStream out;
        //Lock for safety purposes
        synchronized (ChatNode.lock) {
            System.out.println(ChatNode.participantsMap.toString());
            //Loop thorugh the hash map
            for (NodeInfo node : ChatNode.participantsMap.keySet()) {
                try {
                    if (!node.equals(ChatNode.thisNode)) {
                        //Create new socket
                        socket = new Socket(node.ip, node.port);
                        //Message to send 
                        out = new ObjectOutputStream(socket.getOutputStream());
                        out.writeObject(message);
                        out.flush();
                        out.close();
                        socket.close();
                    }
                } catch (Exception e) {
                    return false;
                }
            }
            return true;
        }
    }
}
