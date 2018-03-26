package com.company.graph;

import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import javafx.geometry.Point2D;

import java.util.HashMap;

public class MapGraph extends edu.uci.ics.jung.graph.SparseMultigraph {

    public Graph getM_graph() {
        return m_graph;
    }

    public StaticLayout getM_layout() {
        return m_layout;
    }

    private StaticLayout m_layout;
    private edu.uci.ics.jung.graph.Graph<Vertex,String> m_graph;
    private HashMap<Integer,Vertex> m_hashmapVertexes;
    private HashMap<Point2D,Integer> m_hashmapLocations;

    public MapGraph(int nvertexes) {
        m_graph = new SparseMultigraph<Vertex,String>();
        m_hashmapVertexes = new HashMap<Integer,Vertex>();
        m_hashmapLocations = new HashMap<Point2D,Integer>();
        m_layout = new StaticLayout(m_graph);
        populateVertexes();
    }

    public Vertex getVertex(int ID){
        return m_hashmapVertexes.get(ID);
    }

    public Integer getVertexId(Point2D location){return m_hashmapLocations.get(location);}

    public void populateVertexes()
    {
        int operatingNode = 0;
        for (int i = 0; i<GraphProperties.N_NODES_IN_COLUMNS; i++) {
            for (int j = 0; j<GraphProperties.N_NODES_IN_ROWS; j++) {
                operatingNode++;
                Point2D location = new Point2D( i*GraphProperties.NODE_X_DISTANCE, j*GraphProperties.NODE_Y_DISTANCE);
                Vertex v = new Vertex(operatingNode, location);
                m_graph.addVertex(v);
                m_hashmapVertexes.put(operatingNode,v);
                m_hashmapLocations.put(location,operatingNode);
                m_layout.setLocation(operatingNode,location.getX(),location.getY());
            }
        }

    }


    public boolean AddEdge(int vertexA, int vertexB) {
        //add an edge between vertexes
        String edgeID = String.valueOf(vertexA) + String.valueOf(vertexB);
        if(m_hashmapVertexes.containsKey(vertexA) && m_hashmapVertexes.containsKey(vertexB))
        {

            m_graph.addEdge(edgeID, m_hashmapVertexes.get(vertexA), m_hashmapVertexes.get(vertexB));
            return true;
        }
        else return false;

    }





    public class Vertex{
        private int m_vertexID;

        public Point2D getM_coords() {
            return m_coords;
        }

        private Point2D m_coords;



        public Vertex(int VertexId, Point2D coords) {
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
