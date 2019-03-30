package com.company.graph;

import com.company.MainRobot;
import com.company.manager.IManager;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.renderers.BasicEdgeRenderer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.apache.logging.log4j.Level;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class GraphViewer extends JFrame  {

    private Dimension m_dimension;

    public VisualizationViewer getM_vv() {
        return m_vv;
    }

    private VisualizationViewer m_vv;
    private StaticLayout m_layout;

    public GraphManager getM_graphManager() {
        return m_graphManager;
    }

    private GraphManager m_graphManager;



    public GraphViewer(GraphManager graphManager) throws HeadlessException {
        m_graphManager = graphManager;
        m_layout= m_graphManager.getM_layout();
        m_dimension= new Dimension(GraphProperties.WINDOW_HEIGHT,GraphProperties.WINDOW_WIDTH);
        createVisualization();
    }

    public void viewGraph()
    {
        createVisualization();
        createFrame();
    }

    public void updateGraph()
    {
        updateVisualization();
        createFrame();
    }

    public void createFrame() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setSize(m_dimension);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void createVisualization() {
        m_vv = new VisualizationViewer(m_layout, m_dimension);




        m_vv.getRenderContext().setVertexFillPaintTransformer(drawVertexColor());
        m_vv.getRenderContext().setVertexShapeTransformer(drawVertexShape());
        m_vv.getRenderContext().setEdgeLabelTransformer(drawEdgeLabel());
        //zooming and transforming
        GraphZoomScrollPane zoomPane = new GraphZoomScrollPane(m_vv);
        DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();
        m_vv.setGraphMouse(graphMouse);
        improvePerformance(m_vv);

        this.getContentPane().add(m_vv);
    }


    public void updateVisualization() {
        m_layout= m_graphManager.getM_layout();
        m_vv.setGraphLayout(m_layout);
        m_vv.repaint();
    }


    public Transformer<Integer, Paint> drawVertexColor() {
       return getVertexColorTransformer();
    }
    public Transformer<Integer, Shape> drawVertexShape() {
       return getVertexShapeTransformer();
    }
    public Transformer<String, String> drawEdgeLabel() {
        return getEdgeLabelTransformer();
    }

/*
    private void drawVertexCoords(){
        m_vv.getRenderContext().setVertexLabelTransformer(getVertexLabelTransformer());
    }



    private void drawEdgeLabel() {
        m_vv.getRenderContext().setEdgeLabelTransformer(getEdgeLabelTransformer());
    }
    */


    private Transformer<Integer, Paint> getVertexColorTransformer(){
        // Transformer maps the vertex number to a vertex property
        return  new Transformer<Integer, Paint>() {
            public Paint transform(Integer vID) {

                if (m_graphManager.isVertexWall(vID)) return GraphProperties.WALL_COLOR;
                else if (m_graphManager.wasVertexVisited(vID)) return GraphProperties.VISITED_NODE_COLOR;
                else return GraphProperties.NODE_COLOR;

            }
        };
    }

    private Transformer<Integer, Shape> getVertexShapeTransformer() {
        return new Transformer<Integer, Shape>() {
            public Shape transform(Integer i) {
                Ellipse2D circle = new Ellipse2D.Double(GraphProperties.NODE_X, GraphProperties.NODE_Y, GraphProperties.NODE_W, GraphProperties.NODE_H);
                return circle;
            }
        };
    }

    private Transformer<Integer,String> getVertexLabelTransformer(){
        return new Transformer<Integer, String>() {
            public String transform(Integer e) {
                String s= "C:"+ m_graphManager.getVertex(e).getM_coords().toString();
                return (e.toString()+s );
            }
        };
    }


    private Transformer<String,String> getEdgeLabelTransformer(){
        return new Transformer<String, String>() {
            public String transform(String e) {
                return (e.toString());
            }
        };
    }

    /*
    OPTIMIZATIONS
     */

    // This method summarizes several options for improving the painting
    // performance. Enable or disable them depending on which visual features
    // you want to sacrifice for the higher performance.
    private static <V, E> void improvePerformance(
            VisualizationViewer<V, E> vv)
    {
        // Probably the most important step for the pure rendering performance:
        // Disable anti-aliasing
        vv.getRenderingHints().remove(RenderingHints.KEY_ANTIALIASING);

        // Skip vertices that are not inside the visible area.
        doNotPaintInvisibleVertices(vv);

        // May be helpful for performance in general, but not appropriate
        // when there are multiple edges between a pair of nodes: Draw
        // the edges not as curves, but as straight lines:
        vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<V,E>());

        // May be helpful for painting performance: Omit the arrow heads
        // of directed edges
        Predicate<Context<Graph<V, E>, E>> edgeArrowPredicate =
                new Predicate<Context<Graph<V,E>,E>>()
                {
                    @Override
                    public boolean evaluate(Context<Graph<V, E>, E> arg0)
                    {
                        return false;
                    }
                };
        vv.getRenderContext().setEdgeArrowPredicate(edgeArrowPredicate);

    }

    // Skip all vertices that are not in the visible area.
    // NOTE: See notes at the end of this method!
    private static <V, E> void doNotPaintInvisibleVertices(
            VisualizationViewer<V, E> vv)
    {
        Predicate<Context<Graph<V, E>, V>> vertexIncludePredicate =
                new Predicate<Context<Graph<V,E>,V>>()
                {
                    Dimension size = new Dimension();

                    @Override
                    public boolean evaluate(Context<Graph<V, E>, V> c)
                    {
                        vv.getSize(size);
                        Point2D point = vv.getGraphLayout().transform(c.element);
                        Point2D transformed =
                                vv.getRenderContext().getMultiLayerTransformer()
                                        .transform(point);
                        if (transformed.getX() < 0 || transformed.getX() > size.width)
                        {
                            return false;
                        }
                        if (transformed.getY() < 0 || transformed.getY() > size.height)
                        {
                            return false;
                        }
                        return true;
                    }
                };
        vv.getRenderContext().setVertexIncludePredicate(vertexIncludePredicate);

        // NOTE: By default, edges will NOT be included in the visualization
        // when ONE of their vertices is NOT included in the visualization.
        // This may look a bit odd when zooming and panning over the navigation.
        // Calling the following method will cause the edges to be skipped
        // ONLY when BOTH their vertices are NOT included in the visualization,
        // which may look nicer and more intuitive
        doPaintEdgesAtLeastOneVertexIsVisible(vv);
    }

    // See note at end of "doNotPaintInvisibleVertices"
    private static <V, E> void doPaintEdgesAtLeastOneVertexIsVisible(
            VisualizationViewer<V, E> vv)
    {
        vv.getRenderer().setEdgeRenderer(new BasicEdgeRenderer<V, E>()
        {
            @Override
            public void paintEdge(RenderContext<V,E> rc, Layout<V, E> layout, E e)
            {
                GraphicsDecorator g2d = rc.getGraphicsContext();
                Graph<V,E> graph = layout.getGraph();
                if (!rc.getEdgeIncludePredicate().evaluate(
                        Context.<Graph<V,E>,E>getInstance(graph,e)))
                    return;

                Pair<V> endpoints = graph.getEndpoints(e);
                V v1 = endpoints.getFirst();
                V v2 = endpoints.getSecond();
                if (!rc.getVertexIncludePredicate().evaluate(
                        Context.<Graph<V,E>,V>getInstance(graph,v1)) &&
                        !rc.getVertexIncludePredicate().evaluate(
                                Context.<Graph<V,E>,V>getInstance(graph,v2)))
                    return;

                Stroke new_stroke = rc.getEdgeStrokeTransformer().transform(e);
                Stroke old_stroke = g2d.getStroke();
                if (new_stroke != null)
                    g2d.setStroke(new_stroke);

                drawSimpleEdge(rc, layout, e);

                // restore paint and stroke
                if (new_stroke != null)
                    g2d.setStroke(old_stroke);
            }
        });
    }



}
