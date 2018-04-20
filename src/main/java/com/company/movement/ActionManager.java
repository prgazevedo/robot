package com.company.movement;


import com.company.MainRobot;
import com.company.events.Event;
import com.company.manager.Manager;
import com.company.events.EventCaller;
import com.company.events.IEvent;
import com.company.navigation.Direction;
import org.apache.logging.log4j.Level;

import java.util.Comparator;
import java.util.HashMap;
import java.util.NavigableMap;
import java.util.TreeMap;

public class ActionManager extends Manager implements IEvent {


    private EventCaller m_eventCaller;
    private ActionProperties m_AP;
    private NavigableMap<Integer,ActionResult > m_ListActionResult;

    @Override
    public void initialize() {
        super.initialize();

    }


    public ActionManager(MainRobot mainRobot) {
        m_mainRobot = mainRobot;
        m_eventCaller = new EventCaller(m_mainRobot);
        m_AP = new ActionProperties();
        m_ListActionResult = new TreeMap<Integer,ActionResult >(new Comparator<Integer>() {
            @Override
            public int compare(Integer i1, Integer i2) {
                // Overriding the compare method to sort the ID
                return i1.compareTo(i2);
            }
        });

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





    private int getNextActionIndex(){
        return m_ListActionResult.size();
    }

    private Integer getLastActionIndex(){
        if(m_ListActionResult.isEmpty()) return 0;
        else return m_ListActionResult.lastKey();
    }

    private ActionResult getLastActionResult(){
        if(m_ListActionResult.isEmpty()) return null;
        else {
           return m_ListActionResult.lastEntry().getValue();
        }
    }

    private HashMap<String,Result> getLastResults(){
        if(m_ListActionResult.isEmpty()) return null;
        else {
            return m_ListActionResult.lastEntry().getValue().getResults();
        }
    }

    public int getLookResult(){
        int distance = getLastActionResult().getResult("distance").getInt();
        return m_AP.convertDistanceToHops(distance);
    }


    private void addToListActionResult(Action action){
        m_ListActionResult.put(action.getM_actionID(), new ActionResult(action));
    }

    private void updateListActionResult(Action action, ActionResult ar){
        m_ListActionResult.replace(action.getM_actionID(), ar);
    }

    public void move(Integer distance){
        Action.ACTION actionEnum=Action.ACTION.CAR_MOVE;
        int index = getNextActionIndex();
        Action action = new Action(index,actionEnum );
        addToListActionResult(action);
        Event event = new Event(index,Action.translateActionToEvent(actionEnum));
        m_eventCaller.prepareCallBack(this,event);
        writeLog(Level.TRACE,"Action Manager-move called distance:"+distance);
        m_eventCaller.move(m_AP.convertDistanceToFwdMoveDirection(distance), m_AP.ROT_SPEED_OF_MOVEMENT,m_AP.convertDistanceToMoveTime(distance));
        m_eventCaller.waitCallBack(event);
    }


    public void rotate(Integer degrees){
        Action.ACTION actionEnum=Action.ACTION.CAR_ROTATE;
        int index = getNextActionIndex();
        Action action = new Action(index,actionEnum );
        addToListActionResult(action);
        Event event = new Event(index,Action.translateActionToEvent(actionEnum));
        m_eventCaller.prepareCallBack(this,event);
        writeLog(Level.TRACE,"Action Manager-rotate called degrees:"+degrees);
        m_eventCaller.rotate(m_AP.convertDegreesToRotateWestDirection(degrees), m_AP.ROT_SPEED_OF_MOVEMENT,m_AP.convertDegreesToRotationTime(degrees));
        m_eventCaller.waitCallBack(event);
    }


    public void look(Integer degrees) {
        if(m_AP.performLook(degrees)){
            Action.ACTION actionEnum=Action.ACTION.TAKE_DISTANCE;
            int index = getNextActionIndex();
            Action action = new Action(index,actionEnum );
            addToListActionResult(action);
            Event event = new Event(index,Action.translateActionToEvent(actionEnum));
            m_eventCaller.prepareCallBack(this,event);
            writeLog(Level.INFO, "Action Manager-look called, degrees:" + degrees);
            m_eventCaller.look(degrees);
            m_eventCaller.waitCallBack(event);
        }
        else {
            writeLog(Level.INFO, "Action Manager-look called,but performLook disallowed action");
        }

    }



    public void testIfArduinoReady() {

        writeLog(Level.INFO,"Test if Arduino is Ready called. Currently is: "+m_mainRobot.getM_StateManager().isM_bIsArduinoReady());
        Action.ACTION actionEnum=Action.ACTION.TEST_IS_READY;
        int index = getNextActionIndex();
        Action action = new Action(index,actionEnum );
        addToListActionResult(action);
        Event event = new Event(index,Action.translateActionToEvent(actionEnum));
        m_eventCaller.prepareCallBack(this,event);
        m_eventCaller.isReady();
        m_eventCaller.waitCallBack(event);
    }


    @Override
    public synchronized void carMoved(boolean fwd, int speed, int time) {
        ActionResult previousAR = getLastActionResult();
        previousAR.setM_ActionResult(ActionResult.ACTION_RESULT.COMPLETED);
        previousAR.addResult("fwd",new Result<>(fwd));
        previousAR.addResult("speed",new Result<>(speed));
        previousAR.addResult("time",new Result<>(time));
        updateListActionResult(previousAR.getM_Action(),previousAR );
        writeLog(Level.INFO,"MOVED:"+fwd+",Speed:"+speed+",Time:"+time);

    }

    @Override
    public synchronized void carRotated(boolean left, int speed, int time)  {
        ActionResult previousAR = getLastActionResult();
        previousAR.setM_ActionResult(ActionResult.ACTION_RESULT.COMPLETED);
        previousAR.addResult("left",new Result<>(left));
        previousAR.addResult("speed",new Result<>(speed));
        previousAR.addResult("time",new Result<>(time));
        updateListActionResult(previousAR.getM_Action(),previousAR );
        writeLog(Level.INFO,"ROTATED:"+left+",Speed:"+speed+",Time:"+time);
    }

    @Override
    public synchronized void distanceTaken(int degrees, int distance) {
        ActionResult previousAR = getLastActionResult();
        previousAR.setM_ActionResult(ActionResult.ACTION_RESULT.COMPLETED);
        previousAR.addResult("degrees",new Result<>(degrees));
        previousAR.addResult("distance",new Result<>(distance));
        updateListActionResult(previousAR.getM_Action(),previousAR );
        writeLog(Level.INFO,"DISTANCE TAKEN:"+distance+",degrees:"+degrees+" distance:"+distance);
    }



    @Override
    public void ackReady() {
        ActionResult previousAR = getLastActionResult();
        previousAR.setM_ActionResult(ActionResult.ACTION_RESULT.COMPLETED);
        previousAR.addResult("ack",new Result<>(true));
        updateListActionResult(previousAR.getM_Action(),previousAR );
        writeLog(Level.INFO,"ARDUINO IS READY");
        m_mainRobot.getM_StateManager().ackReady();
    }


}
