package com.company.navigation;


import javafx.util.Pair;


public enum Direction {

    NONE(-1, new DirectionProperties(0,new Pair(0, 0))),
    NORTH(0, new DirectionProperties(0,new Pair(0, GraphProperties.NODE_Y_DISTANCE))),
    NORTHEAST(1, new DirectionProperties(45,new Pair(+GraphProperties.NODE_X_DISTANCE, GraphProperties.NODE_Y_DISTANCE))),
    EAST(2, new DirectionProperties(90,new Pair(GraphProperties.NODE_X_DISTANCE, 0))),
    SOUTHEAST(3, new DirectionProperties(125,new Pair(+GraphProperties.NODE_X_DISTANCE, -GraphProperties.NODE_Y_DISTANCE))),
    SOUTH(4, new DirectionProperties(180,new Pair(0, -GraphProperties.NODE_Y_DISTANCE))),
    SOUTHWEST(5, new DirectionProperties(225,new Pair(-GraphProperties.NODE_X_DISTANCE, -GraphProperties.NODE_Y_DISTANCE))),
    WEST(6, new DirectionProperties(270,new Pair(-GraphProperties.NODE_X_DISTANCE, 0))),
    NORTHWEST(7, new DirectionProperties(325,new Pair(-GraphProperties.NODE_X_DISTANCE, GraphProperties.NODE_Y_DISTANCE)));




    private final int m_index;
    private final DirectionProperties m_properties;

    public DirectionProperties getM_properties() { return m_properties; }
    public int getM_index(){return m_index;}
    public Pair getDirection(){return m_properties.getM_coordinates();}
    public int getX() {
        return m_properties.getM_coordinates().getKey();
    }
    public float getY() {
        return m_properties.getM_coordinates().getValue();
    }
    public static int getNumberDirections()
    {
        return Direction.values().length;
    }
    public static int getNumberValidDirections()
    {
        return Direction.values().length-1;
    }


    Direction(int index, DirectionProperties properties) {
        this.m_index = index;
        this.m_properties = properties;
    }

    public static Direction getDirection(int i){
        if(i<Direction.values().length && i>0)
            try {
                return Direction.values()[i];
            } catch (Exception e) {
                e.printStackTrace();
        }
        return Direction.NONE;
    }
    public Direction getNext() {
        int myOrdinal=ordinal();
        return values()[(myOrdinal + 1) % getNumberDirections()];
    }


    public static class DirectionProperties{
        private final Integer degrees;
        private final javafx.util.Pair<Integer,Integer> m_coordinates;

        public Integer getDegrees() {
            return degrees;
        }

        public Pair<Integer, Integer> getM_coordinates() {
            return m_coordinates;
        }



        public DirectionProperties(Integer degrees, Pair<Integer, Integer> m_coordinates) {
            this.degrees = degrees;
            this.m_coordinates = m_coordinates;
        }
    }

}
