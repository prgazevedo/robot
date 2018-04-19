package com.company.movement;


import com.company.MainRobot;
import com.company.manager.Manager;
import com.company.events.EventCaller;
import com.company.events.IEvent;
import com.company.navigation.Direction;
import com.company.state.State;
import com.company.state.StateManager;
import org.apache.logging.log4j.Level;

public class MovementManager extends Manager implements IEvent {


    private EventCaller m_eventCaller;



    @Override
    public void initialize() {
        super.initialize();

    }


    public MovementManager(MainRobot mainRobot) {
        m_mainRobot = mainRobot;
        m_eventCaller = new EventCaller(m_mainRobot);

    }



    public void testEngines() {
        writeLog(Level.INFO,"MovementManager:testEngines");
        //Test write to Arduino
        writeLog(Level.INFO,"MovementManager: move NORTH");
        move(Direction.NORTH,10);

        writeLog(Level.INFO,"MovementManager: move SOUTH");
        move(Direction.SOUTH,10);

        writeLog(Level.INFO,"MovementManager: move EAST");
        move(Direction.EAST,10);

        writeLog(Level.INFO,"MovementManager: move WEST");
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
                writeLog(Level.INFO,"MovementManager: move implies a ROTATE");
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
        m_eventCaller.prepareCallBack(this,EVENT.CAR_ROTATED);
        //m_eventCaller.addEventCaller(this, EVENT.CAR_ROTATED);
        writeLog(Level.INFO,"Movement Manager-rotate called degrees:"+degrees);
        int rotation_time=MovementProperties.ROT_MULTIPLIER*degrees;

        if(degrees<0) {
            m_eventCaller.rotate(true,MovementProperties.ROT_SPEED_OF_MOVEMENT,rotation_time);
        }
        else{
            m_eventCaller.rotate(false,MovementProperties.ROT_SPEED_OF_MOVEMENT,rotation_time);
        }
        m_eventCaller.waitCallBack(EVENT.CAR_ROTATED);
    }



    public void move(Integer distance){
        m_eventCaller.prepareCallBack(this,EVENT.CAR_MOVED);
        //m_eventCaller.addEventCaller(this, IEvent.EVENT.CAR_MOVED);
        writeLog(Level.INFO,"Movement Manager-move called distance:"+distance);
        int move_time=MovementProperties.TIME_MOVE_MULTIPLIER*distance;

        if(distance>0) {

            m_eventCaller.move(true,MovementProperties.ROT_SPEED_OF_MOVEMENT,move_time);
        }
        else{
            m_eventCaller.move(false,MovementProperties.ROT_SPEED_OF_MOVEMENT,move_time);
        }
        m_eventCaller.waitCallBack(EVENT.CAR_MOVED);
    }

    public void look(Integer degrees){
        m_eventCaller.prepareCallBack(this,EVENT.DISTANCE_TAKEN);
        //m_eventCaller.addEventCaller(this, EVENT.DISTANCE_TAKEN);
        writeLog(Level.INFO,"Movement Manager-look called, degrees:"+degrees);
        if(degrees>0 && degrees<180) {
            m_eventCaller.look(degrees);
        }
        m_eventCaller.waitCallBack(EVENT.DISTANCE_TAKEN);
    }

    @Override
    public synchronized void carMoved(boolean fwd, int speed, int time) {
        writeLog(Level.INFO,"MOVED:"+fwd+",Speed:"+speed+",Time:"+time);
    }

    @Override
    public synchronized void carRotated(boolean fwd, int speed, int time)  {
        writeLog(Level.INFO,"ROTATED:"+fwd+",Speed:"+speed+",Time:"+time);
    }

    @Override
    public synchronized void distanceTaken(int degrees, int distance) {
        writeLog(Level.INFO,"DISTANCE TAKEN:"+distance+",degrees:"+degrees+" distance:"+distance);
    }

    @Override
    public void ackReady() { throw new UnsupportedOperationException(); }


}
