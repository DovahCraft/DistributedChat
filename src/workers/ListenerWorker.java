package workers;

import com.company.ChatNode;

import com.company.NodeInfo;
import com.company.Utils;
import message.*;
import org.w3c.dom.Node;

import java.io.*;
import java.net.Socket;

public class ListenerWorker implements Runnable {
    final Socket chatSocket;
    final ObjectOutputStream outputStream;
    final ObjectInputStream inputStream;

    public ListenerWorker(Socket inputSocket) throws IOException {
        this.chatSocket = inputSocket;
        outputStream = new ObjectOutputStream(chatSocket.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(chatSocket.getInputStream());
    }

    @Override
    public void run() {
        System.out.println("Recieving message!");
        try (
                chatSocket;
                outputStream;
                inputStream
        ) {
            synchronized (System.out) {
                System.out.println("Chat node connected!");
            }
            Object fromClient = inputStream.readObject();
            System.out.println("Reading object");
            Message clientMessage = (Message) fromClient;
            checkFlagType(clientMessage);
        } catch (Exception e) {
            System.out.println("Couldn't open client socket in ListenerWorker!");
            e.printStackTrace();
        }
    }

    public void checkFlagType(Message message) {
        switch (message.messageType) {
            case JOIN -> handleJoin((JoinMessage) message);
            case LEAVE -> handleLeave(message);
            case CHAT -> handleChat((ChatMessage) message);
            default -> System.out.print("INVALID MESSAGE TYPE");
        }
    }


    public void handleJoin(JoinMessage message) {
        try {
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

    public void handleLeave(Message message) {
        NodeInfo toRemove = (NodeInfo) message.source;
        NodeInfo foundNode = null;
        synchronized (ChatNode.lock) {
        for (NodeInfo node : ChatNode.participantsMap.keySet()){
                if (node.equals(toRemove)) {
                    foundNode = node;
                }
        }
        ChatNode.participantsMap.remove(foundNode);
        }

    }

    public void handleChat(ChatMessage message) {
        System.out.println(message.toString());
    }
}
