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
        String curString = "";
        if (k == 0) { // базис
            if (i != j) {
                curString = getTransByDestNode(mindfa, i - 1, j - 1); // если существует путь из i в j
            } else {
                curString = getTransByDestNode(mindfa, i - 1, j - 1); // если существует путь из i в j
                if (!curString.equals("")) {
                    curString = curString.concat("|^");
                } else curString = curString.concat("^");
            }
            return curString;
        } else {
            // R(k,i,j) = R(k-1,i,j) | (R(k-1,i,k)(R(k-1,k,k)+|^)R(k-1,k,j))
            String tmpString = R(mindfa, k - 1, i, j);
            curString = curString.concat(tmpString); // R(k,i,j) = R(k-1,i,j)
            if (!tmpString.equals(""))
                curString = curString.concat("|("); // |(
            else {
                curString = curString.concat("(");
            }
            String tmpString1 = R(mindfa, k - 1, i, k);
            String tmpString2 = R(mindfa, k - 1, k, k);
            String tmpString3 = R(mindfa, k - 1, k, j);
            if (tmpString1.equals("") | tmpString2.equals("") | tmpString3.equals("")){
                return tmpString;
            }
            curString = curString.concat(tmpString1); // R(k-1,i,k)
            curString = curString.concat("(("); // (
            curString = curString.concat(tmpString2); // R(k-1,k,k)
            curString = curString.concat(")+|^)("); // )+|^
            curString = curString.concat(tmpString3); // R(k-1,k,j)
            curString = curString.concat("))"); // )
        }
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
            resString = resString.concat(R(mindfa, mindfa.nodesArray.length, sourceId, endId[i]));
            if (i != endId.length - 1) {
                resString = resString.concat("|");
            }
        }
        System.out.println(resString);
        String eqResString;
        do {
            eqResString = resString;
            resString = resString.replaceAll("\\(\\(\\^\\)\\+\\|\\^\\)", "^"); // replace ((^)+|^) to ^
            //resString = resString.replaceAll("\\(\\^\\)", "^") // replace (^) to ^
                    //.replaceAll("\\(\\)", "") // replace () to ""
                    //.replaceAll("\\(\\|\\(", "\\(\\("); // replace (|( to ((
            while (resString.indexOf("|") == 0 || resString.indexOf(".") == 0) { // delete first |
                resString = resString.replaceFirst("\\|", "");
            }
            resString = resString.replaceAll("\\|\\|", "\\|"); // replace || to |
            resString = resString.replaceAll("\\^\\^", "\\^"); // replace ^^ to ^
            resString = resString.replaceAll("\\|\\)", "\\)"); // replace |) to )
            resString = resString.replaceAll("\\(\\^\\(", "\\(\\("); // replace (^( to ((
            resString = resString.replaceAll("\\)\\^\\)", "\\)\\)"); // replace )^) to ))
            resString = resString.replaceAll("\\)\\^\\(", "\\)\\("); // replace )^( to )(
            //resString = resString.replaceAll("\\|\\^\\(", "\\(") // replace |^( to (
            //       .replaceAll("\\^\\)", "\\)") // replace ^) to )
            //        .replaceAll("\\|\\^\\)", "\\)"); // replace |^) to )
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