package com.company.graph;

import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import javafx.geometry.Point2D;

import java.util.HashMap;

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
        for (int i = 0; i<GraphProperties.N_NODES_IN_COLUMNS; i++) {
            for (int j = 0; j<GraphProperties.N_NODES_IN_ROWS; j++) {

                Point2D location = calculateNewLocation(i,j);
                m_graph.addVertex(operatingNode);
                Vertex v = new Vertex(operatingNode, location);
                m_hashmapVertexes.put(operatingNode,v);
                m_hashmapLocations.put(location,operatingNode);
                m_layout.setLocation(operatingNode,location.getX(),location.getY());
                operatingNode++;
            }
        }
        setUpperLocationBounds();



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





    public class Vertex{
        private int m_vertexID;
        private Point2D m_coords;
        private boolean m_visited;
        private boolean m_wall;

        public boolean isM_visited() {
            return m_visited;
        }

        public void setM_visited(boolean m_visited) {
            this.m_visited = m_visited;
        }


        public boolean isM_wall() {
            return m_wall;
        }

        public void setM_wall(boolean m_wall) {
            this.m_wall = m_wall;
        }

        public Point2D getM_coords() {
            return m_coords;
        }



        public Vertex(int VertexId, Point2D coords) {
            m_wall = false;
            m_visited = false;
            m_vertexID = VertexId;
            this.m_coords = new Point2D(coords.getX(),coords.getY());

        }


        public boolean equals(Object o) {

            return o instanceof Integer && m_vertexID == ((Integer) o).intValue();

        }

        public int hashCode() {
            return m_vertexID;
        }



    }
}
