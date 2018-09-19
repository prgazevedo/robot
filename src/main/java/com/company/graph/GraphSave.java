package com.company.graph;

import com.company.properties.PropertiesManager;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.*;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import org.apache.logging.log4j.Level;

import javax.imageio.ImageIO;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GraphSave {

    BufferedImage m_image;
    private VisualizationImageServer  m_vis;
    private VisualizationViewer m_vv;
    private GraphViewer  m_graphViewer;
    private PropertiesManager m_propertiesManager;
    private GraphManager m_graphManager;
    private String m_WorkingDir;

    public GraphSave(GraphManager graphManager) {
        m_graphManager = graphManager;
        m_graphViewer = m_graphManager.getGraphViewer();
        m_vv=m_graphViewer.getM_vv();
        m_WorkingDir = m_graphManager.getM_mainRobot().getM_PropertiesManager().getM_WorkingDir();
        createVisualizationImageServer();

    }

    private void createVisualizationImageServer() {


        // Create the VisualizationImageServer
        // vv is the VisualizationViewer containing my graph
         m_vis = new VisualizationImageServer(m_vv.getGraphLayout(), m_vv.getGraphLayout().getSize());

        // Configure the VisualizationImageServer the same way as for thw Viewer

        m_vis.setBackground(Color.WHITE);
        m_vis.getRenderContext().setEdgeLabelTransformer(m_graphViewer.drawEdgeLabel());
        m_vis.getRenderContext().setVertexShapeTransformer(m_graphViewer.drawVertexShape());
        m_vis.getRenderContext().setVertexFillPaintTransformer(m_graphViewer.drawVertexColor());
        //m_vis.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);


    }


    public void updateImage() {
        // Create the buffered image
        try {
            m_image = (BufferedImage) m_vis.getImage(
                    new Point2D.Double(m_vv.getGraphLayout().getSize().getWidth() / 2,
                            m_vv.getGraphLayout().getSize().getHeight() / 2),
                    new Dimension(m_vv.getGraphLayout().getSize()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void saveImage( File fileToWrite) {
        updateImage();




        try {
            m_graphManager.writeLog(Level.INFO, "GraphSave about to write image in: "+ fileToWrite.getCanonicalPath());
            ImageIO.write(m_image, "png", fileToWrite);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
