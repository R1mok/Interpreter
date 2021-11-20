package ru.mephi;

import javafx.util.Pair;
import lombok.Data;

import java.lang.ref.SoftReference;
import java.util.*;

@Data
public class DFA {
    private Queue<SoftReference<DFANode>> q = new ArrayDeque<>(); // очередь множеств из NFA
    private Set<SoftReference<DFANode>> sets = new HashSet<>(); // сет множеств, которые уже были в очереди
    private SoftReference<DFANode> start; // указатель на начальное состояние
    private SoftReference<DFANode>[] end; // массив принимающих вершин

    private DFANode epsCircuits(NFA nfa, DFANode nodes) {
        Set<NFANode> curSet = new HashSet<>();
        for (NFANode elem : nodes.getValue()) {
            Set<NFANode> tmpSet = epsCircuits(nfa, elem);
            curSet.addAll(tmpSet);
        }
        return new DFANode(curSet);
    }

    private Set<NFANode> epsCircuits(NFA nfa, NFANode node) {
        Queue<NFANode> q = new ArrayDeque<>();
        Set<NFANode> set = new HashSet<>();
        q.offer(node);
        int[] visited = new int[nfa.getCountNodes()];
        visited[node.getId()] = 1;
        while (!q.isEmpty()) {
            NFANode tmpNode = q.poll();
            set.add(tmpNode);
            for (Pair<SoftReference<NFANode>, Node> nodes : tmpNode.listNodes) {
                if (visited[nodes.getKey().get().getId()] == 0 && nodes.getValue().equals(Metasymbols.EPSILON)) {
                    q.offer(nodes.getKey().get());
                    set.add(nodes.getKey().get());
                    visited[nodes.getKey().get().getId()] = 1;
                }
            }
        }
        return set;
    }

    private DFANode trans(DFANode DFAset, String symbol) {
        Set<NFANode> newSet = new HashSet<>();
        for (NFANode elem : DFAset.getValue()) {
            newSet.add(elem);
            for (Pair<SoftReference<NFANode>, Node> nodes : elem.listNodes) {
                if (nodes.getValue().equals(symbol)) {
                    newSet.add(nodes.getKey().get());
                }
            }
        }
        return new DFANode(newSet);
    }

    public DFA makeDFA(NFA nfa) { // изменить epsCircuits and trans чтобы была работа с ссылками
        DFA dfa = new DFA();
        Set<NFANode> startSet = epsCircuits(nfa, nfa.getStart().get());
        DFANode startNode = new DFANode(startSet);
        dfa.q.add(new SoftReference<>(startNode));
        dfa.sets.add(new SoftReference<>(startNode));
        dfa.start = new SoftReference<>(startNode);
        while (!dfa.q.isEmpty()) {
            SoftReference<DFANode> tmpSet = dfa.q.poll();
            for (String symbol : nfa.alphabet) {
                DFANode curSet = epsCircuits(nfa, trans(tmpSet.get(), symbol));
                if (!dfa.sets.contains(curSet)) {
                    dfa.q.add(new SoftReference<>(curSet));
                    tmpSet.get().listNodes.add(new Pair<>(new SoftReference<>(curSet), symbol));
                    dfa.sets.add(new SoftReference<>(curSet));
                }
            }
        }
        return dfa;
    }

}
