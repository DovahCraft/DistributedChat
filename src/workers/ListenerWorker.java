package workers;

import com.company.ChatNode;
import com.company.NodeInfo;
import message.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ListenerWorker implements Runnable{
    Socket chatSocket;

    public ListenerWorker(Socket inputSocket) {
        this.chatSocket = inputSocket;
    }
    @Override
    public void run() {
        System.out.println("Recieving message!");
        NodeInfo node = new NodeInfo(chatSocket.getPort(), "Name");
        ChatMessage fromClient = new ChatMessage(Type.CHAT, "huh");
        flagType(fromClient);
        /**
        try (
                ObjectInputStream inputStream = new ObjectInputStream(chatSocket.getInputStream());
                ObjectOutputStream outputStream = new ObjectOutputStream(chatSocket.getOutputStream())
        ) {
            System.out.println("Opened listenerWorker input and output streams successfully.");
            //Mostly place holder information
            NodeInfo node = new NodeInfo(chatSocket.getPort(), "Name");
            node.setIp(chatSocket.getLocalSocketAddress().toString());
            /**
            Object fromClient = inputStream.readObject();
            if(fromClient.equals("JOIN"))
            {
                flagType(Type.JOIN);
            }

            else if(fromClient.equals("QUIT"))
            {
                flagType(Type.LEAVE);
            }

            else
            {
                flagType(Type.CHAT);
            }

        } catch (IOException e) {
            System.out.println("Couldn't open client socket in ListenerWorker!");
            e.printStackTrace();
        }
         **/
    }

    public static void flagType(ChatMessage fromClient)
    {
        Type type = fromClient.messageType;
        switch(type)
        {
            case JOIN:
                break;
            case LEAVE:
                break;
            case CHAT:
                handleChat(fromClient);
                break;
            default:
                System.out.print("INVALID MESSAGE TYPE");
        }
    }


    public static void handleChat(ChatMessage fromClient)
    {
        System.out.println("Message :" + fromClient.getPayload());
    }

    public static void handleJoin(NodeInfo node)
    {

    }
}
