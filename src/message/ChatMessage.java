package message;

import com.company.NodeInfo;

//Message of Chat Type
public class ChatMessage extends Message {
    public final String payload;
    //init ChatMessage
    public ChatMessage(MessageType messageType, NodeInfo source, String payload) {
        super(messageType, source);
        this.payload = payload;
    }

    @Override
    public String toString() {
        //return payload
        return source.name + " : " + payload;
    }
}
