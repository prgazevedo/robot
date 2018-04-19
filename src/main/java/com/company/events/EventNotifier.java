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
        IEvent.EVENT event = convertCmdToEvent(cmds);
        if(event.equals(IEvent.EVENT.CAR_MOVED)) m_caller.carMoved(convertToBoolean(args.get(0)),Integer.valueOf(args.get(1)),Integer.valueOf(args.get(2)));
        if(event.equals(IEvent.EVENT.CAR_ROTATED)) m_caller.carRotated(convertToBoolean(args.get(0)),Integer.valueOf(args.get(1)),Integer.valueOf(args.get(2)));
        if(event.equals(IEvent.EVENT.DISTANCE_TAKEN))  m_caller.distanceTaken(Integer.valueOf(args.get(0)),Integer.valueOf(args.get(1)));
        if(event.equals(IEvent.EVENT.ISREADY)) m_caller.ackReady();
    }

    private IEvent.EVENT convertCmdToEvent(CommsProperties.cmds cmds){
        switch(cmds){
            case AckMove: return IEvent.EVENT.CAR_MOVED;
            case AckRotate: return IEvent.EVENT.CAR_ROTATED;
            case AckLook: return IEvent.EVENT.DISTANCE_TAKEN;
            case Acknowledge: return IEvent.EVENT.ISREADY;
            default: return IEvent.EVENT.NONE;
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
