package ru.mephi;

import com.mxgraph.layout.*;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.orthogonal.mxOrthogonalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;
import javafx.scene.shape.Circle;
import javafx.util.Pair;
import org.jgraph.graph.CellHandle;
import org.jgraph.graph.CellView;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;
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
    public static int curEdge = 0;

    public MyEdge(String edgeName) {
        this.edgeName = edgeName;
    }

}
public class Viz {
    public static DirectedWeightedPseudograph<String, String> createGraph(SoftReference<NFA> nfa) throws IOException {
        File imgFile = new File("graph.png");
        imgFile.createNewFile();
        DirectedWeightedPseudograph<String, String> g =
                new DirectedWeightedPseudograph<String, String>(String.class);
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
                MyEdge tmpEdge = new MyEdge(curEdge);
                if (g.containsEdge(tmpEdge.edgeName)){
                    tmpEdge.edgeName += MyEdge.curEdge;
                    ++MyEdge.curEdge;
                }
                String destVertex = String.valueOf(elem.getKey().get().getId());
                g.addEdge(sourceVertex, destVertex, tmpEdge.edgeName);
            }
        }
        return g;
    }

    public static void givenAdaptedGraph_whenWriteBufferedImage_thenFileShouldExist(SoftReference<NFA> nfa) throws IOException {
        JGraphXAdapter<String, String> graphAdapter =
                new JGraphXAdapter<String, String>(createGraph(nfa));
        mxIGraphLayout layout = new mxHierarchicalLayout(graphAdapter); // mxHierarchicalLayout
        layout.execute(graphAdapter.getDefaultParent());
        BufferedImage image =
                mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
        File imgFile = new File("graph.png");
        ImageIO.write(image, "PNG", imgFile);
    }

    public static void main(String[] args) throws IOException {
        String str = "a|(3:n)";
        AbstractSyntaxTree tree = new AbstractSyntaxTree(str);
        Node rootNode = tree.buildTree();
        tree.doOrder(rootNode);
        NFA nfa = new NFA(rootNode);
        SoftReference<NFA> nfaSR = new SoftReference<>(nfa);
        System.out.println("StartNode: " + nfa.getStart().get().getId());
        System.out.println("EndNode: " + nfa.getEnd().get().getId());
        int i = 0;
        String[] edgesName = new String[nfaSR.get().alphabet.size()];
        for(String c : nfaSR.get().alphabet){
            edgesName[i] = c;
            ++i;
        }
        givenAdaptedGraph_whenWriteBufferedImage_thenFileShouldExist(nfaSR);
    }
}