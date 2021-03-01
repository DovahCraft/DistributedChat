package com.company.messages;

import com.company.NodeInfo;

import java.io.Serializable;
//Message class that is serializable
public class Message implements Serializable {
    //init variables
    public final MessageType messageType;
    public final NodeInfo source;
    //Set variables
    public Message(MessageType messageType, NodeInfo source) {
        this.messageType = messageType;
        this.source = source;
    }
}
