package com.company.state;



import com.company.MainRobot;
import com.company.events.Event;
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

        if(m_StateMap.containsKey(caller)) {
            State oldState = m_StateMap.get(caller);
            writeLog(Level.INFO,"updateStateCaller replace state called. OldState:"+oldState+" NewState:"+state.toString());
            m_StateMap.replace(caller,state);
        }
        else
        {
            m_StateMap.put(caller,state);
            writeLog(Level.INFO,"updateStateCaller new state inserted: "+state.toString());
        }
    }


    @Override
    public void initialize() {
        super.initialize();
        m_bIsArduinoReady=false;
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
