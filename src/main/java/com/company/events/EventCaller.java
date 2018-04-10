package com.company.events;

import com.company.ApplicationProperties;
import com.company.MainRobot;
import com.company.Manager;
import com.company.WorkingThreads.ThreadManager;
import com.company.movement.IMovement;
import com.company.navigation.Direction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;

import java.util.ArrayList;
import java.util.HashMap;

public class EventCaller extends Manager implements IMovement,IEvent{

    private ThreadManager m_threadManager;
    private HashMap<EVENT,IEvent> m_CallerMap;
    private EventNotifier en;
    public EventCaller (MainRobot mainRobot)
    {
        m_threadManager= mainRobot.getM_ThreadManager();
        m_CallerMap = new HashMap<EVENT,IEvent>();

    }


    public void addEventCaller(IEvent caller,IEvent.EVENT event){
        System.out.println("addEventCaller move called");
        m_CallerMap.put(event,caller);
        // Create the event notifier and pass ourself to it.
        en = new EventNotifier (this);
        //make the call
        m_threadManager.getM_monitorThread().notifyME(en);

    }


    public IEvent getCalleeFromEvent(IEvent.EVENT key){
        return m_CallerMap.get(key);
    }
    //...
    @Override
    public void carMoved (boolean fwd,int speed, int time){
        System.out.println("EventCaller cardMoved called");
        getCalleeFromEvent(EVENT.CAR_MOVED).carMoved(fwd,speed,time);
    }
    @Override
    public void carRotated (boolean left,int speed, int time){

    }
    @Override
    public void distanceTaken (int angle,int distance ){

    }

    @Override
    public void move(boolean fwd_direction, int speed, int time) {
        System.out.println("EventCaller move called");
        m_threadManager.getM_writeThread().move(fwd_direction, speed, time);
    }

    @Override
    public void rotate(boolean west_direction,int speed, int time ){
        m_threadManager.getM_writeThread().rotate(west_direction,speed,time);
    }

    @Override
    public void look(int degrees) {
        m_threadManager.getM_writeThread().look(degrees);
    }
}
