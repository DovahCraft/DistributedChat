package com.company;


public class NodeInfo {
    public String ip;
    public int port;
    public String name;

    public NodeInfo(int inputPort, String inputName) {
        this.ip = null;
        this.port = inputPort;
        this.name = inputName;
    }

    public void setIp(String inputIp) {
        this.ip = inputIp;
    }

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

    @Override
    public String toString() {
        return "IP: " + this.ip + "Port: " + this.port + "Name: " + this.name;
    }
}
