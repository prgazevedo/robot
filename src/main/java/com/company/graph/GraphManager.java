package com.company.graph;





import javafx.geometry.Point2D;

import java.awt.*;
import java.util.Random;
import java.util.Timer;

public class GraphManager {


    private static MapGraph m_mp;
    private static GraphViewer m_gv;
    private static NavigationManager m_nm;



    public GraphManager() {
        m_mp  = new MapGraph(GraphProperties.N_VERTEXES);
        m_gv = new GraphViewer("Graph", m_mp );
        m_nm = new NavigationManager(m_mp);

    }


    public static void main(String[] args) {
       GraphManager gm = new GraphManager();
        m_nm.mock_navigator_3();
        m_gv.viewGraph();
    }




}
