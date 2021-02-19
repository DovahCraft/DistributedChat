package message;

import com.company.NodeInfo;

import java.io.Serializable;

public class Message implements Serializable {
    public final MessageType messageType;
    public final NodeInfo source;
    public Message(MessageType messageType, NodeInfo source) {
        this.messageType = messageType;
        this.source = source;
    }
}
