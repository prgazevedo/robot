package com.company.graph;

import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.Graph;

public class MapGraph extends edu.uci.ics.jung.graph.SparseMultigraph {

    public Graph getM_graph() {
        return m_graph;
    }

    private edu.uci.ics.jung.graph.Graph<Integer,String> m_graph;

    public MapGraph(int nvertexes) {
        m_graph = new SparseMultigraph<Integer,String>();
        populateVertexes(nvertexes);
    }

    public void populateVertexes(int nvertexes)
    {
        //idea is to add the vertices and change and the position of each vertex to a coordinate in a grid
        for (int n=0;n<nvertexes;n++) {
            m_graph.addVertex(n);
        }
    }

    public boolean AddEdge(int vertexA, int vertexB) {
        //add an edge between vertexes
        String edgeID = String.valueOf(vertexA) + String.valueOf(vertexB);
        if(m_graph.containsVertex(vertexA) && m_graph.containsVertex(vertexB))
        {
            m_graph.addEdge(edgeID, vertexA, vertexB);
            return true;
        }
        else return false;

    }
}
