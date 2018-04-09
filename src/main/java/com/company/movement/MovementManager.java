package com.company.movement;


import com.company.MainRobot;
import com.company.Manager;
import com.company.events.EventCaller;
import com.company.events.IEvent;
import com.company.navigation.Direction;
import org.apache.logging.log4j.Level;

public class MovementManager extends Manager {

    /** RobotProxy **/
    private MainRobot m_mainRobot;
    private EventCaller m_eventCaller;



    public MovementManager(MainRobot mainRobot) {
        m_mainRobot = mainRobot;
        m_eventCaller = new EventCaller(m_mainRobot.getM_ThreadManager());

    }



    public void testEngines() {
        writeLog(Level.INFO,"MovementManager:testEngines");
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


    public void rotate(Integer degrees){

        int rotation_time=MovementProperties.ROT_MULTIPLIER*degrees;

        if(degrees<0) {
            m_eventCaller.rotate(true,MovementProperties.ROT_SPEED_OF_MOVEMENT,rotation_time);
        }
        else{
            m_eventCaller.rotate(false,MovementProperties.ROT_SPEED_OF_MOVEMENT,rotation_time);
        }
    }


    public void move(Integer distance){
        m_eventCaller.addEventCaller(m_mainRobot.getM_NavigationManager(), IEvent.EVENT.CAR_MOVED);
        writeLog(Level.INFO,"Movement Manager-move called:"+distance);
        int move_time=MovementProperties.TIME_MOVE_MULTIPLIER*distance;

        if(distance>0) {

            m_eventCaller.move(true,MovementProperties.ROT_SPEED_OF_MOVEMENT,move_time);
        }
        else{
            m_eventCaller.move(false,MovementProperties.ROT_SPEED_OF_MOVEMENT,move_time);
        }
    }

    public void look(Integer degrees){
        if(degrees>0 && degrees<180) {
            m_eventCaller.look(degrees);
        }
    }



}
