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


    /** Rotation boundaries*/
    public static final int MAXIMUM_ARDUINO_SERVO_ROTATION = 170;
    public static final int MINIMUM_ARDUINO_SERVO_ROTATION = 10;

    public static boolean convertDistanceToFwdMoveDirection(int distance){
        if(distance>0) return true;
        else return false;
    }

    public static boolean convertDegreesToRotateWestDirection(int degrees){
        if(degrees<0) return true;
        else return false;
    }


    public static int convertLookDegreesToArduino(int degrees){
        //avoid extreme rotations in arduino
        int converted_degrees_to_send_arduino= (-degrees+90);
        if( converted_degrees_to_send_arduino >= MAXIMUM_ARDUINO_SERVO_ROTATION) return MAXIMUM_ARDUINO_SERVO_ROTATION;
        else if( converted_degrees_to_send_arduino < MINIMUM_ARDUINO_SERVO_ROTATION) return MINIMUM_ARDUINO_SERVO_ROTATION;
        else return converted_degrees_to_send_arduino;
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


    public boolean canPerformLook(int degrees){
        if (degrees >= -90 && degrees <= 90) return true;
        else return false;
    }

}
