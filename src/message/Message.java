package message;

import java.io.Serializable;

public  class Message implements Serializable {

    Type messageType;
    Object data;

    public void setType(Type type)
    {
        this.messageType = type;
    }

    public Type getType() {
        return messageType;
    }

    public Object getData() {
        return data;
    }
}
