package com.company.movement;

import com.company.WriteThread;
import com.company.navigation.Direction;

import java.util.concurrent.TimeUnit;

public class MovementManager extends Thread{

    /** MoveRobot **/
    private static MoveRobot m_moveRobot=null;

    public MovementManager( MoveRobot moveRobot) {
        m_moveRobot= moveRobot;
    }


    public void init() throws Exception{
        m_moveRobot.checkComms();

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

            m_moveRobot.rotate(true,MovementProperties.ROT_SPEED_OF_MOVEMENT,rotation_time);
        }
        else{
            m_moveRobot.rotate(false,MovementProperties.ROT_SPEED_OF_MOVEMENT,rotation_time);
        }
    }


    private void move(Integer distance){
        int move_time=MovementProperties.TIME_MOVE_MULTIPLIER*distance;

        if(distance>0) {

            m_moveRobot.move(true,MovementProperties.ROT_SPEED_OF_MOVEMENT,move_time);
        }
        else{
            m_moveRobot.move(false,MovementProperties.ROT_SPEED_OF_MOVEMENT,move_time);
        }
    }
}
