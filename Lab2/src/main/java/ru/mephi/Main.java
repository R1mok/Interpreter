package ru.mephi;

public class Main {
    public static void main(String[] args) {
        String str = "(12:(r{2,2}ab|c))";
        AbstractSyntaxTree tree = new AbstractSyntaxTree(str);
        Node rootNode = tree.buildTree();
        tree.doOrder(rootNode);
        NFA Automato = new NFA();
        Automato.doOrder(rootNode);
    }
}
