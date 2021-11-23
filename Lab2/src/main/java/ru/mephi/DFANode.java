package ru.mephi;

import javafx.util.Pair;
import lombok.Data;

import java.lang.ref.SoftReference;
import java.util.HashSet;
import java.util.Set;

@Data
public class DFANode {
    protected HashSet<Pair<SoftReference<DFANode>, String>> listNodes = new HashSet<>(); // переходы по вершинам DFA
    private Set<SoftReference<NFANode>> value; // множество вершин из NFA или вершина из DFA
    DFANode(Set<SoftReference<NFANode>> value){
        this.value = value;
    }
}
