package ru.mephi;

import lombok.*;

import java.lang.ref.SoftReference;

@Data
public class AbstractSyntaxTree {
    private String r;
    private Node rootNode = new Node();

    // new Tree
    AbstractSyntaxTree(String r) {
        this.r = r; // regular expression
    }

    public void doOrder(Node rootNode) {
        rootNode.parent = new SoftReference<>(rootNode);
        setParents(rootNode);
        order(rootNode, 0);
    }

    public void order(Node v, int n) {
        int i;
        if (v != null) {
            order((Node) v.getRightChild(), n + 7);
            for (i = 0; i < n; ++i)
                System.out.print(" ");
            System.out.println(v.getValue());
            order((Node) v.getLeftChild(), n + 7);
        }
    }
    private void setParents(Node rootNode){
        parents(rootNode);
    }
    private void parents(Node v){
        if (v != null){
            if (v.getRightChild() instanceof Node){
                ((Node) v.getRightChild()).parent = new SoftReference<>(v);
            }
            if (v.getLeftChild() instanceof Node){
                ((Node) v.getLeftChild()).parent = new SoftReference<>(v);
            }
            parents((Node) v.getRightChild());
            parents((Node) v.getLeftChild());
        }
    }

    public Node buildTree() {
        String tmp = "(";
        tmp += r.concat(")");
        r = tmp;
        // start of global while
        Object[] a = new Object[r.length()];
        for (int i = 0; i < r.length(); ++i) {
            a[i] = r.charAt(i);
        }
        while (a.length != 1) {
            int maxLb = 0;
            int first = 0, last = r.length();
            int lb = 0;
            for (Object o : a) {
                if (o.equals('(')) {
                    ++lb;
                }
                if (o.equals(')')) {
                    --lb;
                }
                maxLb = Math.max(lb, maxLb);
            }
            for (int i = 0; i < a.length || maxLb > 0; ++i) {
                if (a[i].equals('(')) {
                    --maxLb;
                    first = i;
                }
            }
            for (int i = first; i < a.length; ++i) {
                if (a[i].equals(')')) {
                    last = i;
                    break;
                }
            }
            int k = 0;
            // found the closest brackets
            for (int i = first; i < last; ++i) {
                if (a[i].equals('#')) {
                    Node aNode = new Node();
                    aNode.setValue(String.valueOf(a[i + 1]));
                    a[i] = aNode;
                    Object[] a1 = new Object[a.length - 1];
                    System.arraycopy(a, 0, a1, 0, i + 1);
                    System.arraycopy(a, i+2, a1, i+1, a.length - i - 2);
                    a = a1;
                    k = 1;
                    break;
                }
            }
            last -= k;
            if (k == 1)
                continue;
            for (int i = first; i < last; ++i) {
                if (!a[i].equals('+') && !a[i].equals('.') && !a[i].equals('|') && !a[i].equals('(') && !a[i].equals(')') &&
                        !(a[i] instanceof Node)) {
                    Node aNode = new Node(String.valueOf(a[i]));
                    a[i] = aNode;
                }

            }
            for (int i = first; i < last; ++i) { // found +
                if (a[i].equals('+')) {
                    Node plusNode = new Node(Metasymbols.KLINI);
                    plusNode.setRightChild(a[i - 1]);
                    a[i - 1] = plusNode;
                }
            }
            for (int i = first; i < last; ++i) { // reduce tokens array
                if (a[i].equals('+')) {
                    Object[] a1 = new Object[a.length - 1];
                    System.arraycopy(a, 0, a1, 0, i);
                    System.arraycopy(a, i + 1, a1, i, a.length - i - 1);
                    a = a1;
                    --i;
                    ++k;
                }
            }
            last -= k;
            k = 0;
            for (int i = first; i < last; ++i) { // found |
                if (a[i].equals('|')) {
                    Node orNode = new Node(Metasymbols.OR);
                    orNode.setLeftChild(a[i - 1]);
                    orNode.setRightChild(a[i + 1]);
                    a[i - 1] = orNode;
                    Object[] a1 = new Object[a.length - 2];
                    System.arraycopy(a, 0, a1, 0, i);
                    System.arraycopy(a, i + 2, a1, i, a.length - i - 2);
                    a = a1;
                    last -= 2;
                    k = 1;
                    break;
                }
            }
            if (k == 1)
                continue;
            for (int i = first; i < last; ++i) {
                if (a[i] instanceof Node && a[i + 1] instanceof Node) {
                    Node catNode = new Node();
                    catNode.setValue(Metasymbols.AND);
                    catNode.setLeftChild(a[i]);
                    catNode.setRightChild(a[i + 1]);
                    a[i] = catNode;
                    Object[] a1 = new Object[a.length - 1];
                    System.arraycopy(a, 0, a1, 0, i + 1);
                    System.arraycopy(a, i + 2, a1, i + 1, a.length - i - 2);
                    a = a1;
                    last -= 1;
                    k = 1;
                    break;
                }
            }
            if (k == 1)
                continue;
            for (int i = first; i < last; ++i) {
                if (a[i].equals('.')) {
                    Node catNode = new Node(Metasymbols.AND);
                    catNode.setLeftChild(a[i - 1]);
                    catNode.setRightChild(a[i + 1]);
                    a[i - 1] = catNode;
                    Object[] a1 = new Object[a.length - 2];
                    System.arraycopy(a, 0, a1, 0, i);
                    System.arraycopy(a, i + 2, a1, i, a.length - i - 2);
                    a = a1;
                    last -= 2;
                    k = 1;
                    break;
                }
            }
            if (k == 1)
                continue;
            Object[] a1 = new Object[a.length - 2];
            System.arraycopy(a, 0, a1, 0, first);
            System.arraycopy(a, first + 1, a1, first, last - first - 1);
            System.arraycopy(a, last + 1, a1, last - 1, a.length - last - 1);
            a = a1;
        }
        return rootNode = (Node) a[0];
    }
}
