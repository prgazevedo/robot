package com.company.events;

import com.company.navigation.Direction;

public interface IEvent
{
    // This is just a regular method so it can return something or
    // take arguments if you like.
    public void carMoved (boolean fwd,int distance);
    public void carRotated (Direction direction );
    public void distanceTaken (int distance, Direction direction);
}