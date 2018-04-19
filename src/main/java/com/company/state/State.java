package com.company.state;

public enum State {
    NONE(0),WAITING_ANSWER(1), READY(2);
    private int state;
    private State(int i){this.state = i;}
    public State getState(){
        try {
            return State.values()[state];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return State.NONE;
    }
}



