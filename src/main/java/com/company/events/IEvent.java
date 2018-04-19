package com.company.events;

import com.company.navigation.Direction;
import com.company.state.State;

public interface IEvent
{
    // Callback methods
    public void carMoved (boolean fwd,int speed, int time);
    public void carRotated (boolean left,int speed, int time);
    public void distanceTaken (int angle,int distance );
    public void ackReady();


}