package ru.mephi;

import javafx.util.Pair;
import lombok.Data;

import java.lang.ref.SoftReference;
import java.util.*;

class MulDFANode {
    protected Pair<DFANode, DFANode> node;
    protected HashSet<Pair<MulDFANode, String>> listnodes;
}

class mulDFA {
    protected MulDFANode[] nodesArray;
    protected MulDFANode startNode;
    protected HashSet<MulDFANode> endNodes;
}

@Data
public class RegexLib {
    private String string;
    private DFA dfa;
    private NFA nfa;
    private minDFA mindfa;
    private AbstractSyntaxTree tree;
    private Set<Pair<HashMap<String, Integer>, String>> table = new HashSet<>();

    public minDFA compile(String string) {
        this.string = string;
        AbstractSyntaxTree tree = new AbstractSyntaxTree(string);
        Node rootNode = tree.buildTree();
        tree.doOrder(rootNode);
        this.tree = tree;
        NFA nfa = new NFA(rootNode);
        this.nfa = nfa;
        SoftReference<NFA> nfaSR = new SoftReference<>(nfa);
        System.out.println(tree.getAlphabet());
        DFA dfa = new DFA();
        dfa = dfa.makeDFA(nfaSR);
        this.dfa = dfa;
        minDFA mindfa = new minDFA();
        mindfa.makeMinDFA(dfa);
        this.mindfa = mindfa;
        return mindfa;
    }

    private String getTransByDestNode(minDFA mindfa, int sourceNode, int endNode) {
        SoftReference<DFANode> endDFA = mindfa.nodesArray[endNode];
        String resString = "";
        int transCount = mindfa.nodesArray[sourceNode].get().listNodes.size();
        for (Pair<SoftReference<DFANode>, String> pair : mindfa.nodesArray[sourceNode].get().listNodes) {
            //System.out.println("i:" + sourceNode + "j:" + endNode + "getKey: " + pair.getKey().get().getValue().toString() + "\nendDFA: " + endDFA.get().getValue().toString());
            if (pair.getKey().get().getValue().equals(endDFA.get().getValue())) {
                //System.out.println("its equals");
                resString = resString.concat(pair.getValue());
                if (transCount != 1) {
                    resString = resString.concat("|");
                }
                --transCount;
            }
        }
        return resString;
    }

    private String R(minDFA mindfa, int k, int i, int j) {
        HashMap<String, Integer> tmpMap = new HashMap<>();
        tmpMap.put("k", k);
        tmpMap.put("i", i);
        tmpMap.put("j", j);
        for (Pair<HashMap<String, Integer>, String> pair : table) {
            if (pair.getKey().equals(tmpMap)) {
                return pair.getValue();
            }
        }
        //System.out.println("k:" + k + "i:" + i + "j:" + j);
        String curString = "";
        if (k == 0) { // базис
            curString = getTransByDestNode(mindfa, i, j);
            return curString;
        } else {
            // R(k,i,j) = R(k-1,i,j) | (R(k-1,i,k)(R(k-1,k,k)+|^)R(k-1,k,j))
            curString = curString.concat(R(mindfa, k - 1, i, j)); // R(k,i,j) = R(k-1,i,j)
            curString = curString.concat("|("); // |(
            curString = curString.concat(R(mindfa, k - 1, i, k)); // R(k-1,i,k)
            curString = curString.concat("(("); // ((
            curString = curString.concat(R(mindfa, k - 1, k, k)); // R(k-1,k,k)
            curString = curString.concat(")+|^)("); // )+|^)(
            curString = curString.concat(R(mindfa, k - 1, k, j)); // R(k-1,k,j)
            curString = curString.concat("))"); // ))
        }

        HashMap<String, Integer> curTriple = new HashMap<>();
        curTriple.put("k", k);
        curTriple.put("i", i);
        curTriple.put("j", j);
        table.add(new Pair<>(curTriple, curString));
        return curString;
    }

    public String kpath(minDFA mindfa) {
        int sourceId = mindfa.startNode.get().getValue().stream().findFirst().get().get().getId();
        int[] endId = new int[mindfa.endNodes.size()];
        int p = 0;
        for (SoftReference<DFANode> endNode : mindfa.endNodes) {
            endId[p] = endNode.get().getValue().stream().findFirst().get().get().getId();
        }
        String resString = "";
        for (int i = 0; i < endId.length; ++i) {
            resString = resString.concat(R(mindfa, mindfa.nodesArray.length - 1, sourceId, endId[i]));
            if (i != endId.length - 1) {
                resString = resString.concat("|");
            }
        }
        System.out.println(resString);
        String eqResString;
        do {
            eqResString = resString;
            resString = resString.replaceAll("\\(\\(\\^\\)\\+\\|\\^\\)", "") // delete ((^)+|^)
                    .replaceAll("\\(\\(\\)\\+\\|\\^\\)", ""); // delete (()+|^)
            resString = resString.replaceAll("\\(\\^\\)", "") // delete (^)
                    .replaceAll("\\(\\)", "")
                    .replaceAll("\\(\\|\\(", "\\(\\("); // delete ()
            while (resString.indexOf("|") == 0) {
                resString = resString.replaceFirst("\\|", "");
            }
            resString = resString.replaceAll("\\|\\|", "\\|");
            resString = resString.replaceAll("\\|\\)", "\\|\\^\\)");
        } while (!eqResString.equals(resString));
        return resString;
    }

    public void search(String str) {
        minDFA mindfa = compile(str);
    }

    public mulDFA multiplyOfAutomatoes(String str) {
        minDFA secdfa = compile(str);
        mulDFA muldfa = new mulDFA();
        muldfa.nodesArray = new MulDFANode[this.mindfa.nodesArray.length * secdfa.nodesArray.length];
        int i = 0;
        for (SoftReference<DFANode> firstNode : this.mindfa.nodesArray) {
            for (SoftReference<DFANode> secondNode : secdfa.nodesArray) {
                MulDFANode tmpNode = new MulDFANode();
                tmpNode.node = new Pair<>(firstNode.get(), secondNode.get());
                muldfa.nodesArray[i] = tmpNode;
                ++i;
            }
        }
        for (MulDFANode elem : muldfa.nodesArray) {
            elem.listnodes = new HashSet<>();
            DFANode firNode = elem.node.getKey();
            DFANode secNode = elem.node.getValue();
            DFANode destFirNode = null;
            DFANode destSecNode = null;
            Set<String> alphabets = new HashSet<>();
            alphabets.addAll(this.dfa.alphabet);
            alphabets.addAll(secdfa.alphabet);
            String tmpSymbol = "";
            for (String symbol : alphabets) {
                if (firNode.getTransBySymbol(symbol) != null && secNode.getTransBySymbol(symbol) != null) {
                    destFirNode = firNode.getTransBySymbol(symbol).get();
                    destSecNode = secNode.getTransBySymbol(symbol).get();
                    tmpSymbol = symbol;
                }
            }
            for (MulDFANode destElem : muldfa.nodesArray) {
                if (destElem.node.getKey().equals(destFirNode) && destElem.node.getValue().equals(destSecNode)) {
                    elem.listnodes.add(new Pair<>(destElem, tmpSymbol));
                }
            }
        }
        for (MulDFANode elem : muldfa.nodesArray) {
            if (elem.node.getKey().equals(this.mindfa.startNode.get()) && elem.node.getValue().equals(secdfa.startNode.get())) {
                muldfa.startNode = elem;
            }
        }
        return muldfa;
    }
}