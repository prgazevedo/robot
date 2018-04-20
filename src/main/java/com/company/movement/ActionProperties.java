package com.company.movement;

public class ActionProperties {
    /** Default distance*/
    public static final int DEFAULT_DISTANCE = 10;
    /** Hop distance*/
    public static final int HOP_DISTANCE = 10;
    /** Strength of Engines*/
    public static final int SPEED_OF_MOVEMENT = 100;
    /** Milliseconds to move multiplier for seconds to obtain distance in */
    public static final int TIME_MOVE_MULTIPLIER = 100;

    /** Rotation Engine Strength*/
    public static final int ROT_SPEED_OF_MOVEMENT = 100;
    /** Rotation Milliseconds to move Multiplier for degree*/
    public static final int ROT_MULTIPLIER = 10;

    public static boolean convertDistanceToFwdMoveDirection(int distance){
        if(distance>0) return true;
        else return false;
    }

    public static boolean convertDegreesToRotateWestDirection(int degrees){
        if(degrees<0) return true;
        else return false;
    }

    public static int convertDegreesToRotationTime(int degrees){
        return ActionProperties.ROT_MULTIPLIER*degrees;
    }

    public static int convertDistanceToMoveTime(int distance){
        return ActionProperties.TIME_MOVE_MULTIPLIER*distance;
    }
    public static int convertDistanceToHops(int distance){
        return distance/ActionProperties.HOP_DISTANCE;
    }


    public boolean performLook(int degrees){
        if (degrees > -1 && degrees < 181) return true;
        else return false;
    }

}
