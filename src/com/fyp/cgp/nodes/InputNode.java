package com.fyp.cgp.nodes;

public class InputNode extends GenericNode{
    public InputNode(int index){
        super(index);
        setActive();
    }

    @Override
    public String toString() {
        return "InputNode{" +
                "index=" + getIndex() +
                "}";
    }

    public String getGenesString(){return "";}

}
