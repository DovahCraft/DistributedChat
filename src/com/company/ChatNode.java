package com.company;

import workers.Listener;
import org.w3c.dom.Node;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Hashtable;
import java.util.Map;
/*
This is the core class that houses the receiver and sender classes in their threads. This represents one node in the
mesh topology of the distributed chat system.
 */
public class ChatNode {
    static Map<NodeInfo, Boolean> participantsMap;
    static NodeInfo thisNode;

    public static void main(String[] args){
        System.out.println("Node created");
        if(args.length != 2){
            System.out.println("Parameter Format: <PORT> <LOGICAL NAME>");
        }
        int port = Integer.parseInt(args[0]);
        String logicalName = args[1];


        //Listen for input
        //Create sender thread on enter pressed with valid message format.
        //Create receiver thread, passing it the server socket we create.
        try(ServerSocket listenerSocket = new ServerSocket(port)){
            thisNode = new NodeInfo(listenerSocket.getLocalSocketAddress().toString(), port, logicalName);
            Thread listenerThread = new Thread(new Listener(listenerSocket));
            listenerThread.start();

        }

        catch(IOException e){
            System.out.println(e.getMessage());
        }

    }
}
