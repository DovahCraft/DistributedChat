package message;

import com.company.NodeInfo;

public class JoinMessage extends Message {
    final public String destinationIp;
    final public int destinationPort;

    public JoinMessage(MessageType messageType, NodeInfo source, String destinationIp, int destinationPort) {
        super(messageType, source);
        this.destinationIp = destinationIp;
        this.destinationPort = destinationPort;
    }
}
