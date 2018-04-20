package com.company.events;

public class Event {
    public Event(int eventID, EVENT event) {
        this.eventID = eventID;
        this.event = event;
    }

    private Integer eventID = 0;
    private EVENT event;

    public Integer getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public EVENT getEvent() {
        return event;
    }

    public void setEvent(EVENT event) {
        this.event = event;
    }


    public enum EVENT{

        NONE(0),CAR_MOVED(1), CAR_ROTATED(2), DISTANCE_TAKEN(3), ISREADY(4);
        private int event;
        private EVENT(int i){this.event = i;}
        public EVENT getEvent(){
            if(event<EVENT.values().length && event>0) {
                try {
                    return EVENT.values()[event];
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return EVENT.NONE;
        }

    }
}
