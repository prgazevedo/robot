package com.company.graph;


import com.company.MainRobot;
import com.company.manager.IManager;
import com.company.manager.Manager;
import com.company.navigation.Direction;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GraphManager extends Manager implements IManager {


    private StaticLayout m_layout;
    private edu.uci.ics.jung.graph.Graph<Integer,String> m_graph;
    private HashMap<Integer,Vertex> m_hashmapVertexes;
    private HashMap<Coordinates2D,Integer> m_hashmapLocations;
    private int m_Upper_X_Location = 0;
    private int m_Upper_Y_Location = 0;
    /** The logger we shall use */
    private final static Logger logger =  LogManager.getLogger(GraphManager.class);

    public Graph getM_graph() {
        return m_graph;
    }

    public StaticLayout getM_layout() { return m_layout; }

    public int getM_Upper_X_Location() { return m_Upper_X_Location; }

    public int getM_Upper_Y_Location() { return m_Upper_Y_Location; }

    @Override
    public void initialize() {
        super.initialize();
        populateVertexes();
    }


    public GraphManager(MainRobot mainRobot) {
        m_mainRobot = mainRobot;
        m_graph = new SparseMultigraph<Integer,String>();
        m_hashmapVertexes = new HashMap<Integer,Vertex>();
        m_hashmapLocations = new HashMap<Coordinates2D,Integer>();
        m_layout = new StaticLayout(m_graph);

    }



    public Vertex getVertex(int ID){
        Vertex v=null;
        try {
            v =  m_hashmapVertexes.get(ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }

    public boolean wasVertexVisited(int ID){
        boolean bWasvisited=false;
        try {
            bWasvisited =  m_hashmapVertexes.get(ID).isM_visited();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bWasvisited;
    }

    public boolean isVertexWall(int ID){
        boolean bIsWall=false;
        try {
            bIsWall = m_hashmapVertexes.get(ID).isM_wall();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bIsWall;
    }


    public void setVertexVisited(int ID,boolean bVisited){
        if(m_hashmapVertexes.containsKey(ID)) {
            Vertex v = m_hashmapVertexes.get(ID);
            v.setM_visited(bVisited);
            m_hashmapVertexes.replace(ID, v);
        }
    }

    public void setVertexWall(int ID,boolean isWall){
        if(m_hashmapVertexes.containsKey(ID)) {
            Vertex v = m_hashmapVertexes.get(ID);
            v.setM_wall(isWall);
            m_hashmapVertexes.replace(ID, v);
        }
    }



    public Integer getVertexId(Coordinates2D location){return m_hashmapLocations.get(location);}

    public void populateVertexes()
    {
        int operatingNode = 0;
        for (int i = 0; i<GraphProperties.N_NODES_IN_ROWS; i++) {
            for (int j = 0; j<GraphProperties.N_NODES_IN_COLUMNS; j++) {

                Coordinates2D location = calculateNewLocation(i,j);
                m_graph.addVertex(operatingNode);
                Vertex v = new Vertex(operatingNode, location);
                mapNeighbors(v,operatingNode);
                boundNeighbors(j,i,v);
                m_hashmapVertexes.put(operatingNode,v);
                m_hashmapLocations.put(location,operatingNode);
                m_layout.setLocation(operatingNode,location.getX(),location.getY());
                operatingNode++;

            }
        }

    }



    public void printMap()
    {
        writeLog(Level.INFO,"printMap");
        int operatingNode = 0;
        for (int i = 0; i<GraphProperties.N_NODES_IN_ROWS; i++) {
            for (int j = 0; j<GraphProperties.N_NODES_IN_COLUMNS; j++) {


                Vertex v  = m_hashmapVertexes.get(operatingNode);
                writeLog(Level.INFO,"Vertex Id:"+operatingNode);
                writeLog(Level.INFO,"Vertex Location:"+v.getM_coords().toString());
                writeLog(Level.INFO,"Neighbors:"+v.printNeighbors());
                operatingNode++;

            }
        }
        setUpperLocationBounds();

    }


    public Coordinates2D getVertexCoordinates(Integer VID)
    {
        if(m_hashmapVertexes.containsKey(VID)) {
            try {
                Vertex v = m_hashmapVertexes.get(VID);
                return v.getM_coords();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        else return null;
    }

    public int getNeighborIDInHops(int myID,Direction direction, int hops){
       return getNeighborInDirection( myID, direction, hops);
    }

    public int getNeighborID(int myID, Direction direction){
        int neighborID = -1;
        if(direction.equals(Direction.NONE))return neighborID;
        else {
            if (m_hashmapVertexes.containsKey(myID)) {
                neighborID = m_hashmapVertexes.get(myID).getNeighborVertexID(direction);
            }
            return neighborID;
        }
    }

    public boolean isNeighborDirectionWall(int myID,Direction direction)
    {
        if(direction.equals(Direction.NONE))return false;
        else {
            try {
                int neighborID = getNeighborID(myID, direction);
                return m_hashmapVertexes.get(neighborID).isM_wall();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

    }



    public boolean wasVertexNeighborVisited(int myID,Direction direction)
    {
        if(direction.equals(Direction.NONE))return false;
        else{
            try {
                int neighborID=getNeighborID(myID,direction);
                return m_hashmapVertexes.get(neighborID).isM_visited();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }


    }


    public boolean isNeighborOutOfBounds(int myID,Direction direction)
    {
        int neighborID=getNeighborID(myID,direction);
        if(m_hashmapVertexes.containsKey(neighborID))
        {
            return false;
        }
        else
        {
            return true;
        }

    }



    public void setNeighborInDirectionVisited(int myID,Direction direction,boolean bVisited)
    {
        int neighborID=getNeighborID(myID,direction);
        setVertexVisited(neighborID,bVisited);
    }

    private int getNeighborInDirection(int myID,Direction direction, int distance) {

        for (int i = 0; i < distance; i++) {
            myID = getNeighborID(myID, direction);
        }
        return myID;
    }



    public void setNeighborInDirectionAsWall(int myID,Direction direction, int hops){
        int ID = getNeighborInDirection(myID,direction,hops);
        setVertexWall(ID,true);
    }


    private void boundNeighbors(int X, int Y, Vertex v) {
        if(Y==0){
            //SOUTH WALL
            v.setM_neighbor(Direction.SOUTH,-1);
            v.setM_neighbor(Direction.SOUTHEAST,-1);
            v.setM_neighbor(Direction.SOUTHWEST,-1);
        }
        if(X==0){
            //WEST WALL
            v.setM_neighbor(Direction.WEST,-1);
            v.setM_neighbor(Direction.SOUTHWEST,-1);
            v.setM_neighbor(Direction.NORTHWEST,-1);
        }
        if(Y==GraphProperties.N_NODES_IN_COLUMNS-1){
            //NORTH WALL
            v.setM_neighbor(Direction.NORTH,-1);
            v.setM_neighbor(Direction.NORTHWEST,-1);
            v.setM_neighbor(Direction.NORTHEAST,-1);
        }
        if(X==GraphProperties.N_NODES_IN_ROWS-1){
            //EAST WALL
            v.setM_neighbor(Direction.EAST,-1);
            v.setM_neighbor(Direction.NORTHEAST,-1);
            v.setM_neighbor(Direction.SOUTHEAST,-1);
        }
    }
    private void mapNeighbors(Vertex v,int operatingNode)
    {
        List<Direction> directionList = Arrays.asList(Direction.values());
        for (Direction d:directionList)
        {
            if(d.equals(Direction.NORTH)) {
                v.setM_neighbor(Direction.NORTH,operatingNode+GraphProperties.N_NODES_IN_ROWS);
            }
            if(d.equals(Direction.SOUTH)) {
                v.setM_neighbor(Direction.SOUTH,operatingNode-GraphProperties.N_NODES_IN_ROWS);
            }
            if(d.equals(Direction.EAST)) {
                v.setM_neighbor(Direction.EAST,operatingNode+1);
            }
            if(d.equals(Direction.WEST)) {
                v.setM_neighbor(Direction.WEST,operatingNode-1);
            }
            if(d.equals(Direction.NORTHEAST)) {
                v.setM_neighbor(Direction.NORTHEAST,operatingNode+GraphProperties.N_NODES_IN_ROWS+1);
            }
            if(d.equals(Direction.NORTHWEST)) {
                v.setM_neighbor(Direction.NORTHWEST,operatingNode+GraphProperties.N_NODES_IN_ROWS-1);
            }
            if(d.equals(Direction.SOUTHEAST)) {
                v.setM_neighbor(Direction.SOUTHEAST,operatingNode-GraphProperties.N_NODES_IN_ROWS+1);
            }
            if(d.equals(Direction.SOUTHWEST)) {
                v.setM_neighbor(Direction.SOUTHWEST,operatingNode-GraphProperties.N_NODES_IN_ROWS-1);
            }


        }
    }


    private Coordinates2D calculateNewLocation(int i, int j)
    {

        return new Coordinates2D( j*GraphProperties.NODE_X_DISTANCE, -i*GraphProperties.NODE_Y_DISTANCE+(GraphProperties.DELTA_Y-GraphProperties.NODE_Y_DISTANCE));
    }

    private void setUpperLocationBounds()
    {
        m_Upper_X_Location=(GraphProperties.N_NODES_IN_COLUMNS-1)*GraphProperties.NODE_X_DISTANCE;
        m_Upper_Y_Location=(GraphProperties.N_NODES_IN_ROWS-1)*GraphProperties.NODE_Y_DISTANCE;
    }


    public boolean AddEdge(String edgeID, int vertexA, int vertexB) {
        //add an edge between vertexes

        if(m_hashmapVertexes.containsKey(vertexA) && m_hashmapVertexes.containsKey(vertexB))
        {
            m_graph.addEdge(edgeID,vertexA, vertexB);
            return true;
        }
        else return false;

    }






}
