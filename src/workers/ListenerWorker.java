package workers;

import com.company.ChatNode;

import com.company.NodeInfo;
import com.company.Utils;
import message.*;
import org.w3c.dom.Node;

import java.io.*;
import java.net.Socket;

public class ListenerWorker implements Runnable {
    //init variables
    final Socket chatSocket;
    final ObjectOutputStream outputStream;
    final ObjectInputStream inputStream;
    
    //Set Variables
    public ListenerWorker(Socket inputSocket) throws IOException {
        this.chatSocket = inputSocket;
        outputStream = new ObjectOutputStream(chatSocket.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(chatSocket.getInputStream());
    }
    
    //Fucntion for when a node connects
    @Override
    public void run() {
        System.out.println("Recieving message!");
        try (
                chatSocket;
                outputStream;
                inputStream
        ) {
            //verify connection
            synchronized (System.out) {
                System.out.println("Chat node connected!");
            }
            //Get message object from new node
            Object fromClient = inputStream.readObject();
            System.out.println("Reading object");
            //Create mesasge from message object
            Message clientMessage = (Message) fromClient;
            //Check the type of message
            checkFlagType(clientMessage);
        } catch (Exception e) {
            System.out.println("Couldn't open client socket in ListenerWorker!");
            e.printStackTrace();
        }
    }
    
    //Function for checking our message type
    public void checkFlagType(Message message) {
        switch (message.messageType) {
            //Handle every type of message type
            case JOIN -> handleJoin((JoinMessage) message);
            case LEAVE -> handleLeave(message);
            case CHAT -> handleChat((ChatMessage) message);
            //if not valid message type, error message
            default -> System.out.print("INVALID MESSAGE TYPE");
        }
    }
    //Function to handle message of type join
    public void handleJoin(JoinMessage message) {
        try {
            //init Variables
            Boolean isIn = false;
            String socketIP = chatSocket.getInetAddress().getHostAddress();
            //Integer socketPort = chatSocket.getPort();
            //print current info of the joining node 
            System.out.println("Message IP: " + message.source.ip + " Message Port: " + message.source.port + " SocketIP: " + socketIP);
            //Handle join
            if (message.source.ip.equals(socketIP)) {
                System.out.println("Running condition in handlejoin");
                synchronized (ChatNode.lock) {
                    outputStream.writeObject(ChatNode.participantsMap);
                }
                //Send Join message to the current participants map
                outputStream.flush();
                Utils.sendToAll(message);
            }
            synchronized (ChatNode.lock) {
                //Verify node information
                for (NodeInfo node : ChatNode.participantsMap.keySet()){
                    if (node.equals(message.source)) {
                        isIn = true;
                        break;
                    }
                }
            }
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
    
    //Function to handle a leave message
    public void handleLeave(Message message) {
        NodeInfo dummy = new NodeInfo(8080, "CB");
        dummy.setIp("192.168.68.146");
        //set information we want to remove
        NodeInfo toRemove = (NodeInfo) message.source;
        synchronized (ChatNode.lock) {
        //If information is in participants map, remove 
        for (NodeInfo node : ChatNode.participantsMap.keySet()){
                if (node.equals(toRemove)) {
                    ChatNode.participantsMap.remove(node);
                }
        }
        }

    }
    
    //Handle chat message
    public void handleChat(ChatMessage message) {
        //print message
        System.out.println(message.toString());
    }
}
