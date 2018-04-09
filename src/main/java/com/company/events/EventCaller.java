package com.company.events;

import com.company.Manager;
import com.company.WorkingThreads.ThreadManager;
import com.company.movement.IMovement;
import com.company.navigation.Direction;

import java.util.ArrayList;
import java.util.HashMap;

public class EventCaller implements IMovement,IEvent{

    private ThreadManager m_threadManager;
    private HashMap<EVENT,IEvent> managerArrayList;
    private EventNotifier en;
    public EventCaller (ThreadManager threadManager)
    {
        m_threadManager= threadManager;

    }


    public void addEventCaller(IEvent caller,IEvent.EVENT event){
        System.out.println("addEventCaller move called");
        managerArrayList.put(event,caller);
        // Create the event notifier and pass ourself to it.
        en = new EventNotifier (this);
        //make the call
        m_threadManager.getM_monitorThread().notifyME(en);

    }


    public IEvent getCalleeFromEvent(IEvent.EVENT key){
        return managerArrayList.get(key);
    }
    //...
    @Override
    public void carMoved (boolean fwd,int distance){
        System.out.println("EventCaller cardMoved called");
        getCalleeFromEvent(EVENT.CAR_MOVED).carMoved(fwd,distance);
    }
    @Override
    public void carRotated (Direction direction ){

    }
    @Override
    public void distanceTaken (int distance, Direction direction){

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
