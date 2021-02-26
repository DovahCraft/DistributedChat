package workers;

import com.company.ChatNode;
import com.company.NodeInfo;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Listener implements Runnable {
    Socket clientSocket;
    Integer port;

    public Listener(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        System.out.println("Running listener, checking for new connections to Node");
        try (ServerSocket listenerSocket = new ServerSocket(this.port)) {

            synchronized (ChatNode.lock) {
                System.out.println();
                ChatNode.thisNode.setIp(Inet4Address.getLocalHost().getHostAddress());
                ChatNode.lock.notify();
            }

            while (true) {
                Thread listenerWorkThread = new Thread(new ListenerWorker(listenerSocket.accept()));
                listenerWorkThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
