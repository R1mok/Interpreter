package ru.mephi;

import javafx.util.Pair;
import lombok.Data;

import java.lang.ref.SoftReference;
import java.util.*;

@Data
public class NFANode {
    protected HashSet<Pair<SoftReference<NFANode>, Node>> listNodes = new HashSet<>();
    private Node value; // значение вершины
    private int id; // номер вершины в массиве NFA

    NFANode(int id){
        this.id = id;
    }
}
