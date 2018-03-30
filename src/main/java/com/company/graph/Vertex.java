package com.company.graph;

import javafx.geometry.Point2D;

import java.util.HashMap;
import java.util.Map;

public class Vertex{
    private int m_vertexID;
    private Point2D m_coords;
    private boolean m_visited;
    private boolean m_wall;
    private HashMap<Direction,Integer> m_neighbors;

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
        this.m_neighbors = new HashMap<Direction,Integer>(Direction.getNumberDirections());

    }

    public void setM_neighbors(HashMap<Direction,Integer> neighbors){
        this.m_neighbors=neighbors;
    }

    public void setM_neighbor(Direction direction, int vertexID){
        m_neighbors.put(direction,vertexID);
    }

    public int getNeighborVertexID(Direction direction){
        if(m_neighbors.containsKey(direction)){
           return m_neighbors.get(direction);
        }
        else return -1;

    }

    public String printNeighbors(){
        String s="printNeighbors:";
        for (Map.Entry v:m_neighbors.entrySet()){
            s+=" "+v.getKey().toString()+":"+v.getValue().toString();
        }

        return s;
    }

    public boolean equals(Object o) {

        return o instanceof Integer && m_vertexID == ((Integer) o).intValue();

    }

    public int hashCode() {
        return m_vertexID;
    }



}
