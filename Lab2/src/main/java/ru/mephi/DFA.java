package ru.mephi;

import javafx.util.Pair;
import lombok.Data;

import javax.print.attribute.SetOfIntegerSyntax;
import java.awt.*;
import java.lang.ref.SoftReference;
import java.util.*;

@Data
public class DFA {
    private Queue<SoftReference<DFANode>> q = new ArrayDeque<>(); // очередь множеств из NFA
    private Set<SoftReference<DFANode>> sets = new HashSet<>(); // сет множеств, которые уже были в очереди
    private SoftReference<DFANode> start; // указатель на начальное состояние
    private Set<SoftReference<DFANode>> end = new HashSet<>(); // массив принимающих вершин

    private Set<SoftReference<NFANode>> epsCircuitsByDFANode(SoftReference<NFA> nfa, SoftReference<DFANode> nodes) {
        Set<SoftReference<NFANode>> curSet = new HashSet<>();
        for (SoftReference<NFANode> elem : nodes.get().getValue()) {
            Set<SoftReference<NFANode>> tmpSet = epsCircuits(nfa, elem);
            curSet.addAll(tmpSet);
        }
        return curSet;
    }

    private Set<SoftReference<NFANode>> epsCircuits(SoftReference<NFA> nfa, SoftReference<NFANode> node) {
        Queue<SoftReference<NFANode>> q = new ArrayDeque<>();
        Set<SoftReference<NFANode>> set = new HashSet<>();
        q.offer(node);
        int[] visited = new int[nfa.get().getCountNodes()];
        visited[node.get().getId()] = 1;
        while (!q.isEmpty()) {
            SoftReference<NFANode> tmpNode = q.poll();
            set.add(tmpNode);
            for (Pair<SoftReference<NFANode>, Node> nodes : tmpNode.get().listNodes) {
                if (visited[nodes.getKey().get().getId()] == 0 && nodes.getValue().equals(new Node(Metasymbols.EPSILON))) {
                    q.offer(nodes.getKey());
                    set.add(nodes.getKey());
                    visited[nodes.getKey().get().getId()] = 1;
                }
            }
        }
        return set;
    }

    private SoftReference<DFANode> trans(SoftReference<DFANode> DFAset, String symbol) {
        Set<SoftReference<NFANode>> newSet = new HashSet<>();
        for (SoftReference<NFANode> elem : DFAset.get().getValue()) {
            // newSet.add(elem);
            for (Pair<SoftReference<NFANode>, Node> nodes : elem.get().listNodes) {
                if (nodes.getValue().getValue() instanceof Node) {
                    if (((Node) nodes.getValue().getValue()).getValue().equals(symbol)) {
                        newSet.add(nodes.getKey());
                    }
                } else {
                    if (nodes.getValue().getValue().equals(symbol)) {
                        newSet.add(nodes.getKey());
                    }
                }
            }
        }
        return new SoftReference<>(new DFANode(newSet));
    }

    public DFA makeDFA(SoftReference<NFA> nfa) { // изменить epsCircuits and trans чтобы была работа с ссылками
        DFA dfa = new DFA();
        Set<SoftReference<NFANode>> startSet = epsCircuits(nfa, nfa.get().getStart());
        DFANode startNode = new DFANode(startSet);
        SoftReference<DFANode> startDFANode = new SoftReference<>(startNode);
        dfa.q.add(startDFANode);
        dfa.sets.add(startDFANode);
        dfa.start = startDFANode;
        while (!dfa.q.isEmpty()) {
            SoftReference<DFANode> tmpSet = dfa.q.poll();
            for (String symbol : nfa.get().alphabet) {
                DFANode curSet = new DFANode(epsCircuitsByDFANode(nfa, trans(tmpSet, symbol)));
                int k = 0;
                for (SoftReference<DFANode> setElem : dfa.sets) {
                    if ((curSet.getValue().equals(setElem.get().getValue()))) { // сравнить кол-во эл-тов из множеств
                        k++;
                    }
                }
                if (k == 0) {
                    dfa.q.add(new SoftReference<>(curSet));
                    tmpSet.get().listNodes.add(new Pair<>(new SoftReference<>(curSet), symbol));
                    dfa.sets.add(new SoftReference<>(curSet));
                } else {
                    for (SoftReference<DFANode> set : dfa.sets){
                        if (set.get().equals(tmpSet.get()));
                        {
                            tmpSet.get().listNodes.add(new Pair<>(tmpSet, symbol));
                        }
                    }
                }
                //tmpSet.get().listNodes.add(new Pair<>(new SoftReference<>(curSet), symbol));
            }
        }
        SoftReference<NFANode> endNode = nfa.get().getEnd();
        for (SoftReference<DFANode> dfaNode : dfa.sets) {
            for (SoftReference<NFANode> nfaNode : dfaNode.get().getValue()) {
                if (nfaNode.get().equals(endNode.get())) {
                    dfa.end.add(dfaNode);
                }
            }
        }
        return dfa;
    }
}
