package message;

import com.company.NodeInfo;

public class ChatMessage extends Message {
    public final String payload;

    public ChatMessage(MessageType messageType, NodeInfo source, String payload) {
        super(messageType, source);
        this.payload = payload;
    }
}
