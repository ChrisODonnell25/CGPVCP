package com.fyp.cgp.nodes;

public class OutputNode extends GenericNode{
    private int connection;

    public OutputNode(int index, int connection) {
        super(index);
        setActive();
        this.connection = connection;
    }

    public int getConnection() {
        return connection;
    }

    public void setConnection(int connection) {
        this.connection = connection;
    }

    @Override
    public String toString() {
        return "OutputNode{" +
                "index=" + getIndex() +
                ", connection=" + connection +
                '}';
    }

    public String getGenesString(){
        return connection + "";
    }
}
