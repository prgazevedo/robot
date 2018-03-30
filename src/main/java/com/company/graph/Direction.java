package com.company.graph;

import javafx.util.Pair;

public enum Direction {

    NONE(-1, new Pair(0, 0)),
    NORTH(0, new Pair(0, GraphProperties.NODE_Y_DISTANCE)),
    SOUTH(1, new Pair(0, -GraphProperties.NODE_Y_DISTANCE)),
    WEST(2, new Pair(-GraphProperties.NODE_X_DISTANCE, 0)),
    EAST(3, new Pair(GraphProperties.NODE_X_DISTANCE, 0)),
    NORTHWEST(4, new Pair(-GraphProperties.NODE_X_DISTANCE, GraphProperties.NODE_Y_DISTANCE)),
    NORTHEAST(5, new Pair(+GraphProperties.NODE_X_DISTANCE, GraphProperties.NODE_Y_DISTANCE)),
    SOUTHWEST(6, new Pair(-GraphProperties.NODE_X_DISTANCE, -GraphProperties.NODE_Y_DISTANCE)),
    SOUTHEAST(7, new Pair(+GraphProperties.NODE_X_DISTANCE, -GraphProperties.NODE_Y_DISTANCE));

    private final int m_index;
    private final javafx.util.Pair<Integer,Integer> m_coordinates;

    Direction(int index, Pair<Integer,Integer> direction) {
        this.m_index = index;
        this.m_coordinates = direction;
    }

    public static Direction getDirection(int i){
        if(i<Direction.values().length && i>=NONE.m_index)
            return Direction.values()[i];
        else return Direction.NONE;
    }

    public int index(){return m_index;}
    public static javafx.util.Pair<Integer,Integer> navigationCoordinates(int i){
        return getDirection(i).m_coordinates;
    }
    public Pair direction(){return m_coordinates;}


    public static int getNumberDirections()
    {
        return Direction.values().length;
    }



    public int getX() {
        return m_coordinates.getKey();
    }
    public float getY() {
        return m_coordinates.getValue();
    }


}
