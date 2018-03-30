package com.company.graph;

public class Orientation {
    public Direction getMy_Direction() {
        return my_Direction;
    }

    public void setMy_Direction(Direction my_Direction) {
        this.my_Direction = my_Direction;
    }

    private Direction my_Direction;

    Orientation(Direction direction){
        my_Direction=direction;
    }

    public Direction getDirectionFromOrientation(Direction directionToConvert){
        int distance = directionToConvert.getDistanceFromDirection(Direction.NORTH);
        Direction dir = my_Direction.getDirectionFromDistance(distance);
        return dir;

    }

    public static void main(String[] args) {
        Direction testDirection = Direction.WEST;
        Orientation orientation = new Orientation(Direction.NORTH);
        System.out.println("Test Orientation");
        for(int i=0;i<10;i++)
        {
            System.out.println(testDirection);
            testDirection=testDirection.getNext();
        }

    }

}
