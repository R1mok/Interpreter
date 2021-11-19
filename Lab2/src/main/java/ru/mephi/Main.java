package ru.mephi;

public class Main {
    public static void main(String[] args) {
        String str = "(rb){3}";
        AbstractSyntaxTree tree = new AbstractSyntaxTree(str);
        Node rootNode = tree.buildTree();
        tree.doOrder(rootNode);
        NFA Automato = new NFA();
        Automato.doOrder(rootNode);
    }
}
