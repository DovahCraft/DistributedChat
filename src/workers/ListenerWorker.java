package workers;

import com.company.ChatNode;

import com.company.NodeInfo;
import com.company.Utils;
import message.*;
import org.w3c.dom.Node;

import java.io.*;
import java.net.Socket;
//Class for listening for connections
public class ListenerWorker implements Runnable {
    //Init Variables
    final Socket chatSocket;
    final ObjectOutputStream outputStream;
    final ObjectInputStream inputStream;
    
    //Assign variables
    public ListenerWorker(Socket inputSocket) throws IOException {
        this.chatSocket = inputSocket;
        outputStream = new ObjectOutputStream(chatSocket.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(chatSocket.getInputStream());
    }

    @Override
    //function to handle our messages 
    public void run() {
        System.out.println("Recieving message!");
        try (
                chatSocket;
                outputStream;
                inputStream
        ) {
            //Notify the user of a new connection
            synchronized (System.out) {
                System.out.println("Chat node connected!");
            }
            //Get info from connection
            Object fromClient = inputStream.readObject();
            System.out.println("Reading object");
            //set variables
            Message clientMessage = (Message) fromClient;
            checkFlagType(clientMessage);
        } catch (Exception e) {
            System.out.println("Couldn't open client socket in ListenerWorker!");
            e.printStackTrace();
        }
    }
    
    //function to check what kind of message type we have
    public void checkFlagType(Message message) {
        switch (message.messageType) {
            case JOIN -> handleJoin((JoinMessage) message);
            case LEAVE -> handleLeave(message);
            case CHAT -> handleChat((ChatMessage) message);
            default -> System.out.print("INVALID MESSAGE TYPE");
        }
    }

    //function for handline joining node
    public void handleJoin(JoinMessage message) {
        try {
            //set variables
            Boolean isIn = false;
            String socketIP = chatSocket.getInetAddress().getHostAddress();
            //Integer socketPort = chatSocket.getPort();
            System.out.println("Message IP: " + message.source.ip + " Message Port: " + message.source.port + " SocketIP: " + socketIP);
            if (message.source.ip.equals(socketIP)) {
                System.out.println("Running condition in handlejoin");
                synchronized (ChatNode.lock) {
                    outputStream.writeObject(ChatNode.participantsMap);
                }
                outputStream.flush();
                Utils.sendToAll(message);
            }
            synchronized (ChatNode.lock) {
                //Check if node is in particpantsMap
                for (NodeInfo node : ChatNode.participantsMap.keySet()){
                    if (node.equals(message.source)) {
                        isIn = true;
                        break;
                    }
                }
            }
            //Insert current node into particpantsMap
            synchronized (ChatNode.lock) {
                if(!isIn) {
                    ChatNode.participantsMap.put(message.source, true);
                }
            }

        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
        }
        System.out.println("Finished if statement");

    }
    
    //Handle the leave message from a node
    public void handleLeave(Message message) {
        //set variables
        NodeInfo toRemove = (NodeInfo) message.source;
        NodeInfo foundNode = null;
        synchronized (ChatNode.lock) {
        //Iterate through the participantsMap
        for (NodeInfo node : ChatNode.participantsMap.keySet()){
                //If the the node is in the map set it to foundNode
                if (node.equals(toRemove)) {
                    foundNode = node;
                }
        }
            
        //remove the found Node
        ChatNode.participantsMap.remove(foundNode);
        }

    }
    
    //function to print out the chat message
    public void handleChat(ChatMessage message) {
        System.out.println(message.toString());

    }
}
