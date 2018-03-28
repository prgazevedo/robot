package com.company.graph;

import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import javafx.geometry.Point2D;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MapGraph extends edu.uci.ics.jung.graph.SparseMultigraph {



    private StaticLayout m_layout;
    private edu.uci.ics.jung.graph.Graph<Integer,String> m_graph;
    private HashMap<Integer,Vertex> m_hashmapVertexes;
    private HashMap<Point2D,Integer> m_hashmapLocations;
    private int m_Upper_X_Location = 0;
    private int m_Upper_Y_Location = 0;

    public Graph getM_graph() {
        return m_graph;
    }

    public StaticLayout getM_layout() {
        return m_layout;
    }

    public int getM_Upper_X_Location() {
        return m_Upper_X_Location;
    }

    public int getM_Upper_Y_Location() {
        return m_Upper_Y_Location;
    }



    public MapGraph(int nvertexes) {
        m_graph = new SparseMultigraph<Integer,String>();
        m_hashmapVertexes = new HashMap<Integer,Vertex>();
        m_hashmapLocations = new HashMap<Point2D,Integer>();
        m_layout = new StaticLayout(m_graph);
        populateVertexes();
    }

    public Vertex getVertex(int ID){
        return m_hashmapVertexes.get(ID);
    }

    public boolean wasVertexVisited(int ID){
        return m_hashmapVertexes.get(ID).isM_visited();
    }

    public boolean isVertexWall(int ID){
        return m_hashmapVertexes.get(ID).isM_wall();
    }


    public void setVertexVisited(int ID){
        Vertex v = m_hashmapVertexes.get(ID);
        v.setM_visited(true);
        m_hashmapVertexes.replace(ID,v);
    }

    public void setVertexWall(int ID){
        Vertex v = m_hashmapVertexes.get(ID);
        v.setM_wall(true);
        m_hashmapVertexes.replace(ID,v);
    }

    public Integer getVertexId(Point2D location){return m_hashmapLocations.get(location);}

    public void populateVertexes()
    {
        int operatingNode = 0;
        for (int i = 0; i<GraphProperties.N_NODES_IN_ROWS; i++) {
            for (int j = 0; j<GraphProperties.N_NODES_IN_COLUMNS; j++) {

                Point2D location = calculateNewLocation(i,j);
                m_graph.addVertex(operatingNode);
                Vertex v = new Vertex(operatingNode, location);
                mapNeighbors(v,operatingNode);
                m_hashmapVertexes.put(operatingNode,v);
                m_hashmapLocations.put(location,operatingNode);
                m_layout.setLocation(operatingNode,location.getX(),location.getY());
                operatingNode++;

            }
        }
        setUpperLocationBounds();



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


    private Point2D calculateNewLocation(int i, int j)
    {
        return new Point2D( i*GraphProperties.NODE_X_DISTANCE, j*GraphProperties.NODE_Y_DISTANCE);
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
