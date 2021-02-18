package workers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Listener implements Runnable{
    ServerSocket listenerSocket;
    Socket clientSocket;
    public Listener(ServerSocket inputSocket){
        this.listenerSocket = inputSocket;
    }
    @Override
    public void run() {
        System.out.println("Running listener, checking for new connections.");
        while(true){
            try {
                Thread listenerWorkThread = new Thread(new ListenerWorker(listenerSocket.accept()));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
