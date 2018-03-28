package com.company.graph;

import javafx.geometry.Point2D;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.NavigableMap;
import java.util.TreeMap;


public class NavigationManager {


    private MapGraph m_mp;

    /**
     * Key is order of path ,Value is Id of vertex
     */
    private NavigableMap<Integer, Integer> m_path;

    private RandomUtil m_random;

    private Integer m_currentPositionKey;
    private Integer m_currentPositionValue;

    public NavigationManager(MapGraph mg) {


        m_path = new TreeMap<Integer, Integer>();
        m_random = new RandomUtil(0,Direction.getNumberDirections());
        m_mp = mg;
        m_currentPositionKey= 0;
        m_currentPositionValue= 0;
    }


    private  void mock_navigator_1(){

        int last_visited=0;
        for (int i=0; i<GraphProperties.NAV_ITERATIONS; i++) {

            int v0 = last_visited;
            int v1 = (new RandomUtil(0,GraphProperties.N_VERTEXES).getNonRepeatingRandomInt());
            last_visited = v1;
            m_mp.AddEdge(String.valueOf(i),v0,v1);


        }
    }

    public void mock_navigator_3(){

        int last_visited=0;
        m_path.put(0,last_visited);
        for (int i=0; i<GraphProperties.NAV_ITERATIONS; i++) {
            System.out.println("mock_navigator_3 - iteration:"+i);
            int v0 = last_visited;

            int v1 = nextVertex(v0);
            if(v1!=-1)
            {
                last_visited = v1;
                System.out.println("mock_navigator_3 navigating to:"+v1);
                m_mp.AddEdge(String.valueOf(i), v0, v1);
                m_mp.setVertexVisited(v1);
                m_currentPositionKey=i+1;
                m_currentPositionValue=v1;
                m_path.put(m_currentPositionKey,m_currentPositionValue);
            }
            else
            {
                System.out.println("mock_navigator_3 Stop navigation");
                break;
            }

        }
        System.out.println("mock_navigator_3 End navigation");
    }

    private  int nextVertex(int v0){
        int v1=-1;
        boolean bSearching=true;
        m_random.init();
        while(bSearching) {
            //int i_new_direction = m_random.nextInt(Direction.getNumberDirections());
            int i_new_direction = m_random.getNonRepeatingRandomInt();
            if(i_new_direction==-1)
            {
                //dead-end -> retrace the path
                bSearching=retracePath();
            }
            else
            {
                //not a dead end
                Direction direction = Direction.navigationDirection(i_new_direction);
                Point2D oldlocation = m_mp.getVertex(v0).getM_coords();
                Point2D newlocation = new Point2D(oldlocation.getX() + direction.getX(), oldlocation.getY() + direction.getY());
                newlocation=boundNavigation(newlocation);

                try {
                    v1 = m_mp.getVertexId(newlocation);

                } catch (Exception e) {
                    System.out.println("mock_navigator" + e.toString());
                }
                if(!m_mp.wasVertexVisited(v1))
                {
                    //New valid node so exit the search
                    bSearching=false;
                }
            }

        }
        return v1;

    }

    private boolean retracePath(){
        if(m_path.containsKey(m_currentPositionKey))
        {
            m_currentPositionKey = m_path.lowerKey(m_currentPositionKey);
            m_currentPositionValue = m_path.get(m_currentPositionKey);
            System.out.println("retracePath go back to:" + m_currentPositionValue);
            nextVertex(m_currentPositionValue);
            return false;
        }
        else
        {
            System.out.println("retracePath Cannot go earlier than:" + m_currentPositionValue);
            return false;
        }

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
            int i_new_direction = m_random.getNonRepeatingRandomInt();//m_random.nextInt(Direction.getNumberDirections());
            //TODO add code to change direction
            Direction direction= Direction.navigationDirection(i_new_direction);
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


    public static enum Direction {

        NORTH(0, new Pair(0, GraphProperties.NODE_Y_DISTANCE)),
        SOUTH(1, new Pair(0, -GraphProperties.NODE_Y_DISTANCE)),
        WEST(2, new Pair(-GraphProperties.NODE_X_DISTANCE, 0)),
        EAST(3, new Pair(GraphProperties.NODE_X_DISTANCE, 0)),
        NORTHWEST(4, new Pair(-GraphProperties.NODE_X_DISTANCE, GraphProperties.NODE_Y_DISTANCE)),
        NORTHEAST(5, new Pair(+GraphProperties.NODE_X_DISTANCE, GraphProperties.NODE_Y_DISTANCE)),
        SOUTHWEST(6, new Pair(-GraphProperties.NODE_X_DISTANCE, -GraphProperties.NODE_Y_DISTANCE)),
        SOUTHEAST(7, new Pair(+GraphProperties.NODE_X_DISTANCE, -GraphProperties.NODE_Y_DISTANCE));

        private final int m_index;
        private final javafx.util.Pair<Integer,Integer> m_direction;

        Direction(int index, Pair<Integer,Integer> direction) {
            this.m_index = index;
            this.m_direction = direction;
        }

        public int index(){return m_index;}
        public static Direction navigationDirection(int i){
            if(i<Direction.values().length && i>-1)
            return Direction.values()[i];
            else return Direction.NORTH;
        }
        public Pair direction(){return m_direction;}


        public static int getNumberDirections()
        {
            return Direction.values().length;
        }


        public int getX() {
            return m_direction.getKey();
        }
        public float getY() {
            return m_direction.getValue();
        }

    }
}
