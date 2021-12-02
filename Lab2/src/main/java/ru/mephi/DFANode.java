package ru.mephi;

import javafx.util.Pair;
import lombok.Data;

import java.lang.ref.SoftReference;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

@Data
public class DFANode {
    protected HashSet<Pair<SoftReference<DFANode>, String>> listNodes = new HashSet<>(); // переходы по вершинам DFA
    private Set<SoftReference<NFANode>> value; // множество вершин из NFA или вершина из DFA

    DFANode(Set<SoftReference<NFANode>> value) {
        this.value = value;
    }

    public DFANode() {

    }

    public DFANode(int i) {
        NFANode nfaNode = new NFANode(i);
        Set<SoftReference<NFANode>> newSet = new HashSet<>();
        newSet.add(new SoftReference<>(nfaNode));
        this.value = newSet;
    }

    public Set<SoftReference<DFANode>> getGroupByNode(Set<Set<SoftReference<DFANode>>> Splitting) {
        for (Set<SoftReference<DFANode>> group : Splitting) {
            for (SoftReference<DFANode> node : group) {
                if (node.get().equals(this)) {
                    return group;
                }
            }
        }
        return null;
    }
    public boolean isTransAlreadyExist(SoftReference<DFANode> destNode, String symbol){
        for (Pair<SoftReference<DFANode>, String> pair : this.listNodes){
            if (pair.getKey().get().equals(destNode.get()) && pair.getValue().equals(symbol)){
                return true;
            }
        }
        return false;
    }
    public SoftReference<DFANode> getTransBySymbol(String symbol) {
        for (Pair<SoftReference<DFANode>, String> pair : listNodes) {
            if (pair.getValue().equals(symbol)) {
                return pair.getKey();
            }
        }
        return null;
    }
}
