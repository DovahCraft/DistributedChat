package com.company;

public class NodeInfo {
    String ip;
    Integer port;
    String name;

    public NodeInfo(int inputPort, String inputName){
        this.ip = null;
        this.port = inputPort;
        this.name = inputName;
    }

    public void setIp(String inputIp){
        this.ip = inputIp;
    }

    @Override
    public String toString(){
        return "IP: " + this.ip + "Port: " + this.port.toString() + "Name: " + this.name;
    }
}
