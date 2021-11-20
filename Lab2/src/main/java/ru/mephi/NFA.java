package ru.mephi;

import javafx.util.Pair;
import lombok.Data;

import java.lang.ref.SoftReference;
import java.util.HashSet;
import java.util.Set;

@Data
public class NFA {
    private SoftReference<NFANode> start; // указатель на начало автомата
    private SoftReference<NFANode> end; // указатель на принимающее состояние
    private int countNodes = 0; // количество вершин в автомате
    protected NFANode[] nodes = new NFANode[2]; // массив вершин (изначально начало и конец)
    protected Set<String> alphabet = new HashSet<>();

    public NFA(Node rootNode) {
        NFA newNFA = doOrder(rootNode);
        this.countNodes = newNFA.countNodes;
        this.end = newNFA.end;
        this.start = newNFA.start;
        this.nodes = newNFA.nodes;
    }

    public NFA() {
    }

    public NFA doOrder(Node rootNode) {
        return order(rootNode);
    }

    private NFA order(Node v) {
        if (v != null && v.getRightChild() == null && v.getLeftChild() == null) {
            alphabet.add((String) v.getValue());
        }
        if (v != null) {
            order((Node) v.getLeftChild());
            order((Node) v.getRightChild());
            return createNFA(v);
        }
        return null;
    }

    public static NFA createNFA(Node v) {
        NFA tmpNFA = new NFA();
        if (!(v.getValue() instanceof Metasymbols)) {
            tmpNFA.countNodes = 2;
            int first = 0;
            tmpNFA.nodes[first] = new NFANode(first);
            int second = 1;
            tmpNFA.nodes[second] = new NFANode(second);
            tmpNFA.nodes[first].listNodes.add(new Pair<>(new SoftReference<>(tmpNFA.nodes[second]), v));
            tmpNFA.start = new SoftReference<>(tmpNFA.nodes[first]);
            tmpNFA.end = new SoftReference<>(tmpNFA.nodes[second]);
        }
        if (v.getValue() instanceof Metasymbols) {
            SoftReference<Node> left = new SoftReference<>((Node) v.getLeftChild());
            SoftReference<Node> right = new SoftReference<>((Node) v.getRightChild());

            if (v.getValue().equals(Metasymbols.CIRCUIT)) { // klini в правом дереве
                SoftReference<NFA> predNFA = new SoftReference<>(((Node) v.getRightChild()).auto.get());
                tmpNFA.countNodes = predNFA.get().countNodes + 2;
                NFANode[] newNodes = new NFANode[tmpNFA.countNodes];
                System.arraycopy(predNFA.get().nodes, 0, newNodes, 1, predNFA.get().countNodes);
                for (int i = 1; i < predNFA.get().countNodes + 1; ++i) {
                    newNodes[i].setId(i);
                }
                NFANode startNode = new NFANode(0);
                newNodes[0] = startNode;
                Node newStartNode = new Node(Metasymbols.EPSILON);
                newNodes[0].listNodes.add(new Pair<>(new SoftReference<>(predNFA.get().start.get()), newStartNode));
                tmpNFA.start = new SoftReference<>(newNodes[0]);
                Node newEndNode = new Node(Metasymbols.EPSILON);
                NFANode endNode = new NFANode(tmpNFA.countNodes - 1);
                newNodes[tmpNFA.countNodes - 1] = endNode;
                newNodes[tmpNFA.countNodes - 2].listNodes.add(new Pair<>(new SoftReference<>(newNodes[tmpNFA.countNodes - 1]), newEndNode));
                tmpNFA.end = new SoftReference<>(newNodes[tmpNFA.countNodes - 1]);
                Node newBackTrans = new Node(Metasymbols.EPSILON);
                newNodes[tmpNFA.countNodes - 2].listNodes.add(new Pair<>(new SoftReference<>(newNodes[1]), newBackTrans));
                tmpNFA.nodes = newNodes;
            }
            if (v.getValue().equals(Metasymbols.OR)) {
                NFANode startNode = new NFANode(0);
                NFANode[] newNodes = new NFANode[left.get().getAuto().get().getCountNodes() + right.get().getAuto().get().getCountNodes() + 2];
                System.arraycopy(left.get().getAuto().get().nodes, 0, newNodes, 1, left.get().getAuto().get().getCountNodes());
                for (int i = 1; i < left.get().getAuto().get().getCountNodes() + 1; ++i) {
                    newNodes[i].setId(i);
                }
                newNodes[0] = startNode;
                Node newStartNode = new Node(Metasymbols.EPSILON);
                newNodes[0].listNodes.add(new Pair<>(new SoftReference<>(newNodes[1]), newStartNode));
                System.arraycopy(right.get().getAuto().get().nodes, 0, newNodes, left.get().getAuto().get().getCountNodes() + 1, right.get().getAuto().get().getCountNodes());
                for (int i = 1 + left.get().getAuto().get().getCountNodes(); i < newNodes.length - 1; ++i) {
                    newNodes[i].setId(i);
                }
                newNodes[0].listNodes.add(new Pair<>(new SoftReference<>(newNodes[1 + left.get().getAuto().get().getCountNodes()]), newStartNode));
                NFANode endNode = new NFANode(newNodes.length - 1);
                newNodes[newNodes.length - 1] = endNode;
                Node newEndNode = new Node(Metasymbols.EPSILON);
                newNodes[left.get().getAuto().get().getCountNodes()].listNodes.add(new Pair<>(new SoftReference<>(newNodes[newNodes.length - 1]), newEndNode));
                newNodes[newNodes.length - 2].listNodes.add(new Pair<>(new SoftReference<>(newNodes[newNodes.length - 1]), newEndNode));
                tmpNFA.start = new SoftReference<>(newNodes[0]);
                tmpNFA.setCountNodes(newNodes.length);
                tmpNFA.end = new SoftReference<>(newNodes[tmpNFA.countNodes - 1]);
                tmpNFA.nodes = newNodes;
            }
            if (v.getValue().equals(Metasymbols.AND)) {
                NFANode[] newNodes = new NFANode[left.get().getAuto().get().getCountNodes() + right.get().getAuto().get().getCountNodes() - 1];
                System.arraycopy(left.get().getAuto().get().nodes, 0, newNodes, 0, left.get().getAuto().get().getCountNodes());
                for (int i = 0; i < left.get().getAuto().get().getCountNodes(); ++i) {
                    newNodes[i].setId(i);
                }
                System.arraycopy(right.get().getAuto().get().nodes, 1, newNodes, left.get().getAuto().get().countNodes, right.get().getAuto().get().countNodes - 1);
                for (int i = left.get().getAuto().get().countNodes; i < newNodes.length; ++i) {
                    newNodes[i].setId(i);
                }
                Node crossNode = new Node();
                if (right.get().getValue().equals(Metasymbols.AND) || right.get().getValue().equals(Metasymbols.OR) || right.get().getValue().equals(Metasymbols.CIRCUIT)) {
                    crossNode.setValue(Metasymbols.EPSILON);
                } else {
                    crossNode.setValue(right.get());
                }

                newNodes[left.get().getAuto().get().countNodes - 1].listNodes.add(new Pair<>
                        (new SoftReference<>(newNodes[left.get().getAuto().get().countNodes]), crossNode));
                tmpNFA.start = left.get().getAuto().get().getStart();
                tmpNFA.end = right.get().getAuto().get().end;
                tmpNFA.nodes = newNodes;
                tmpNFA.setCountNodes(newNodes.length);
            }
        }
        SoftReference<NFA> ref = new SoftReference<>(tmpNFA);
        v.setAuto(ref);
        return tmpNFA;
    }
}
