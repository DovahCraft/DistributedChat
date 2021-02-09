package com.company;

public class NodeInfo {
    String ip;
    int port;
    String name;

    public NodeInfo(int inputPort, String inputName){
        this.port = inputPort;
        this.name = inputName;
    }

    void setIp(String inputIp){
        this.ip = inputIp;
    }
}
