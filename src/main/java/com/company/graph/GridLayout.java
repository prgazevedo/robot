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

public class GridLayout {



    public StaticLayout getM_layout() {
        return m_layout;
    }

    private StaticLayout m_layout;




    public GridLayout(MapGraph graph, int numRows, int numColumns) {

        m_layout = new StaticLayout(graph);

        //distance between the nodes
        int distX=1;
        int distY=1;



        int operatingNode = 0;

        for (int i=0;i<numRows;i++) {
            for (int j=0;j<numColumns;j++) {
                m_layout.setLocation((operatingNode++), i*distX, j*distY);
            }
        }

    }


}

