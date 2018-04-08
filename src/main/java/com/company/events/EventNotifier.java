package com.company.events;

import com.company.navigation.Direction;

public class EventNotifier
{
    private IEvent ie;
    private boolean somethingHappened;
    public EventNotifier (IEvent event)
    {
        // Save the event object for later use.
        ie = event;
        // Nothing to report yet.
        somethingHappened = false;
    }
    //...


    public void carMoved (boolean fwd,int distance){

    }
    public void carRotated (Direction direction ){

    }
    public void distanceTaken (int distance, Direction direction){

    }



    public void doWork ()
    {
        // Check the predicate, which is set elsewhere.
        if (somethingHappened)
        {
            // Signal the even by invoking the interface's method.
            ie.interestingEvent ();
        }
        //...
    }
    // ...
}
