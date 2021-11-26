package ru.mephi;

import com.mxgraph.layout.*;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.util.mxCellRenderer;
import javafx.util.Pair;
import org.antlr.v4.runtime.misc.OrderedHashSet;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedPseudograph;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.charset.StandardCharsets;
import java.util.*;


class MyEdge extends DefaultWeightedEdge {
    public String edgeName;
    public boolean startNode = false;

    @Override
    public String toString() {
        return edgeName;
    }
}

public class Viz {
    public static DirectedWeightedPseudograph<String, MyEdge> createDFAGraph(SoftReference<DFA> dfa) throws IOException {
        File imgFile = new File("DFAgraph.png");
        imgFile.createNewFile();
        DirectedWeightedPseudograph<String, MyEdge> g =
                new DirectedWeightedPseudograph<String, MyEdge>(MyEdge.class);
        for (SoftReference<DFANode> dfanode : dfa.get().getSets()) {
            String curVertex = "";
            Set<Integer> hashSet = new TreeSet<>();
            if (dfanode.get().getValue().size() == 0) {
                curVertex += "T";
                g.addVertex(curVertex);
                continue;
            }
            for (SoftReference<NFANode> nfanode : dfanode.get().getValue()) {
                hashSet.add(nfanode.get().getId());
            }
            for (Integer elem : hashSet) {
                curVertex = curVertex.concat(String.valueOf(elem)) + ".";
            }
            g.addVertex(curVertex);
        }
        for (SoftReference<DFANode> dfanode : dfa.get().getSets()) {
            Set<Integer> hashSet = new TreeSet<>();
            for (SoftReference<NFANode> nfanode : dfanode.get().getValue()) {
                hashSet.add(nfanode.get().getId());
            }
            String sourceVertex = "";
            for (Integer elem : hashSet) {
                sourceVertex = sourceVertex.concat(String.valueOf(elem)) + ".";
            }
            if (sourceVertex.equals("")) {
                sourceVertex = "T";
            }
            hashSet.clear();
            for (Pair<SoftReference<DFANode>, String> nfanode : dfanode.get().listNodes) {
                for (SoftReference<NFANode> destnfa : nfanode.getKey().get().getValue()) {
                    hashSet.add(destnfa.get().getId());
                }
                String destVertex = "";
                for (Integer elem : hashSet) {
                    destVertex = destVertex.concat(String.valueOf(elem)) + ".";
                }
                hashSet.clear();
                if (destVertex.equals("")) {
                    destVertex = "T";
                }
                String curEdge = nfanode.getValue();
                if (Objects.equals(nfanode.getValue(), "CIRCUMFLEXUS"))
                    curEdge = "^";

                MyEdge edge = new MyEdge();
                edge.edgeName = curEdge;
                g.addEdge(sourceVertex, destVertex, edge);
            }
        }
        return g;
    }

    public static void VizDFA(SoftReference<DFA> dfa) throws IOException {
        JGraphXAdapter<String, MyEdge> graphAdapter =
                new JGraphXAdapter<String, MyEdge>(createDFAGraph(dfa));
        mxIGraphLayout layout = new mxHierarchicalLayout(graphAdapter); // mxHierarchicalLayout
        layout.execute(graphAdapter.getDefaultParent());
        BufferedImage image =
                mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
        File imgFile = new File("DFAgraph.png");
        ImageIO.write(image, "PNG", imgFile);
    }

    public static DirectedWeightedPseudograph<String, MyEdge> createNFAGraph(SoftReference<NFA> nfa) throws IOException {
        File imgFile = new File("NFAgraph.png");
        imgFile.createNewFile();
        DirectedWeightedPseudograph<String, MyEdge> g =
                new DirectedWeightedPseudograph<String, MyEdge>(MyEdge.class);
        for (NFANode nfaNode : nfa.get().nodes) {
            String curVertex = String.valueOf(nfaNode.getId());
            g.addVertex(curVertex);
        }
        for (NFANode nfaNode : nfa.get().nodes) {
            String sourceVertex = String.valueOf(nfaNode.getId());
            for (Pair<SoftReference<NFANode>, Node> elem : nfaNode.listNodes) {
                String curEdge;
                if (elem.getValue().getValue() instanceof Metasymbols) {
                    curEdge = ((Metasymbols) elem.getValue().getValue()).value;
                } else {
                    curEdge = ((Node) elem.getValue().getValue()).getValue().toString();
                }
                if (Objects.equals(curEdge, "CIRCUMFLEXUS"))
                    curEdge = "^";
                MyEdge tmpEdge = new MyEdge();
                tmpEdge.edgeName = curEdge;
                String destVertex = String.valueOf(elem.getKey().get().getId());
                g.addEdge(sourceVertex, destVertex, tmpEdge);
            }
        }
        return g;
    }

    public static void VizNFA(SoftReference<NFA> nfa) throws IOException {
        JGraphXAdapter<String, MyEdge> graphAdapter =
                new JGraphXAdapter<String, MyEdge>(createNFAGraph(nfa));
        mxIGraphLayout layout = new mxHierarchicalLayout(graphAdapter); // mxHierarchicalLayout
        layout.execute(graphAdapter.getDefaultParent());
        BufferedImage image =
                mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
        File imgFile = new File("NFAgraph.png");
        ImageIO.write(image, "PNG", imgFile);
    }

    public static void main(String[] args) throws IOException {
        String str1 = "a{3,4}"; // aaa(^|a) a{3}
        String str = "(aaa)(^|a)"; // (aaa)|(aaaa)
        AbstractSyntaxTree tree = new AbstractSyntaxTree(str);
        Node rootNode = tree.buildTree();
        tree.doOrder(rootNode);
        NFA nfa = new NFA(rootNode);
        SoftReference<NFA> nfaSR = new SoftReference<>(nfa);
        DFA dfa = new DFA();
        dfa = dfa.makeDFA(nfaSR);
        SoftReference<DFA> dfaSR = new SoftReference<>(dfa);
        System.out.println("NFA nodes");
        System.out.println("StartNode: " + nfa.getStart().get().getId());
        System.out.println("EndNode: " + nfa.getEnd().get().getId());
        VizNFA(nfaSR);
        System.out.println("DFA nodes");
        System.out.print("Start node: ");
        TreeSet<Integer> hashSet = new TreeSet<>();
        for (SoftReference<NFANode> nfastart : dfa.getStart().get().getValue()) {
            hashSet.add(nfastart.get().getId());
        }
        for (Integer elem : hashSet) {
            System.out.print(elem);
        }
        System.out.println("");
        hashSet.clear();
        System.out.print("End nodes: ");
        for (SoftReference<DFANode> endNodes : dfa.getEnd()) {
            for (SoftReference<NFANode> endNFANodes : endNodes.get().getValue()) {
                hashSet.add(endNFANodes.get().getId());
            }
            for (Integer elem : hashSet) {
                System.out.print(elem + ".");
            }
            hashSet.clear();
            System.out.print(" ");
        }
        VizDFA(dfaSR);

    }
}