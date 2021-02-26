package workers;

import com.company.ChatNode;
import com.company.ParticipantsMap;
import com.company.Utils;
import message.Message;
import message.JoinMessage;
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
        ObjectOutputStream out;
        ObjectInputStream in;
        Socket socket;
        try {
            switch (message.messageType) {
                case JOIN -> {
                    JoinMessage joinMessage = (JoinMessage) message;
                    socket = new Socket(joinMessage.destinationIp, joinMessage.destinationPort);
                    out = new ObjectOutputStream(socket.getOutputStream());
                    in = new ObjectInputStream(socket.getInputStream());
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

                case CHAT -> Utils.sendToAll(message);

                case LEAVE -> {
                    Utils.sendToAll(message);
                    synchronized (ChatNode.lock) {
                        ChatNode.participantsMap.clear();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
        }
    }


}
