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
        try (
                ObjectInputStream inputStream = new ObjectInputStream(chatSocket.getInputStream());
                ObjectOutputStream outputStream = new ObjectOutputStream(chatSocket.getOutputStream())
        ) {
            System.out.println("Opened listenerWorker input and output streams successfully.");
            //Mostly place holder information
            NodeInfo node = new NodeInfo(chatSocket.getLocalSocketAddress().toString(), chatSocket.getPort(), "Name");
            String fromClient = inputStream.readLine();
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
