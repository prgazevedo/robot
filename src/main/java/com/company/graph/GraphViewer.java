package com.company.graph;

import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import org.apache.commons.collections15.Transformer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class GraphViewer extends JFrame {

    private Dimension m_dimension;
    private VisualizationViewer vv;
    private StaticLayout m_layout;

    public GraphViewer(String title, Dimension dimension, StaticLayout layout) throws HeadlessException {
        super(title);
        m_dimension= new Dimension(dimension);
        m_layout=layout;
    }

    public void viewGraph()
    {
        createVisualization();
        createFrame();
    }

    public void createFrame() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }

    public void createVisualization() {
        vv = new VisualizationViewer(m_layout, new Dimension(800, 600));


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
