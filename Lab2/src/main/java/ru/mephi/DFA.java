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
    protected Set<SoftReference<DFANode>> sets = new HashSet<>(); // сет множеств, которые уже были в очереди
    private SoftReference<DFANode> start; // указатель на начальное состояние
    private Set<SoftReference<DFANode>> end = new HashSet<>(); // массив принимающих вершин
    protected Set<String> alphabet = new HashSet<>();
    protected SoftReference<DFANode> terminalNode;

    public DFA(SoftReference<NFA> nfa) {
        this.alphabet.addAll(nfa.get().alphabet);
    }

    public DFA() {
    }

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

    public DFA makeDFA(SoftReference<NFA> nfa) {
        DFA dfa = new DFA();
        dfa.alphabet.addAll(nfa.get().alphabet);
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
                    if ((curSet.getValue().equals(setElem.get().getValue()))) { // есть ли элемент в множестве вершин
                        k++;
                    }
                }
                if (k == 0) { // если нет, добавляем
                    SoftReference<DFANode> curSetSR = new SoftReference<>(curSet);
                    dfa.q.add(curSetSR);
                    if (curSet.getValue().size() == 0) {
                        dfa.terminalNode = curSetSR;
                    }
                    dfa.sets.add(new SoftReference<>(curSet));
                }
                tmpSet.get().listNodes.add(new Pair<>(new SoftReference<>(curSet), symbol));
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

        /*
        for (SoftReference<DFANode> nodes : dfa.getSets()) {
            for (String symbol : dfa.alphabet) {
                SoftReference<DFANode> nullNode = nodes.get().getTransBySymbol(symbol);
                if (nullNode == null || nullNode.get().getValue().size() == 0) {
                    Pair<SoftReference<DFANode>, String> pair = new Pair(nullNode, symbol);
                    nodes.get().listNodes.remove(pair);
                    nodes.get().listNodes.add(new Pair<>(nodes, symbol));
                }
            }
        }
        dfa.getSets().removeIf(nodes -> nodes.get().getValue().size() == 0);
        */
        return dfa;
    }
}