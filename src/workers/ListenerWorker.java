package workers;

import com.company.ChatNode;

import com.company.Utils;
import message.*;

import java.io.*;
import java.net.Socket;

public class ListenerWorker implements Runnable {
    Socket chatSocket;

    public ListenerWorker(Socket inputSocket) {
        this.chatSocket = inputSocket;
    }

    @Override
    public void run() {
        System.out.println("Recieving message!");
        try (
                ObjectInputStream inputStream = new ObjectInputStream(chatSocket.getInputStream())
                //ObjectOutputStream outputStream = new ObjectOutputStream(chatSocket.getOutputStream());
        ) {
            synchronized (System.out) {
                System.out.println("Chat node connected!");
            }
            Object fromClient = inputStream.readObject();
            Message clientMessage = (Message) fromClient;
            checkFlagType(clientMessage);
        } catch (Exception e) {
            System.out.println("Couldn't open client socket in ListenerWorker!");
            e.printStackTrace();
        }
    }

    public void checkFlagType(Message message) {
        switch (message.messageType) {
            case JOIN -> handleJoin(message);
            case LEAVE -> handleLeave(message);
            case CHAT -> {
                ChatMessage chat = (ChatMessage) message;
                handleChat(chat);
            }
            default -> System.out.print("INVALID MESSAGE TYPE");
        }
    }


    public void handleJoin(Message message) {
        try {
            if (message.source.ip.equals(chatSocket.getInetAddress().getHostAddress())
                    && message.source.port == chatSocket.getPort()) {
                Utils.sendToAll(message);
            }
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
        }
        ChatNode.participantsMap.put(message.source, true);
    }

    public void handleLeave(Message message) {
        try {
            if (message.source.ip.equals(chatSocket.getInetAddress().getHostAddress())
                    && message.source.port == chatSocket.getPort()) {
                Utils.sendToAll(message);
            }
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
        }
        ChatNode.participantsMap.remove(message.source, true);
    }

    public void handleChat(ChatMessage message) {
        System.out.println(message.toString());
    }
}
