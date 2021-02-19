package workers;

import com.company.ChatNode;
import com.company.NodeInfo;
import com.company.ParticipantsMap;
import message.Message;
import message.JoinMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Sender implements Runnable {
    final Message message;

    public Sender(Message message) {
        this.message = message;
    }

    @Override
    public void run() {
        ObjectInputStream in;
        ObjectOutputStream out;
        Socket socket;
        try {
            switch (message.messageType) {
                case JOIN -> {
                    JoinMessage joinMessage = (JoinMessage) message;
                    socket = new Socket(joinMessage.destinationIp, joinMessage.destinationPort);
                    in = new ObjectInputStream(socket.getInputStream());
                    out = new ObjectOutputStream(socket.getOutputStream());
                    out.writeObject(message);
                    synchronized (ChatNode.lock) {
                        Object othersList = in.readObject();
                        //Action if null?
                        if (othersList instanceof ParticipantsMap) {
                            ChatNode.participantsMap.putAll((ParticipantsMap) othersList);
                        }

                    }
                    in.close();
                    out.close();
                    socket.close();
                }

                case CHAT -> sendToAll();

                case LEAVE -> {
                    sendToAll();
                    synchronized (ChatNode.lock) {
                        ChatNode.participantsMap.clear();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
        }
    }

    private void sendToAll() throws IOException {
        Socket socket;
        ObjectOutputStream out;
        synchronized (ChatNode.lock) {
            for (NodeInfo node : ChatNode.participantsMap.keySet()) {
                socket = new Socket(node.ip, node.port);
                out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(message);
                out.close();
                socket.close();
            }
        }
    }
}
