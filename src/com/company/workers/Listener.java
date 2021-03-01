package com.company.workers;

import com.company.ChatNode;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;

//Create a listener class 
public class Listener implements Runnable {
    //init variables
    Socket clientSocket;
    Integer port;
    //Assign Variables
    public Listener(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        //Outpout to console we are running a listener thread
        System.out.println("Running listener, checking for new connections to Node");
        //Create new listener socket
        try (ServerSocket listenerSocket = new ServerSocket(this.port)) {
            //Create lock for safety purposes
            synchronized (ChatNode.lock) {
                //Set chat node info and activate lock
                System.out.println();
                ChatNode.thisNode.setIp(Inet4Address.getLocalHost().getHostAddress());
                ChatNode.lock.notify();
            }
            
            //Create a new listener worker thread to listen for incoming nodes 
            while (true) {
                Thread listenerWorkThread = new Thread(new ListenerWorker(listenerSocket.accept()));
                listenerWorkThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
