package com.company;

import message.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;

//Class containing Utilities used
public class Utils {
    //Chceks if the current IP is valid
    public static boolean isValidIpAddr(String ipAddr) {
        String[] frags = ipAddr.split("\\.");
        //Make sure the IP is of correct length
        return frags.length == 4 && Arrays.stream(frags).allMatch(Utils::isInt);
    }
    
    //Check if input is an Int
    public static boolean isInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException n) {
            return false;
        }
    }
    //Util function used for sending messages to all users in the participants list
    public static Boolean sendToAll(Message message) throws IOException {
        //init variables
        Socket socket;
        ObjectOutputStream out;
        //Create a lock for safety purposes
        synchronized (ChatNode.lock) {
            //Print given list of participants
            System.out.println(ChatNode.participantsMap.toString());
            for (NodeInfo node : ChatNode.participantsMap.keySet()) {
                try {
                    //
                    if (!node.equals(ChatNode.thisNode)) {
                        socket = new Socket(node.ip, node.port);
                        //Check if socket is not connected
                        while (!socket.isConnected()) {
                            //if not connected close current socket
                            socket.close();
                            socket = new Socket(node.ip, node.port);
                        }
                        //Get message to send 
                        out = new ObjectOutputStream(socket.getOutputStream());
                        //Create a message object containing our created message
                        out.writeObject(message);
                        //close socket and message.
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
