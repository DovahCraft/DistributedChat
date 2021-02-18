package message;

public class ChatMessage extends Message{
    String payload;
    public ChatMessage(Type type, String payload){
        this.messageType = type;
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }
}
