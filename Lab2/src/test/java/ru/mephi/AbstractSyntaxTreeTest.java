package ru.mephi;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class AbstractSyntaxTreeTest {

    @Test
    public void buildTreeOr() {
        String str = "b|c";
        AbstractSyntaxTree tree = new AbstractSyntaxTree(str);
        Node rootNode = tree.buildTree();
        Assert.assertThat(rootNode.getValue().toString(), is(Metasymbols.OR.toString()));
        Node rightNode = new Node("c");
        Assert.assertThat(((Node) rootNode.getRightChild()), is(rightNode));
        Node leftNode = new Node("b");
        Assert.assertThat(((Node) rootNode.getLeftChild()), is(leftNode));
        tree.doOrder(rootNode);
        Assert.assertThat(((Node) rootNode.getLeftChild()).getParent().get().getValue(), is(Metasymbols.OR));
        Assert.assertThat(((Node) rootNode.getRightChild()).getParent().get().getValue(), is(Metasymbols.OR));
    }

    @Test
    public void buildTreeAnd() {
        String str1 = "a.b";
        String str2 = "ab";
        AbstractSyntaxTree tree1 = new AbstractSyntaxTree(str1);
        AbstractSyntaxTree tree2 = new AbstractSyntaxTree(str2);
        Node rootNode1 = tree1.buildTree();
        Node rootNode2 = tree2.buildTree();
        Assert.assertThat(rootNode1.getValue().toString(), is(Metasymbols.AND.toString()));
        Assert.assertThat(rootNode2.getValue().toString(), is(Metasymbols.AND.toString()));
        Node rightNode1 = new Node("b");
        Node rightNode2 = new Node("b");
        Assert.assertThat(((Node) rootNode1.getRightChild()), is(rightNode1));
        Assert.assertThat(((Node) rootNode2.getRightChild()), is(rightNode2));
        Node leftNode1 = new Node("a");
        Node leftNode2 = new Node("a");
        Assert.assertThat(((Node) rootNode1.getLeftChild()), is(leftNode1));
        Assert.assertThat(((Node) rootNode2.getLeftChild()), is(leftNode2));
        tree1.doOrder(rootNode1);
        tree2.doOrder(rootNode2);
        Assert.assertThat(((Node) rootNode1.getLeftChild()).getParent().get().getValue(), is(Metasymbols.AND));
        Assert.assertThat(((Node) rootNode1.getRightChild()).getParent().get().getValue(), is(Metasymbols.AND));
        Assert.assertThat(((Node) rootNode2.getLeftChild()).getParent().get().getValue(), is(Metasymbols.AND));
        Assert.assertThat(((Node) rootNode2.getRightChild()).getParent().get().getValue(), is(Metasymbols.AND));
    }
    @Test
    public void buildTreeCircuit(){
        String str = "a+";
        AbstractSyntaxTree tree = new AbstractSyntaxTree(str);
        Node rootNode = tree.buildTree();
        Assert.assertThat(rootNode.getValue().toString(), is(Metasymbols.CIRCUIT.toString()));
        Node rightNode = new Node("a");
        Assert.assertThat(((Node) rootNode.getRightChild()), is(rightNode));
        tree.doOrder(rootNode);
        Assert.assertThat(((Node) rootNode.getRightChild()).getParent().get().getValue(), is(Metasymbols.CIRCUIT));
    }
    @Test
    public void buildTreeRepeat(){
        String str1 = "a{1,3}";
        String str2 = "b{3,3}";
        String str3 = "c{3}";
        AbstractSyntaxTree tree1 = new AbstractSyntaxTree(str1);
        AbstractSyntaxTree tree2 = new AbstractSyntaxTree(str2);
        AbstractSyntaxTree tree3 = new AbstractSyntaxTree(str3);
        Node rootNode1 = tree1.buildTree();
        Node rootNode2 = tree2.buildTree();
        Node rootNode3 = tree3.buildTree();

        Assert.assertThat(rootNode1.getValue(), is(new Node(Metasymbols.AND).getValue()));
        Assert.assertThat(((Node)rootNode1.getRightChild()).getValue(), is(new Node(Metasymbols.OR).getValue()));
        Assert.assertThat(((Node)rootNode1.getLeftChild()).getValue(), is(new Node("a").getValue()));
        Assert.assertThat(((Node)((Node)rootNode1.getRightChild()).getRightChild()).getValue(), is(new Node(Metasymbols.AND).getValue()));
        Assert.assertThat(((Node)((Node)rootNode1.getRightChild()).getLeftChild()).getValue(), is(new Node(Metasymbols.OR).getValue()));
        Assert.assertThat(((Node)((Node)((Node)rootNode1.getRightChild()).getLeftChild()).getLeftChild()).getValue(), is(new Node(Metasymbols.CIRCUMFLEXUS).getValue().toString()));
        Assert.assertThat(((Node)((Node)((Node)rootNode1.getRightChild()).getLeftChild()).getRightChild()).getValue(), is(new Node("a").getValue()));
        Assert.assertThat(((Node)((Node)((Node)rootNode1.getRightChild()).getRightChild()).getRightChild()).getValue(), is(new Node("a").getValue()));
        Assert.assertThat(((Node)((Node)((Node)rootNode1.getRightChild()).getRightChild()).getLeftChild()).getValue(), is(new Node("a").getValue()));

        Assert.assertThat(rootNode2.getValue(), is(new Node(Metasymbols.AND).getValue()));
        Assert.assertThat(((Node)rootNode2.getRightChild()).getValue(), is(new Node("b").getValue()));
        Assert.assertThat(((Node)rootNode2.getLeftChild()).getValue(), is(new Node(Metasymbols.AND).getValue()));
        Assert.assertThat(((Node)((Node)rootNode2.getLeftChild()).getLeftChild()).getValue(), is(new Node("b").getValue()));
        Assert.assertThat(((Node)((Node)rootNode2.getLeftChild()).getRightChild()).getValue(), is(new Node("b").getValue()));

        Assert.assertThat(rootNode3.getValue(), is(new Node(Metasymbols.AND).getValue()));
        Assert.assertThat(((Node)rootNode3.getRightChild()).getValue(), is(new Node(Metasymbols.CIRCUIT).getValue().toString()));
        Assert.assertThat(((Node)rootNode3.getLeftChild()).getValue(), is(new Node(Metasymbols.AND).getValue()));
        Assert.assertThat(((Node)((Node)rootNode3.getLeftChild()).getLeftChild()).getValue(), is(new Node(Metasymbols.AND).getValue()));
        Assert.assertThat(((Node)((Node)rootNode3.getLeftChild()).getRightChild()).getValue(), is(new Node("c").getValue()));
        Assert.assertThat(((Node)((Node)((Node)rootNode3.getLeftChild()).getLeftChild()).getLeftChild()).getValue(), is(new Node("c").getValue()));
        Assert.assertThat(((Node)((Node)((Node)rootNode3.getLeftChild()).getLeftChild()).getRightChild()).getValue(), is(new Node("c").getValue()));
    }
    @Test
    public void getCapGroup() {
    }
}