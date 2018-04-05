package com.company.movement;


import com.company.RobotProxy;
import com.company.navigation.Direction;

import java.util.concurrent.TimeUnit;

public class MovementManager {

    /** RobotProxy **/
    private static Movement m_movement =null;

    public MovementManager( Movement movement) {
        m_movement = movement;
    }


    public void init() throws Exception{


        //Test write to Arduino
        move(Direction.NORTH,10);
        TimeUnit.SECONDS.sleep(1);
        move(Direction.SOUTH,10);
        TimeUnit.SECONDS.sleep(1);
        move(Direction.EAST,10);
        TimeUnit.SECONDS.sleep(1);
        move(Direction.WEST,10);
    }

    private void move(Direction direction,Integer distance){
        switch(direction){
            case EAST:
            case WEST:
            case NORTHEAST:
            case NORTHWEST:
            case SOUTHEAST:
            case SOUTHWEST: {
                rotate(direction.getM_properties().getDegrees());
                move(distance);
                break;
            }
            case NORTH:
            case SOUTH:{
                move(distance);
                break;
            }
            case NONE:
                default:
                    break;
        }
    }

    private void rotate(Integer degrees){
        int rotation_time=MovementProperties.ROT_MULTIPLIER*degrees;

        if(degrees<0) {

            m_movement.rotate(true,MovementProperties.ROT_SPEED_OF_MOVEMENT,rotation_time);
        }
        else{
            m_movement.rotate(false,MovementProperties.ROT_SPEED_OF_MOVEMENT,rotation_time);
        }
    }


    private void move(Integer distance){
        int move_time=MovementProperties.TIME_MOVE_MULTIPLIER*distance;

        if(distance>0) {

            m_movement.move(true,MovementProperties.ROT_SPEED_OF_MOVEMENT,move_time);
        }
        else{
            m_movement.move(false,MovementProperties.ROT_SPEED_OF_MOVEMENT,move_time);
        }
    }
}
