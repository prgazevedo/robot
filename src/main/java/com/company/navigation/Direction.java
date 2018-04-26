package com.company.navigation;


import com.company.graph.Coordinates2D;
import com.company.graph.GraphProperties;



public enum Direction {

    NONE(-1, new DirectionProperties(0,new Coordinates2D(0, 0))),
    NORTH(0, new DirectionProperties(0,new Coordinates2D(0, GraphProperties.NODE_Y_DISTANCE))),
    NORTHEAST(1, new DirectionProperties(45,new Coordinates2D(+GraphProperties.NODE_X_DISTANCE, GraphProperties.NODE_Y_DISTANCE))),
    EAST(2, new DirectionProperties(90,new Coordinates2D(GraphProperties.NODE_X_DISTANCE, 0))),
    SOUTHEAST(3, new DirectionProperties(125,new Coordinates2D(+GraphProperties.NODE_X_DISTANCE, -GraphProperties.NODE_Y_DISTANCE))),
    SOUTH(4, new DirectionProperties(180,new Coordinates2D(0, -GraphProperties.NODE_Y_DISTANCE))),
    SOUTHWEST(5, new DirectionProperties(-125,new Coordinates2D(-GraphProperties.NODE_X_DISTANCE, -GraphProperties.NODE_Y_DISTANCE))),
    WEST(6, new DirectionProperties(-90,new Coordinates2D(-GraphProperties.NODE_X_DISTANCE, 0))),
    NORTHWEST(7, new DirectionProperties(-45,new Coordinates2D(-GraphProperties.NODE_X_DISTANCE, GraphProperties.NODE_Y_DISTANCE)));




    private final int m_index;
    private final DirectionProperties m_properties;

    public DirectionProperties getM_properties() { return m_properties; }
    public int getM_index(){return m_index;}
    public Coordinates2D getDirection(){return m_properties.getM_coordinates();}
    public double getX() {
        return m_properties.getM_coordinates().getX();
    }
    public double getY() {
        return m_properties.getM_coordinates().getY();
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
        private final Coordinates2D m_coordinates;

        public Integer getDegrees() {
            return degrees;
        }

        public Coordinates2D getM_coordinates() {
            return m_coordinates;
        }



        public DirectionProperties(Integer degrees, Coordinates2D m_coordinates) {
            this.degrees = degrees;
            this.m_coordinates = m_coordinates;
        }
    }

}
