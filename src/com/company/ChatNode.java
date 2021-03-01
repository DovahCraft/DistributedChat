package com.company;

import com.company.messages.ChatMessage;
import com.company.messages.Message;
import com.company.messages.MessageType;
import com.company.messages.JoinMessage;
import com.company.workers.Listener;
import com.company.workers.Sender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.company.Utils.*;

/*
This is the core class that houses the receiver and sender classes in their threads. This represents one node in the
mesh topology of the distributed chat system.
 */
public class ChatNode {
    //Store participants list in a map
    public static ParticipantsMap participantsMap = new ParticipantsMap();
    //Create the current node and its information
    public static NodeInfo thisNode;
    public static final Object lock = new Object();

    public static void main(String[] args) {
        System.out.println("Node created");
        if (args.length != 2) {
            //If format is not correct inform the user of the correct format
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
            System.out.println("This node: " + ChatNode.thisNode.toString());
            //Add ourselves to our participants map once we initialize the IP.
            participantsMap.put(thisNode, true);
            //This will wait for user input and spawn sender when needed.
            handleUser();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    private static void handleUser() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        //init variables
        String input;
        String[] inputParts;
        String joiningIp;
        int joiningPort;
        Sender sender;
        boolean isInvalidCommand;
        try {
            //Execute our loop
            while (true) {
                //assign variables
                sender = null;
                isInvalidCommand = false;
                input = reader.readLine();
                inputParts = input.split(" ");
                switch (inputParts[0]) {
                    //Handle user calling JOIN  
                    case "JOIN" -> {
                        //Get the joining IP from command line
                        joiningIp = inputParts[1];
                        if (inputParts.length == 3 && isValidIpAddr(joiningIp) && isInt(inputParts[2])) {
                            //Assign the port from command line
                            joiningPort = Integer.parseInt(inputParts[2]);
                            //Create a sender of com.company.message type JOIN
                            sender = new Sender(
                                    new JoinMessage(MessageType.JOIN, ChatNode.thisNode, joiningIp, joiningPort));
                        } else {
                            isInvalidCommand = true;
                        }
                    }
                    //Handle user calling LEAVE  
                    case "LEAVE" -> {
                        if(participantsMap.size() > 1){
                            //Create a sender of com.company.message type LEAVE
                            sender = new Sender(new Message(MessageType.LEAVE, ChatNode.thisNode));
                        }
                        else {
                            //If user is alone it cannot leave
                            System.out.println("Size of particpants map is " + participantsMap.size());
                            System.out.println("Cannot leave yourself alone! Call QUIT to shut down the session.");
                        }
                    }
                      
                    //Handle user calling CHAT
                    case "CHAT" -> {
                        //Create new chat com.company.message and it's payload
                        ChatMessage chatMessage = new ChatMessage(MessageType.CHAT, ChatNode.thisNode,
                                input.split(" ", 2)[1]);
                        //Only create a new sender if the participants list has more than one node
                        if(participantsMap.size() > 1){
                            sender = new Sender(chatMessage);
                        }
                        System.out.println(chatMessage.toString());
                    }
                    //Handle user calling HELP  
                    case "HELP" -> printHelpMessage();
                        
                    //Handle user calling LIST  
                    case "LIST" -> {
                        //show list of current participants
                        synchronized (ChatNode.lock){
                            System.out.println(ChatNode.participantsMap.toString());
                        }
                    }
                       
                    //Handle user calling QUIT    
                    case "QUIT" -> System.exit(0);

                    //If command is from none of the above, mark command as invalid
                    default -> isInvalidCommand = true;

                }
                if (sender != null) {
                    new Thread(sender).start();
                }
                //Check if the current command is Valid
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
    
    //class that shows the valid commands 
    private static void printHelpMessage() {
        System.out.println("Commands:\n" +
                "JOIN <IPv4 Address> <Port Number>\n" +
                "CHAT <Message>\n" +
                "LEAVE\n" +
                "QUIT\n" +
                "HELP");
    }
}
