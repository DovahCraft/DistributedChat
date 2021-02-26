package workers;

import com.company.ChatNode;

import com.company.Utils;
import message.*;

import java.io.*;
import java.net.Socket;

public class ListenerWorker implements Runnable {
    final Socket chatSocket;
    final ObjectOutputStream outputStream;
    final ObjectInputStream inputStream;
    public ListenerWorker(Socket inputSocket) throws IOException {
        this.chatSocket = inputSocket;
        outputStream = new ObjectOutputStream(chatSocket.getOutputStream());
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
            ChatNode.participantsMap.put(message.source, true);
            if (message.source.ip.equals(chatSocket.getInetAddress().getHostAddress())
                    && message.source.port == chatSocket.getPort()) {
                Utils.sendToAll(message);
                outputStream.writeObject(ChatNode.participantsMap);
            }
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
        }

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
