package message;

public class ChatMessage extends Message{
    String payload;
    ChatMessage(Type type, String payload){
        this.messageType = type;
        this.payload = payload;
    }
}
