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
    public static NodeInfo thisNode;

    public static void main(String[] args){
        System.out.println("Node created");
        if(args.length != 2){
            System.err.println("Parameter Format: <PORT> <LOGICAL NAME>");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        String logicalName = args[1];
        thisNode = new NodeInfo(port, logicalName);

        //Create receiver thread, passing it the server socket we create.
        try{
            System.out.println("Waiting for user input");
            Thread listenerThread = new Thread(new Listener(port));
            listenerThread.start();
            while(true){
                //Take in input

            }
        }

        catch(Exception e){
            System.out.println(e.getMessage());
        }

    }
}
