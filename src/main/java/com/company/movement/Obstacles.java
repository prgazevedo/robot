package com.company.movement;

import com.company.navigation.Direction;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Obstacles {

    private HashMap<Direction,Integer> m_neighbors;


    public Obstacles(HashMap<Direction, Integer> m_neighbors) {
        this.m_neighbors = m_neighbors;
    }

    public void addObstacle(Direction direction, Integer distance){
        m_neighbors.put(direction,distance);
    }

    public HashMap<Direction,Integer> getObstacles()
    {
        return m_neighbors;
    }

    public Map.Entry<Direction,Integer> getNextObstacle(){
        Map.Entry pair = null;
        if(m_neighbors.size()>0) {
            Iterator it = m_neighbors.entrySet().iterator();
            pair = (Map.Entry) it.next();
            it.remove(); // avoids a ConcurrentModificationException
        }
        return pair;
    }



}
