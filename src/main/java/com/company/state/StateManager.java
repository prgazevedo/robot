package com.company.state;



import com.company.MainRobot;
import com.company.events.IEvent;
import com.company.manager.IManager;
import com.company.manager.Manager;
import org.apache.logging.log4j.Level;

import java.util.HashMap;

public class StateManager extends Manager implements IManager,IEvent{

    private boolean m_bIsArduinoReady;

    private HashMap<IEvent,State> m_StateMap;
    public boolean isM_bIsArduinoReady() {
        return m_bIsArduinoReady;
    }



    public StateManager(MainRobot mainRobot) {
        m_mainRobot=mainRobot;
        m_StateMap = new HashMap<IEvent,State>();
    }


    public State getStateCaller(IEvent caller){
        try {
            return m_StateMap.get(caller);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return State.NONE;
    }

    public void updateStateCaller(IEvent caller, State state){
        writeLog(Level.INFO,"addStateCaller called: "+state.toString());
        if(m_StateMap.containsKey(caller)) m_StateMap.replace(caller,state);
        else m_StateMap.put(caller,state);
    }


    @Override
    public void initialize() {
        super.initialize();
        m_bIsArduinoReady=false;
    }

    public void testIfArduinoReady() {

        writeLog(Level.INFO,"Test if Arduino is Ready called. Currently is: "+m_bIsArduinoReady);
        m_mainRobot.getM_EventCaller().addEventCaller(this, IEvent.EVENT.ISREADY);
        m_mainRobot.getM_EventCaller().isReady();
    }



    @Override
    public synchronized void carMoved(boolean fwd, int speed, int time) { throw new UnsupportedOperationException(); }
    @Override
    public synchronized void carRotated(boolean fwd, int speed, int time)  { throw new UnsupportedOperationException(); }
    @Override
    public synchronized void distanceTaken(int degrees, int distance) { throw new UnsupportedOperationException(); }

    @Override
    public void ackReady() {
        m_bIsArduinoReady=true;
        writeLog(Level.INFO,"Arduino is Ready");
    }

}
