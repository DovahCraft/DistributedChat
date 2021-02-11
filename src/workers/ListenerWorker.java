package workers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
                ObjectInputStream inputStream = new ObjectInputStream(chatSocket.getInputStream());
                ObjectOutputStream outputStream = new ObjectOutputStream(chatSocket.getOutputStream())
        ) {
            System.out.println("Opened listenerWorker input and output streams sucsessfully.");

        } catch (IOException e) {
            System.out.println("Couldn't open client socket in ListenerWorker!");
            e.printStackTrace();
        }
    }
}
