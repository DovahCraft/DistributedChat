package com.company;


import java.io.Serializable;

//Create a class for our Node info that is Serializable
public class NodeInfo implements Serializable {
    //init variables
    public String ip;
    public int port;
    public String name;
    
    //assign variables 
    public NodeInfo(int inputPort, String inputName) {
        this.ip = null;
        this.port = inputPort;
        this.name = inputName;
    }
    //set the current Node's IP
    public void setIp(String inputIp) {
        this.ip = inputIp;
    }
    
    //Check if two nodes are equal
    @Override
    public boolean equals(Object other) {
        if (other instanceof NodeInfo) {
            NodeInfo otherInfo = (NodeInfo) other;
            return this.ip.equals(otherInfo.ip) &&
                    this.port == otherInfo.port &&
                    this.name.equals(otherInfo.name);
        }
        return false;
    }
    
    //Print Node Information
    @Override
    public String toString() {
        return "IP: " + this.ip + " Port: " + this.port + " Name: " + this.name;
    }
}
