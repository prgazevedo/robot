package com.company.events;

public interface IEvent
{
    // Callback methods
    public void carMoved (boolean fwd,int speed, int time);
    public void carRotated (boolean left,int speed, int time);
    public void distanceTaken (int angle,int distance );
    public void ackReady();


}