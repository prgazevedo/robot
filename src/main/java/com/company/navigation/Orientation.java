package com.company.navigation;

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
        int distance = getDistanceFromDirections(Direction.NORTH,directionToConvert);
        Direction dir = getDirectionFromDistance(my_Direction,distance);
        return dir;

    }


    public static int getDistanceFromDirections(Direction fixedDirection, Direction relativeDirection){
        Direction testDirection = relativeDirection;
        for(int i=0;i<testDirection.getNumberValidDirections();i++)
        {
            if(fixedDirection.equals(testDirection)) return i;
            else {
                testDirection=testDirection.getNext();
                if(testDirection.equals(Direction.NONE)){
                    testDirection=testDirection.getNext();
                }
            }
        }
        return -1;
    }

    public Direction getDirectionFromDistance(Direction startDirection,int distance){
        int cycleNumber=startDirection.getNumberValidDirections()-distance;
        Direction testDirection =startDirection;
        for(int i=0;i<cycleNumber;i++)
        {
            testDirection=testDirection.getNext();
            if(testDirection.equals(Direction.NONE)){
                testDirection=testDirection.getNext();
            }
        }
        return testDirection;

    }



}
