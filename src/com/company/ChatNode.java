package com.company;

import message.ChatMessage;
import message.Message;
import message.MessageType;
import message.JoinMessage;
import workers.Listener;
import workers.Sender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Map;

import static com.company.Utils.*;

/*
This is the core class that houses the receiver and sender classes in their threads. This represents one node in the
mesh topology of the distributed chat system.
 */
public class ChatNode {
    public static ParticipantsMap participantsMap = new ParticipantsMap();
    public static NodeInfo thisNode;
    public static final Object lock = new Object();

    public static void main(String[] args) {
        System.out.println("Node created");
        if (args.length != 2) {
            System.err.println("Parameter Format: <PORT NUMBER> <LOGICAL NAME>");
            System.exit(1);
        }
        //Get our port and logical name for the command line
        int port = Integer.parseInt(args[0]);
        String logicalName = args[1];
        thisNode = new NodeInfo(port, logicalName);

        //Create receiver thread, passing it the server socket we create.
        try {
            //System.out.println("Waiting for user input\nType HELP for help");
            Thread listenerThread = new Thread(new Listener(port));
            listenerThread.start();
            synchronized (ChatNode.lock){
                ChatNode.lock.wait();
            }

            //Add ourselves to our participants map once we initialize the IP.
            participantsMap.put(thisNode, true);
            //This will wait for user input and spawn sender when needed.
            handleUser();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    private static void handleUser() {
        System.out.println("HandleUser Starting");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input;
        String[] inputParts;
        String joiningIp;
        int joiningPort;
        Sender sender;
        boolean isInvalidCommand;
        try {
            while (true) {
                sender = null;
                isInvalidCommand = false;
                input = reader.readLine();
                inputParts = input.split(" ");
                switch (inputParts[0]) {
                    case "JOIN" -> {
                        joiningIp = inputParts[1];
                        if (isValidIpAddr(joiningIp) && isInt(inputParts[2])) {
                            joiningPort = Integer.parseInt(inputParts[2]);
                            sender = new Sender(
                                    new JoinMessage(MessageType.JOIN, ChatNode.thisNode, joiningIp, joiningPort));
                        } else {
                            isInvalidCommand = true;
                        }
                    }
                    case "LEAVE" -> {
                        if(participantsMap.size() > 1){
                            sender = new Sender(new Message(MessageType.LEAVE, ChatNode.thisNode));
                        }
                        else{
                            System.out.println("Size of particpantsmap " + participantsMap.size());
                            System.out.println("Cannot leave yourself alone! Call QUIT to shut down the session.");
                        }
                    }

                    case "CHAT" -> {
                        ChatMessage chatMessage = new ChatMessage(MessageType.CHAT, ChatNode.thisNode,
                                input.split(" ", 2)[1]);
                        if(participantsMap.size() > 1){
                            sender = new Sender(chatMessage);
                        }
                        System.out.println(chatMessage.toString());
                    }

                    case "HELP" -> printHelpMessage();

                    case "QUIT" -> System.exit(0);

                    //If command is from none of the above, mark command as invalid
                    default -> isInvalidCommand = true;

                }
                if (sender != null) {
                    new Thread(sender).start();
                }
                if (isInvalidCommand) {
                    System.err.println("Invalid Command");
                    printHelpMessage();
                }
            }
        } catch (IOException e) {
            System.err.println("There was trouble reading your input.");
            System.err.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }

    private static void printHelpMessage() {
        System.out.println("Commands:\n" +
                "JOIN <IPv4 Address> <Port Number>\n" +
                "CHAT <Message>\n" +
                "LEAVE\n" +
                "QUIT\n" +
                "HELP");
    }
}
