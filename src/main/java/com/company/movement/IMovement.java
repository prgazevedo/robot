package com.company.movement;

public interface IMovement {

    public void move(boolean fwd_direction,int speed, int time );
    public void rotate(boolean west_direction,int speed, int time );
    public void look(int degrees );

}
