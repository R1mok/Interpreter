package ru.mephi;

import com.mxgraph.layout.*;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.util.mxCellRenderer;
import javafx.util.Pair;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedPseudograph;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
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
    public static DirectedWeightedPseudograph<String, MyEdge> createGraph(SoftReference<NFA> nfa) throws IOException {
        File imgFile = new File("graph.png");
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

    public static void givenAdaptedGraph_whenWriteBufferedImage_thenFileShouldExist(SoftReference<NFA> nfa) throws IOException {
        JGraphXAdapter<String, MyEdge> graphAdapter =
                new JGraphXAdapter<String, MyEdge>(createGraph(nfa));
        mxIGraphLayout layout = new mxHierarchicalLayout(graphAdapter); // mxHierarchicalLayout
        layout.execute(graphAdapter.getDefaultParent());
        BufferedImage image =
                mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
        File imgFile = new File("graph.png");
        ImageIO.write(image, "PNG", imgFile);
    }

    public static void main(String[] args) throws IOException {
        String str = "a|^";
        AbstractSyntaxTree tree = new AbstractSyntaxTree(str);
        Node rootNode = tree.buildTree();
        tree.doOrder(rootNode);
        NFA nfa = new NFA(rootNode);
        SoftReference<NFA> nfaSR = new SoftReference<>(nfa);
        System.out.println("StartNode: " + nfa.getStart().get().getId());
        System.out.println("EndNode: " + nfa.getEnd().get().getId());
        givenAdaptedGraph_whenWriteBufferedImage_thenFileShouldExist(nfaSR);
    }
}