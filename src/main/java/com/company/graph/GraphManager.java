package com.company.graph;




import java.awt.*;
import java.util.Random;

public class GraphManager {

    private static Random m_random;
    private static MapGraph m_mp;
    private static GraphViewer m_gv;



    public GraphManager() {


    }


    public static void main(String[] args) {
        m_mp  = new MapGraph(GraphProperties.N_VERTEXES);
        m_gv = new GraphViewer("Graph", new Dimension(GraphProperties.WINDOW_HEIGHT,GraphProperties.WINDOW_WIDTH), m_mp.getM_layout());
        mock_navigator_1();
        m_gv.viewGraph();
    }

    private static void mock_navigator_1(){
        m_random = new Random(0);
        int last_visited=0;
        for (int i=0; i<GraphProperties.NAV_ITERATIONS; i++) {

            int v0 = last_visited;
            int v1 = m_random.nextInt(GraphProperties.N_VERTEXES);
            last_visited = v1;
            m_mp.AddEdge(v0,v1);


        }
    }

    private static void mock_navigator_2(){
        m_random = new Random(0);
        int last_visited=0;
        for (int i=0; i<GraphProperties.NAV_ITERATIONS; i++) {

            int v0 = last_visited;
            int new_direction = m_random.nextInt(4);
            //TODO add code to change direction
            //last_visited = v1;
            //m_mp.AddEdge(v0,v1);


        }
    }

    public enum Direction {

        UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0);

        private Direction(float x, float y) {
            this.x = x;
            this.y = y;
        }

        private float x;
        private float y;

        public float getX() {
            return x;
        }
        public float getY() {
            return y;
        }

    }


}
