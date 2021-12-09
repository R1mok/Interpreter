package ru.mephi;

import javafx.util.Pair;

import javax.swing.*;
import java.lang.ref.SoftReference;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class minDFA {
    private Set<Set<SoftReference<DFANode>>> Splitting = new HashSet<>();
    protected SoftReference<DFANode>[] nodesArray;
    protected SoftReference<DFANode> startNode;
    protected Set<SoftReference<DFANode>> endNodes = new HashSet<>();

    minDFA(SoftReference<DFANode>[] nodesArray) {
        this.nodesArray = nodesArray;
    }

    minDFA() {

    }

    public int getIdOfGroup(Set<Set<SoftReference<DFANode>>> Splitting, SoftReference<DFANode> destNode) {
        int i = 0;
        for (Set<SoftReference<DFANode>> group : Splitting) {
            for (SoftReference<DFANode> node : group) {
                if (node.get().getValue().equals(destNode.get().getValue())) {
                    return i;
                }
            }
            ++i;
        }
        return -1;
    }

    public void makeMinDFA(DFA dfa) {
        Set<SoftReference<DFANode>> endSet = new HashSet<>(dfa.getEnd());
        Set<SoftReference<DFANode>> otherSet = new HashSet<>();
        Splitting.add(endSet);
        for (SoftReference<DFANode> nodes : dfa.getSets()) {
            for (String symbol : dfa.alphabet) {
                SoftReference<DFANode> nullNode = nodes.get().getTransBySymbol(symbol);
                if (dfa.terminalNode != null && nullNode.get().equals(dfa.terminalNode.get())) {
                    nodes.get().listNodes.removeIf(node -> node.getKey().get().equals(dfa.terminalNode.get()) && node.getValue().equals(symbol)); // не добавляется элемент с тремя штуками
                }
            }
        }
        if (dfa.terminalNode != null) {
            dfa.sets.removeIf(node -> node.get().equals(dfa.terminalNode.get()));
        }
        for (SoftReference<DFANode> nodes : dfa.getSets()) {
            if (!endSet.contains(nodes)) {
                otherSet.add(nodes);
            }
        }
        Splitting.add(otherSet);
        while (true) {
            Set<Set<SoftReference<DFANode>>> mainSet = new HashSet();
            Set<Set<SoftReference<DFANode>>> startSplitting = new HashSet<>();
            startSplitting.addAll(Splitting);
            for (Set<SoftReference<DFANode>> group : Splitting) { // просматриваем все группы из разбиения
                Set<SoftReference<DFANode>> nodesSet = new HashSet<>();
                for (SoftReference<DFANode> node : group) { // просматриваем по каждому символу из алфавита
                    int belongsToGroup = 0;
                    for (String symbol : dfa.alphabet) { // просматриваем по каждому элементу из группы
                        SoftReference<DFANode> destNode = node.get().getTransBySymbol(symbol);
                        if (destNode == null) {
                            continue;
                        }
                        Set<SoftReference<DFANode>> destGroup = destNode.get().getGroupByNode(Splitting);
                        if (destGroup != null && !destGroup.equals(group)) { // destNode принадлежит этой группе
                            belongsToGroup++;
                        }
                    }
                    if (belongsToGroup > 0) {
                        nodesSet.add(node);
                    }
                }
                int del = 0;
                // если nodeSet.equals(group); нужно разделить все элементы в nodeSet по разным группам
                if (nodesSet.equals(group) && nodesSet.size() > 1) {
                    for (SoftReference<DFANode> elem : nodesSet) {
                        Set<SoftReference<DFANode>> newSet = new HashSet<>();
                        newSet.add(elem);
                        mainSet.add(newSet);
                    }
                    Splitting.removeIf(val -> val.equals(group));
                    Splitting.addAll(mainSet);
                    break;
                }
                int notEquals = 0;// исправить тут что-то для того, чтобы не было в разных группах одинаковых элементов
                for (SoftReference<DFANode> node : nodesSet) {
                    for (Set<SoftReference<DFANode>> nodes : Splitting) {
                        if (nodes.equals(nodesSet)) {
                            ++notEquals;
                        }
                    }
                    if (notEquals == 0) {
                        group.removeIf(val -> val.get().equals(node.get()));
                        ++del;
                    }
                }
                if (!nodesSet.isEmpty() && del > 0) {
                    mainSet.add(nodesSet);
                    break;
                }
            }
            if (!mainSet.isEmpty())
                Splitting.addAll(mainSet);
            if (startSplitting.equals(Splitting)) {
                break;
            }
        }
        nodesArray = new SoftReference[Splitting.size()];
        int i = 0;
        for (Set<SoftReference<DFANode>> group : Splitting) {
            DFANode node = new DFANode(i);
            SoftReference<DFANode> nodeSR = new SoftReference<>(node);
            nodesArray[i] = nodeSR;
            ++i;
        }
        i = 0;
        for (Set<SoftReference<DFANode>> group : Splitting) {
            for (SoftReference<DFANode> node : group) {
                for (String symbol : dfa.alphabet) {
                    SoftReference<DFANode> destNode = node.get().getTransBySymbol(symbol);
                    if (destNode != null) {
                        Set<SoftReference<DFANode>> destGroup = destNode.get().getGroupByNode(Splitting);
                        if (destGroup != null) {
                            int GroupId = getIdOfGroup(Splitting, destNode);
                            if (!nodesArray[i].get().isTransAlreadyExist(nodesArray[GroupId], symbol))
                                nodesArray[i].get().listNodes.add(new Pair<>(nodesArray[GroupId], symbol));
                        }
                    }
                }
            }
            ++i;
        }
        SoftReference<DFANode> dfaSR = dfa.getStart();
        startNode = nodesArray[getIdOfGroup(Splitting, dfaSR)];
        for (SoftReference<DFANode> endnodes : dfa.getEnd()) {
            endNodes.add(nodesArray[getIdOfGroup(Splitting, endnodes)]);
        }
    }
}
