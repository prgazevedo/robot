package com.company.movement;


import com.company.MainRobot;
import com.company.WorkingThreads.RobotProxy;
import com.company.navigation.Direction;

public class MovementManager {

    /** RobotProxy **/
    private MainRobot m_mainRobot;
    private RobotProxy m_roboProxy;
    public  void writeLog(org.apache.logging.log4j.Level messageLevel,String message){ m_mainRobot.writeLog(messageLevel,message); }


    public MovementManager(MainRobot mainRobot) {
        m_mainRobot = mainRobot;
        m_roboProxy = mainRobot.getM_Proxy();
    }


    public void test() {

        //Test write to Arduino
        move(Direction.NORTH,10);

        move(Direction.SOUTH,10);

        move(Direction.EAST,10);

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
            m_roboProxy.rotate(true,MovementProperties.ROT_SPEED_OF_MOVEMENT,rotation_time);
        }
        else{
            m_roboProxy.rotate(false,MovementProperties.ROT_SPEED_OF_MOVEMENT,rotation_time);
        }
    }


    private void move(Integer distance){
        int move_time=MovementProperties.TIME_MOVE_MULTIPLIER*distance;

        if(distance>0) {

            m_roboProxy.move(true,MovementProperties.ROT_SPEED_OF_MOVEMENT,move_time);
        }
        else{
            m_roboProxy.move(false,MovementProperties.ROT_SPEED_OF_MOVEMENT,move_time);
        }
    }
}
