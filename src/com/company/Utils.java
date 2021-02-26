package com.company;

import message.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;


public class Utils {
    public static boolean isValidIpAddr(String ipAddr) {
        String[] frags = ipAddr.split("\\.");
        return frags.length == 4 && Arrays.stream(frags).allMatch(Utils::isInt);
    }

    public static boolean isInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException n) {
            return false;
        }
    }

    public static void sendToAll(Message message) throws IOException {
        Socket socket;
        ObjectOutputStream out;
        synchronized (ChatNode.lock) {
            for (NodeInfo node : ChatNode.participantsMap.keySet()) {
                if (!node.equals(ChatNode.thisNode)) {
                    socket = new Socket(node.ip, node.port);
                    out = new ObjectOutputStream(socket.getOutputStream());
                    out.writeObject(message);
                    out.close();
                    socket.close();
                }

            }
        }
    }
}
