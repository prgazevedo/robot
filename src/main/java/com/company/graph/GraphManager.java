package com.company.graph;




import java.awt.*;
import java.util.Random;

public class GraphManager {

    private static Random m_random;
    private static MapGraph m_mp;
    private static GridLayout m_g;
    private static GraphViewer m_gv;



    public GraphManager() {


    }


    public static void main(String[] args) {
        m_mp  = new MapGraph(GraphProperties.N_VERTEXES);
        mock_navigator();
        m_g = new GridLayout(m_mp,GraphProperties.N_NODES_IN_COLUMNS,GraphProperties.N_NODES_IN_ROWS);
        m_gv = new GraphViewer("Graph", new Dimension(GraphProperties.WINDOW_HEIGHT,GraphProperties.WINDOW_WIDTH), m_g.getM_layout());
        m_gv.viewGraph();
    }

    private static void mock_navigator(){
        m_random = new Random(0);
        int last_visited=0;
        for (int i=0; i<GraphProperties.NAV_ITERATIONS; i++) {

            int v0 = last_visited;
            int v1 = m_random.nextInt(GraphProperties.N_VERTEXES);
            last_visited = v1;
            m_mp.AddEdge(v0,v1);

        }
    }


}
