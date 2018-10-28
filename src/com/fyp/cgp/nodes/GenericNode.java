package com.fyp.cgp.nodes;

public abstract class GenericNode {

    private int index;
    private boolean active;

    public GenericNode(int index){
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public abstract String getGenesString();

    public boolean isActive(){
        return active;
    }

    public void setActive(){
        active = true;
    }

    public void setInactive(){
        active = false;
    }
}
