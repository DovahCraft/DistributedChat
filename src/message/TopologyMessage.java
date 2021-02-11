package message;

import com.company.NodeInfo;

public class TopologyMessage extends Message {
    NodeInfo topologyNode;
    public TopologyMessage(Type type, NodeInfo inputNode){
        //Set the passed enum
        this.messageType = type;
        this.topologyNode = inputNode;
    }
}
