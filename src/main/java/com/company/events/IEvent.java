package com.company.events;

import com.company.navigation.Direction;
import com.company.state.State;

public interface IEvent
{
    public enum EVENT{
        NONE(0),CAR_MOVED(1), CAR_ROTATED(2), DISTANCE_TAKEN(3), ISREADY(4);
        private int event;
        private EVENT(int i){this.event = i;}
        public EVENT getEvent(){
            if(event<Direction.values().length && event>0) {
                try {
                    return EVENT.values()[event];
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return EVENT.NONE;
        }

    }
    // Callback methods
    public void carMoved (boolean fwd,int speed, int time);
    public void carRotated (boolean left,int speed, int time);
    public void distanceTaken (int angle,int distance );
    public void ackReady();


}