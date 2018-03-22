package com.company.graph;

import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import org.apache.commons.collections15.Transformer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

public class JungGridLayout extends JFrame{

    Graph graph;
    StaticLayout layout;
    VisualizationViewer vv;

    public static void main(String[] args) {
        JungGridLayout g = new JungGridLayout(10000,100,100);
    }

    public JungGridLayout(int numNodes, int numRows, int numColumns) {
        graph = new SparseMultigraph();
        layout = new StaticLayout(graph);

        //distance between the nodes
        int distX=1;
        int distY=1;

        //idea is to add the vertices and change and the position of each vertex to a coordinate in a grid
        for (int n=0;n<numNodes;n++) {
            graph.addVertex(n);
        }

        int operatingNode = 0;

        for (int i=0;i<numRows;i++) {
            for (int j=0;j<numColumns;j++) {
                layout.setLocation((operatingNode++), i*distX, j*distY);
            }
        }

        createVisualization();
        createFrame();
    }

    public void createFrame() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }

    public void createVisualization() {
        vv = new VisualizationViewer(layout, new Dimension(800, 600));


        // Transformer maps the vertex number to a vertex property
        Transformer<Integer,Paint> vertexColor = new Transformer<Integer,Paint>() {
            public Paint transform(Integer i) {
                return Color.GREEN;

            }
        };
        Transformer<Integer,Shape> vertexSize = new Transformer<Integer,Shape>(){
            public Shape transform(Integer i){
                Ellipse2D circle = new Ellipse2D.Double(-10, -10, 10, 10);
                return circle;
            }
        };
        vv.getRenderContext().setVertexFillPaintTransformer(vertexColor);
        vv.getRenderContext().setVertexShapeTransformer(vertexSize);

        //zooming and transforming
        GraphZoomScrollPane zoomPane = new GraphZoomScrollPane(vv);
        DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();
        vv.setGraphMouse(graphMouse);

        this.getContentPane().add(zoomPane);
    }
}

