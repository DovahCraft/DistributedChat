package message;

import com.company.NodeInfo;

//Create message of type join
public class JoinMessage extends Message {
    //init variables
    final public String destinationIp;
    final public int destinationPort;

    public JoinMessage(MessageType messageType, NodeInfo source, String destinationIp, int destinationPort) {
        //set variables
        super(messageType, source);
        this.destinationIp = destinationIp;
        this.destinationPort = destinationPort;
    }
}
