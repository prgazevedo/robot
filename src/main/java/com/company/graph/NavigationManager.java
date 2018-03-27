package com.company.graph;

import javafx.geometry.Point2D;

import java.util.Random;

public class NavigationManager {

    private Random m_random;
    private MapGraph m_mp;

    public NavigationManager(MapGraph mg) {
        m_random = new Random(System.currentTimeMillis());
        m_mp = mg;
    }


    private  void mock_navigator_1(){

        int last_visited=0;
        for (int i=0; i<GraphProperties.NAV_ITERATIONS; i++) {

            int v0 = last_visited;
            int v1 = m_random.nextInt(GraphProperties.N_VERTEXES);
            last_visited = v1;
            m_mp.AddEdge(String.valueOf(i),v0,v1);


        }
    }

    public void mock_navigator_3(){

        int last_visited=0;
        for (int i=0; i<GraphProperties.NAV_ITERATIONS; i++) {

            int v0 = last_visited;
            int v1 = nextVertex(v0);
            if(v1!=-1) {
                last_visited = v1;
                m_mp.AddEdge(String.valueOf(i), v0, v1);
            }
            else
            {
                System.out.println("Stop navigation");
                break;
            }

        }
        System.out.println("End navigation");
    }

    private  int nextVertex(int v0){
        int v1=-1;
        boolean bSearching=true;
        while(bSearching) {
            int i_new_direction = m_random.nextInt(4);
            Direction direction = Direction.ordinal(i_new_direction);
            Point2D oldlocation = m_mp.getVertex(v0).getM_coords();
            Point2D newlocation = new Point2D(oldlocation.getX() + direction.getX(), oldlocation.getY() + direction.getY());
            newlocation=boundNavigation(newlocation);

            try {
                v1 = m_mp.getVertexId(newlocation);

            } catch (Exception e) {
                System.out.println("mock_navigator" + e.toString());
            }
            if(!m_mp.wasVertexVisited(v1)) bSearching=false;
        }
        return v1;

    }

    private  Point2D boundNavigation(Point2D newlocation){
        //Lower Limit: change direction
        if(newlocation.getX()<0)
        {
            newlocation = new Point2D(-newlocation.getX(),newlocation.getY());
        }
        if(newlocation.getY()<0){
            newlocation = new Point2D(newlocation.getX(),-newlocation.getY());
        }
        //Upper Limit: change to other border
        if(newlocation.getX()>m_mp.getM_Upper_X_Location())
        {
            newlocation = new Point2D(0,newlocation.getY());
        }
        if(newlocation.getY()>m_mp.getM_Upper_Y_Location()){
            newlocation = new Point2D(newlocation.getX(),0);
        }

        return newlocation;
    }



    private  void mock_navigator_2(){

        int last_visited=0;
        for (int i=0; i<GraphProperties.NAV_ITERATIONS; i++) {

            int v0 = last_visited;
            int i_new_direction = m_random.nextInt(4);
            //TODO add code to change direction
            Direction direction= Direction.ordinal(i_new_direction);
            Point2D oldlocation = m_mp.getVertex(v0).getM_coords();
            Point2D newlocation = new Point2D(oldlocation.getX()+direction.getX(),oldlocation.getY()+direction.getY());
            newlocation = boundNavigation(newlocation);
            try {
                int v1 = m_mp.getVertexId(newlocation);
                last_visited = v1;
                m_mp.AddEdge(String.valueOf(i),v0,v1);
            }
            catch (Exception e)
            {
                System.out.println("mock_navigator" +e.toString());
            }


        }
    }

    public enum Direction {

        UP(0, GraphProperties.NODE_Y_DISTANCE), DOWN(0, -GraphProperties.NODE_Y_DISTANCE), LEFT(-GraphProperties.NODE_X_DISTANCE, 0), RIGHT(GraphProperties.NODE_X_DISTANCE, 0);

        public static Direction ordinal(int i){
            switch(i)
            {
                case 0: return UP;
                case 1: return LEFT;
                case 2: return DOWN;
                case 3:
                default:
                    return RIGHT;
            }
        }

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
