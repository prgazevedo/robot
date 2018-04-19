package com.company.movement;

import com.company.events.Event;
import com.company.events.IEvent;

public interface IAction {

    public void move(boolean fwd_direction,int speed, int time );
    public void rotate(boolean west_direction,int speed, int time );
    public void look(int degrees );

}
