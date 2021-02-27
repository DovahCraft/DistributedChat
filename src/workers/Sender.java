package workers;

import com.company.ChatNode;
import com.company.ParticipantsMap;
import com.company.Utils;
import message.Message;
import message.JoinMessage;

import java.io.EOFException;
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

        try {
            switch (message.messageType) {
                case JOIN -> {
                    JoinMessage joinMessage = (JoinMessage) message;
                    try (
                            Socket socket = new Socket(joinMessage.destinationIp, joinMessage.destinationPort);
                            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
                    ) {
                        out.flush();
                        out.writeObject(message);
                        System.out.println("Wrote object sucessfully");
                        out.flush();
                        try {
                            //reads the participant list sent back
                            ParticipantsMap othersList = (ParticipantsMap) in.readObject();
                            ChatNode.participantsMap.putAll(othersList);
                        } catch (EOFException e) {
                            System.err.println("F");
                        } catch (ClassNotFoundException e) {
                        }
                    } catch (IOException e) {
                        System.err.println(e.getLocalizedMessage());
                    }
                }

                case CHAT -> Utils.sendToAll(message);

                case LEAVE -> {
                    try {
                        while (!Utils.sendToAll(message)) {
                            Utils.sendToAll(message);
                        }
                        synchronized (ChatNode.lock) {
                            ChatNode.participantsMap.clear();
                        }
                    } catch (Exception e){
                        System.err.println(e.getLocalizedMessage());
                    }
                }
            }
        } catch (IOException e) {
        }
    }
}
