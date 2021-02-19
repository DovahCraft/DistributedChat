package workers;

import com.company.ChatNode;
import com.company.NodeInfo;
import java.net.*;
import message.*;
import java.io.*;
import java.net.Socket;

public class ListenerWorker implements Runnable{
    Socket chatSocket;
    public ListenerWorker(Socket inputSocket) {
        this.chatSocket = inputSocket;
    }
    @Override
    public void run() {
        System.out.println("Recieving message!");
        try (
                ObjectInputStream inputStream = new ObjectInputStream(chatSocket.getInputStream());
                ObjectOutputStream outputStream = new ObjectOutputStream(chatSocket.getOutputStream())
        ) {
            synchronized (System.out){
                System.out.println("Chat node connected!");
            }
            NodeInfo node = new NodeInfo(chatSocket.getPort(), "Name");
            Message fromClient = new Message();
            fromClient.setType(Type.CHAT);//place Holder Stuff
            flagType(fromClient.getType());

        } catch (IOException e) {
            System.out.println("Couldn't open client socket in ListenerWorker!");
            e.printStackTrace();
        }
    }

    public static void flagType(Type type)
    {
        switch(type)
        {
            case JOIN:
                break;
            case LEAVE:
                break;
            case CHAT:
                break;
            default:
                System.out.print("INVALID MESSAGE TYPE");
        }
    }


    public static void leaveChat(NodeInfo node )
    {
        //nodes.remove(node);
    }

    public static void joinChat(NodeInfo node)
    {
        //nodes.add(node);
    }
}
