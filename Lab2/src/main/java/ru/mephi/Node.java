package ru.mephi;

import lombok.*;

import java.lang.ref.SoftReference;

@Data
public class Node {
    private Object value;
    private Object leftChild;
    private Object rightChild;
    protected SoftReference<Node> parent;
    protected SoftReference<NFA> auto;

    public Node(){
        this.value = null;
        this.leftChild = null;
        this.rightChild = null;
    }
    public Node(Object value){
        this.value = value;
    }
}
