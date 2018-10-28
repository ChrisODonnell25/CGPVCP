package com.fyp.cgp.nodes;

import java.util.Arrays;

public class ConnectionNode extends GenericNode {
    private int function;
    private int[] connections;

    public ConnectionNode(int index, int function, int[] connections) {
        super(index);
        this.function = function;
        this.connections = connections;
    }

    public int getFunction() {
        return function;
    }

    public int[] getConnections() {
        return connections;
    }

    public void setFunction(int function) {
        this.function = function;
    }

    public void setConnections(int[] connections) {
        this.connections = connections;
    }

    @Override
    public String toString() {
        return "ConnectionNode{" +
                "index=" + getIndex() +
                ", function=" + function +
                ", connections=" + Arrays.toString(connections) +
                '}';
    }

    public String getGenesString(){
        return getFunction() + " " + connections[0] + " " + connections[1];
    }
}
