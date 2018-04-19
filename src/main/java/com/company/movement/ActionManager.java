package com.company.movement;


import com.company.MainRobot;
import com.company.events.Event;
import com.company.manager.Manager;
import com.company.events.EventCaller;
import com.company.events.IEvent;
import com.company.navigation.Direction;
import org.apache.logging.log4j.Level;

import java.util.HashMap;

public class ActionManager extends Manager implements IEvent {


    private EventCaller m_eventCaller;

    private HashMap<Integer,ActionResult > m_ListActionResult;

    @Override
    public void initialize() {
        super.initialize();

    }


    public ActionManager(MainRobot mainRobot) {
        m_mainRobot = mainRobot;
        m_eventCaller = new EventCaller(m_mainRobot);
        m_ListActionResult = new HashMap<Integer,ActionResult >();

    }



    public void testRobot() {
        writeLog(Level.INFO,"ActionManager:testRobot - Start");
        //Test write to Arduino
        writeLog(Level.INFO,"ActionManager: move NORTH");
        move(Direction.NORTH,10);

        writeLog(Level.INFO,"ActionManager: move SOUTH");
        move(Direction.SOUTH,10);

        writeLog(Level.INFO,"ActionManager: move EAST");
        move(Direction.EAST,10);

        writeLog(Level.INFO,"ActionManager: move WEST");
        move(Direction.WEST,10);
        writeLog(Level.INFO,"ActionManager: look cycle");
        look(0);
        look(45);
        look(90);
        look(135);
        look(180);
        writeLog(Level.INFO,"ActionManager:testRobot - Stop");

    }

    public void move(Direction direction,Integer distance){
        writeLog(Level.INFO,"ActionManager: move called");
        switch(direction){
            case EAST:
            case WEST:
            case NORTHEAST:
            case NORTHWEST:
            case SOUTHEAST:
            case SOUTHWEST: {
                writeLog(Level.INFO,"ActionManager: move - this direction implies a ROTATE");
                rotate(direction.getM_properties().getDegrees());
                writeLog(Level.INFO,"ActionManager: move - move distance:"+distance);
                move(distance);
                break;
            }
            case NORTH:
            case SOUTH:{
                writeLog(Level.INFO,"ActionManager: move - move distance:"+distance);
                move(distance);
                break;
            }
            case NONE:
                default:
                    break;
        }
    }





    public int getNextActionIndex(){
        return m_ListActionResult.size();
    }


    private void addToListActionResult(Action action){
        m_ListActionResult.put(action.getM_actionID(), new ActionResult(action,ActionResult.getDEFAULT_RESULT()));
    }

    public void move(Integer distance){
        Action.ACTION actionEnum=Action.ACTION.CAR_MOVE;
        int index = getNextActionIndex();
        Action action = new Action(index,actionEnum );
        addToListActionResult(action);
        Event event = new Event(index,Action.translateActionToEvent(actionEnum));
        m_eventCaller.prepareCallBack(this,event);
        writeLog(Level.TRACE,"Movement Manager-move called distance:"+distance);
        int move_time= ActionProperties.TIME_MOVE_MULTIPLIER*distance;

        if(distance>=0) {

            m_eventCaller.move(true, ActionProperties.ROT_SPEED_OF_MOVEMENT,move_time);
        }
        else{
            m_eventCaller.move(false, ActionProperties.ROT_SPEED_OF_MOVEMENT,move_time);
        }
        m_eventCaller.waitCallBack(event);
    }


    public void rotate(Integer degrees){
        Action.ACTION actionEnum=Action.ACTION.CAR_ROTATE;
        int index = getNextActionIndex();
        Action action = new Action(index,actionEnum );
        addToListActionResult(action);
        Event event = new Event(index,Action.translateActionToEvent(actionEnum));
        m_eventCaller.prepareCallBack(this,event);

        writeLog(Level.TRACE,"Movement Manager-rotate called degrees:"+degrees);
        int rotation_time= ActionProperties.ROT_MULTIPLIER*degrees;

        if(degrees<0) {
            m_eventCaller.rotate(true, ActionProperties.ROT_SPEED_OF_MOVEMENT,rotation_time);
        }
        else{
            m_eventCaller.rotate(false, ActionProperties.ROT_SPEED_OF_MOVEMENT,rotation_time);
        }
        m_eventCaller.waitCallBack(event);
    }


    public void look(Integer degrees) {
        writeLog(Level.INFO, "Movement Manager-look called, degrees:" + degrees);
        if (degrees > -1 && degrees < 181) {
            Action.ACTION actionEnum=Action.ACTION.TAKE_DISTANCE;
            int index = getNextActionIndex();
            Action action = new Action(index,actionEnum );
            addToListActionResult(action);
            Event event = new Event(index,Action.translateActionToEvent(actionEnum));
            m_eventCaller.prepareCallBack(this,event);

            m_eventCaller.look(degrees);
            m_eventCaller.waitCallBack(event);
        }

    }



    public void testIfArduinoReady() {

        writeLog(Level.INFO,"Test if Arduino is Ready called. Currently is: "+m_mainRobot.getM_StateManager().isM_bIsArduinoReady());
        Action.ACTION actionEnum=Action.ACTION.TEST_IS_READY;
        int index = getNextActionIndex();
        Action action = new Action(index,actionEnum );
        addToListActionResult(action);
        Event event = new Event(index,Action.translateActionToEvent(actionEnum));
        m_eventCaller.addEventCaller(this, event);
        m_eventCaller.isReady();
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
