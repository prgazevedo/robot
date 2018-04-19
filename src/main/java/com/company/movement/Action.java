package com.company.movement;

import com.company.events.Event;

public class Action {
    private int m_actionID;
    private ACTION m_action;


    public int getM_actionID() {
        return m_actionID;
    }

    public void setM_actionID(int m_actionID) {
        this.m_actionID = m_actionID;
    }

    public ACTION getM_action() {
        return m_action;
    }

    public void setM_action(ACTION m_action) {
        this.m_action = m_action;
    }

    public Action(int m_actionID, ACTION m_action) {
        this.m_actionID = m_actionID;
        this.m_action = m_action;
    }



    public enum ACTION{
        NONE(0),CAR_MOVE(1), CAR_ROTATE(2), TAKE_DISTANCE(3), TEST_IS_READY(4);
        private int action;
        private ACTION(int i){this.action = i;}
        public ACTION getEvent(){
            if(action<ACTION.values().length && action>0) {
                try {
                    return ACTION.values()[action];
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return ACTION.NONE;
        }



    }

    public static Event.EVENT translateActionToEvent(ACTION action){
        switch (action){
            case NONE:
                return Event.EVENT.NONE;
            case CAR_MOVE:
                return Event.EVENT.CAR_MOVED;
            case CAR_ROTATE:
                return Event.EVENT.CAR_ROTATED;
            case TAKE_DISTANCE:
                return Event.EVENT.DISTANCE_TAKEN;
            case TEST_IS_READY:
                return Event.EVENT.ISREADY;
                default:
                    return Event.EVENT.NONE;
        }
    }
}
