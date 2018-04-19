package com.company.events;

import com.company.MainRobot;
import com.company.manager.Manager;
import com.company.WorkingThreads.ThreadManager;
import com.company.movement.IMovement;
import com.company.state.State;
import com.company.state.StateManager;
import org.apache.logging.log4j.Level;

import java.util.HashMap;

/**
 * Responsible to record the caller
 * Dispatches the request to writeThread
 * Dispatches the callback to the caller when is received from Notifier
 */
public class EventCaller extends Manager implements IMovement,IEvent{

    private ThreadManager m_threadManager;
    private StateManager m_StateManager;
    private HashMap<EVENT,IEvent> m_CallerMap;
    private EventNotifier en;
    public EventCaller (MainRobot mainRobot)
    {
        m_mainRobot = mainRobot;
        m_threadManager= mainRobot.getM_ThreadManager();
        m_StateManager= mainRobot.getM_StateManager();
        m_CallerMap = new HashMap<EVENT,IEvent>();

    }

    @Override
    public void initialize() {
        super.initialize();

    }

    @Override
    public void writeLog(Level messageLevel, String message) {
        super.writeLog(messageLevel, message);
    }

    public void addEventCaller(IEvent caller, IEvent.EVENT event){
        writeLog(Level.INFO,"addEventCaller called: "+event.toString());
        m_CallerMap.put(event,caller);
        // Create the event notifier and pass ourself to it.
        en = new EventNotifier (this);
        //make the call
        m_threadManager.getM_monitorThread().notifyME(en);

    }



    public void waitCallBack(IEvent.EVENT event){
        while (!isAnswerReceived(event)){
            m_mainRobot.getM_ThreadManager().sleep();
        }
    }


    private boolean isAnswerReceived(IEvent.EVENT event){
        IEvent callee = getCalleeFromEvent(event);
        if(m_StateManager.getStateCaller(callee).equals(State.READY)) return true;
        else return false;
    }

    public void prepareCallBack(IEvent caller,IEvent.EVENT event){
        writeLog(Level.INFO,"EventCaller callback requested for Event:"+event.toString());
        addEventCaller(caller, event);
        m_StateManager.updateStateCaller(caller, State.WAITING_ANSWER);
    }


    public IEvent getCalleeFromEvent(IEvent.EVENT event){
        return m_CallerMap.get(event);
    }
    //...
    @Override
    public void carMoved (boolean fwd,int speed, int time){
        writeLog(Level.INFO,"EventCaller Callback:car Moved called");
        IEvent callee = getCalleeFromEvent(EVENT.CAR_MOVED);
        m_StateManager.updateStateCaller(callee,State.READY);
        callee.carMoved(fwd,speed,time);
    }
    @Override
    public void carRotated (boolean left,int speed, int time){
        writeLog(Level.INFO,"EventCaller Callback:car Rotated called");
        IEvent callee = getCalleeFromEvent(EVENT.CAR_ROTATED);
        m_StateManager.updateStateCaller(callee,State.READY);
        callee.carRotated(left,speed,time);
    }
    @Override
    public void distanceTaken (int angle,int distance ){
        writeLog(Level.INFO,"EventCaller Callback:distanceTaken called");
        IEvent callee = getCalleeFromEvent(EVENT.DISTANCE_TAKEN);
        m_StateManager.updateStateCaller(callee,State.READY);
        callee.distanceTaken(angle,distance);
    }

    @Override
    public void ackReady() {
        writeLog(Level.INFO,"EventCaller Callback:isReady called");
        getCalleeFromEvent(EVENT.ISREADY).ackReady();
    }

    @Override
    public void move(boolean fwd_direction, int speed, int time) {
        writeLog(Level.INFO,"EventCaller move called");
        m_threadManager.getM_writeThread().move(fwd_direction, speed, time);
    }

    @Override
    public void rotate(boolean west_direction,int speed, int time ){
        writeLog(Level.INFO,"EventCaller rotate called");
        m_threadManager.getM_writeThread().rotate(west_direction,speed,time);
    }

    @Override
    public void look(int degrees) {
        writeLog(Level.INFO,"EventCaller look called");
        m_threadManager.getM_writeThread().look(degrees);
    }

    public void isReady() {
        writeLog(Level.INFO,"EventCaller isReady called");
        m_threadManager.getM_writeThread().pingArduinoIsReady();
    }


}
