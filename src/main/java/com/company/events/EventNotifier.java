package com.company.events;

import com.company.movement.IMovement;
import com.company.navigation.Direction;

import java.util.ArrayList;

public class EventNotifier
{


    private EventCaller m_caller;
    public EventNotifier (EventCaller caller)
    {
        // Save the event object for later use.
        m_caller=caller;
        // Nothing to report yet.

    }


    public void doWork(ArrayList<String> args)
    {
        System.out.println("doWork called");
        //TODO for now hardcoded
        doCallback(IEvent.EVENT.CAR_MOVED,1,10);
    }


    public void doCallback (IEvent.EVENT event, Object ... args)
    {
        System.out.println("doCallBAck called");

       if(event.equals(IEvent.EVENT.CAR_MOVED)){
           m_caller.carMoved((boolean)args[0],(int)args[1]);
       }


    }


    // ...
}
