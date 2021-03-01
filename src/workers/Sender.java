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
//Class to handle the sending of functions
public class Sender implements Runnable {
    //init variables
    final Message message;
    //assign variables
    public Sender(Message message) {
        this.message = message;
    }

    @Override
    public void run() {

        try {
            //swutch statement to check case types
            switch (message.messageType) {
                case JOIN -> {
                    JoinMessage joinMessage = (JoinMessage) message;
                    try (
                            //create new socket info based on user input
                            Socket socket = new Socket(joinMessage.destinationIp, joinMessage.destinationPort);
                            //get stream for sending
                            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                            //get strean for receiving 
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
                   
                //Send message to all if a chat message
                case CHAT -> Utils.sendToAll(message);

                case LEAVE -> {
                    try {
                        //until message has been sent to all, keep sending
                        while (!Utils.sendToAll(message)) {
                            Utils.sendToAll(message);
                        }
                        synchronized (ChatNode.lock) {
                            //clear the current nodes participants map
                            ChatNode.participantsMap.clear();
                            //put the current node back into it's own map
                            ChatNode.participantsMap.put(ChatNode.thisNode, true);
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
