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
            if (tmpString1.equals("") | tmpString2.equals("") | tmpString3.equals("")) {
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
            ++p;
        }
        String resString = "";
        for (int i = 0; i < endId.length; ++i) {
            resString = resString.concat(R(mindfa, mindfa.nodesArray.length, sourceId, endId[i]));
            if (i != endId.length - 1) {
                resString = resString.concat("|");
            }
        }
        String eqResString;
        do {
            eqResString = resString;
            //resString = resString.replaceAll("\\(\\(\\^\\)\\+\\|\\^\\)", "^"); // replace ((^)+|^) to ^
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

    public boolean isEmptyDFA(mulDFA dfa, minDFA firstdfa, minDFA secdfa, boolean isItForSearch) {
        MulDFANode[] startNodes;
        if (isItForSearch) {
            startNodes = new MulDFANode[firstdfa.nodesArray.length];
            int i = 0;
            for (MulDFANode node : dfa.nodesArray) {
                if (node.node.getValue().equals(secdfa.startNode.get())) {
                    startNodes[i] = node;
                    ++i;
                }
            }
        } else {
            startNodes = new MulDFANode[1];
            startNodes[0] = dfa.startNode;
        }
        for (MulDFANode startNode : startNodes) {
            Queue<MulDFANode> q = new ArrayDeque<>();
            q.offer(startNode);
            int[] visited = new int[dfa.nodesArray.length];
            int startIndex = Arrays.asList(dfa.nodesArray).indexOf(dfa.startNode);
            visited[startIndex] = 1;
            while (!q.isEmpty()) {
                MulDFANode tmpNode = q.poll();
                for (Pair<MulDFANode, String> nodes : tmpNode.listnodes) {
                    if (visited[Arrays.asList(dfa.nodesArray).indexOf(nodes.getKey())] == 0) {
                        q.offer(nodes.getKey());
                        visited[Arrays.asList(dfa.nodesArray).indexOf(nodes.getKey())] = 1;
                        for (MulDFANode key : dfa.endNodes) {  // ищем среди принимающих состояний 1го автомата текущее
                            if (nodes.getKey().equals(key)) {
                                // в принимающих вершинах есть текущая вершина
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean search(String str1, String str2) {
        Object[] mul = multiplyOfAutomatoes(str1, str2);
        minDFA firstdfa = (minDFA) mul[0];
        minDFA secdfa = (minDFA) mul[1]; // L
        mulDFA muldfa = (mulDFA) mul[2];
        muldfa.endNodes = new HashSet<>();
        for (MulDFANode node : muldfa.nodesArray) { // проходим по массиву вершин и ищем принимающие состояния
            boolean endValueExist = false;
            for (SoftReference<DFANode> value : secdfa.endNodes) {
                if (node.node.getValue().equals(value.get())) {
                    endValueExist = true;
                    // в принимающих вершинах есть текущая вершина value (2го автомата)
                }
            }
            if (endValueExist) {
                muldfa.endNodes.add(node);
            }
        }
        if (isEmptyDFA(muldfa, firstdfa, secdfa, true)) {
            return false;
        } else {
            return true;
        }
    }

    public minDFA doFullMinDFA(minDFA mindfa){
        DFANode terminal = new DFANode(mindfa.nodesArray.length+1);
        int newLength = mindfa.nodesArray.length + 1;
        int startNodeId = mindfa.startNode.get().getValue().stream().findFirst().get().get().getId();
        int[] endNodeIds = new int[mindfa.endNodes.size()];
        int i = 0;
        for (SoftReference<DFANode> endNodes : mindfa.endNodes){
            endNodeIds[i] = endNodes.get().getValue().stream().findFirst().get().get().getId();
        }
        SoftReference<DFANode>[] newNodesArray = new SoftReference[newLength];
        System.arraycopy(mindfa.nodesArray, 0, newNodesArray, 0, mindfa.nodesArray.length);
        newNodesArray[mindfa.nodesArray.length] = new SoftReference<>(terminal);
        SoftReference<DFANode> TerminalInArray = newNodesArray[mindfa.nodesArray.length];
        mindfa.nodesArray = newNodesArray;
        for (SoftReference<DFANode> node : mindfa.nodesArray){
            for (String symbol : mindfa.alphabet){
                if (node.get().getTransBySymbol(symbol) == null){
                    node.get().listNodes.add(new Pair<>(new SoftReference<>(terminal), symbol));
                }
            }
        }
        for (SoftReference<DFANode> node : mindfa.nodesArray){
            if (node.get().getValue().stream().findFirst().get().get().getId() == startNodeId){
                mindfa.startNode = node;
            }
            for (int id : endNodeIds) {
                if (node.get().getValue().stream().findFirst().get().get().getId() == id){
                    mindfa.endNodes.add(node);
                }
            }
        }

        return mindfa;
    }
    public mulDFA complement(String str) {
        minDFA dfa = compile(str);
        String alphabetKlini = "(";
        int i = 0;
        for (String symbol : dfa.alphabet) {
            ++i;
            alphabetKlini = alphabetKlini.concat(symbol);
            if (i != dfa.alphabet.size()) {
                alphabetKlini = alphabetKlini.concat("|");
            }
        }
        alphabetKlini = alphabetKlini.concat(")+");
        RegexLib rl = new RegexLib();
        minDFA firstdfa = rl.doFullMinDFA(compile(alphabetKlini));
        firstdfa.endNodes.add(firstdfa.startNode);
        minDFA secdfa = rl.doFullMinDFA(compile(str));
        Object[] mul = multiplyOfAutomatoes(firstdfa, secdfa);
        mulDFA muldfa = (mulDFA) mul[2];
        muldfa.endNodes = new HashSet<>();
        for (MulDFANode node : muldfa.nodesArray) {
            boolean endKeyExist = false;
            boolean endValueExist = false;
            for (SoftReference<DFANode> key : firstdfa.endNodes) {
                if (node.node.getKey().equals(key.get())) {
                    endKeyExist = true; // в принимающих вершинах есть текущая key
                }
            }
            for (SoftReference<DFANode> value : secdfa.endNodes) {
                if (node.node.getValue().equals(value.get())) {
                    endValueExist = true; // в принимающих вершинах есть текущая value
                }
            }
            if (endKeyExist && !endValueExist) {
                muldfa.endNodes.add(node);
            }
        }
        return muldfa;
    }

    public mulDFA intersection(String str1, String str2) {
        Object[] mul = multiplyOfAutomatoes(str1, str2);
        minDFA firstdfa = (minDFA) mul[0];
        minDFA secdfa = (minDFA) mul[1];
        mulDFA muldfa = (mulDFA) mul[2];
        muldfa.endNodes = new HashSet<>();
        for (MulDFANode node : muldfa.nodesArray) {
            boolean endKeyExist = false;
            boolean endValueExist = false;
            for (SoftReference<DFANode> key : firstdfa.endNodes) {
                if (node.node.getKey().equals(key.get())) {
                    endKeyExist = true; // в принимающих вершинах есть текущая key
                }
            }
            for (SoftReference<DFANode> value : secdfa.endNodes) {
                if (node.node.getValue().equals(value.get())) {
                    endValueExist = true;
                }
            }
            if (endKeyExist && endValueExist) {
                muldfa.endNodes.add(node);
            }
        }
        return muldfa;
    }

    public Object[] multiplyOfAutomatoes(Object str1, Object str2) {
        Object[] returning = new Object[3];
        minDFA firstdfa = null;
        minDFA secdfa = null;

        if (str1 instanceof String){
            firstdfa = compile((String) str1);
        } else {
            if (str1 instanceof minDFA){
                firstdfa = (minDFA) str1;
            }
        }
        if (str2 instanceof String){
            secdfa = compile((String) str2);
        } else {
            if (str2 instanceof minDFA){
                secdfa = (minDFA) str2;
            }
        }
        returning[0] = firstdfa;
        returning[1] = secdfa;
        mulDFA muldfa = new mulDFA();
        muldfa.nodesArray = new MulDFANode[firstdfa.nodesArray.length * secdfa.nodesArray.length];
        int i = 0;
        for (SoftReference<DFANode> firstNode : firstdfa.nodesArray) {
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
            alphabets.addAll(firstdfa.alphabet);
            alphabets.addAll(secdfa.alphabet);
            String tmpSymbol = "";
            for (String symbol : alphabets) {
                if (firNode.getTransBySymbol(symbol) != null && secNode.getTransBySymbol(symbol) != null) {
                    destFirNode = firNode.getTransBySymbol(symbol).get();
                    destSecNode = secNode.getTransBySymbol(symbol).get();
                    tmpSymbol = symbol;
                    for (MulDFANode destElem : muldfa.nodesArray) {
                        if (destElem.node.getKey().equals(destFirNode) && destElem.node.getValue().equals(destSecNode)) {
                            elem.listnodes.add(new Pair<>(destElem, tmpSymbol));
                        }
                    }
                }
            }
        }
        for (MulDFANode elem : muldfa.nodesArray) {
            if (elem.node.getKey().equals(firstdfa.startNode.get()) && elem.node.getValue().equals(secdfa.startNode.get())) {
                muldfa.startNode = elem;
            }
        }
        returning[2] = muldfa;
        return returning;
    }
}