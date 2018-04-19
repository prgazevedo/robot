package com.company.events;

import com.company.comms.CommsProperties;

import java.util.ArrayList;

/**
 * Responsible to convert from CommsProperties to Events
 * Calls EventCaller to dispatch callback
 */
public class EventNotifier
{


    private EventCaller m_caller;


    public EventNotifier (EventCaller caller)
    {
        // Save the event object for later use.
        m_caller=caller;
        // Nothing to report yet.

    }


    public void doCallback(CommsProperties.cmds cmds, ArrayList<String> args)
    {
        Event.EVENT event = convertCmdToEvent(cmds);
        if(event.equals(Event.EVENT.CAR_MOVED)) m_caller.carMoved(convertToBoolean(args.get(0)),Integer.valueOf(args.get(1)),Integer.valueOf(args.get(2)));
        if(event.equals(Event.EVENT.CAR_ROTATED)) m_caller.carRotated(convertToBoolean(args.get(0)),Integer.valueOf(args.get(1)),Integer.valueOf(args.get(2)));
        if(event.equals(Event.EVENT.DISTANCE_TAKEN))  m_caller.distanceTaken(Integer.valueOf(args.get(0)),Integer.valueOf(args.get(1)));
        if(event.equals(Event.EVENT.ISREADY)) m_caller.ackReady();
    }

    private Event.EVENT convertCmdToEvent(CommsProperties.cmds cmds){
        switch(cmds){
            case AckMove: return Event.EVENT.CAR_MOVED;
            case AckRotate: return Event.EVENT.CAR_ROTATED;
            case AckLook: return Event.EVENT.DISTANCE_TAKEN;
            case Acknowledge: return Event.EVENT.ISREADY;
            default: return Event.EVENT.NONE;
        }
    }



    private boolean convertToBoolean(String value) {
        boolean returnValue = false;
        if ("1".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) ||
                "true".equalsIgnoreCase(value) || "on".equalsIgnoreCase(value))
            returnValue = true;
        return returnValue;
    }

    // ...
}
