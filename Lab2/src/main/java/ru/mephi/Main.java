package ru.mephi;

import java.lang.ref.SoftReference;

public class Main {

    public static void main(String[] args) {
        String str = "a(^|b)";
        AbstractSyntaxTree tree = new AbstractSyntaxTree(str);
        Node rootNode = tree.buildTree();
        tree.doOrder(rootNode);
        NFA nfa = new NFA(rootNode);
        SoftReference<NFA> nfaSR= new SoftReference<>(nfa);
        System.out.println(tree.getAlphabet());
        DFA dfa = new DFA();
        dfa = dfa.makeDFA(nfaSR);
        minDFA mindfa = new minDFA();
        mindfa.makeMinDFA(dfa);
    }
}
