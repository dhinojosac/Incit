package com.braproject.negro.incit.Events;

/**
 * Created by negro-PC on 31-Mar-17.
 */

public class EventIncidentsReady {
    private boolean state;
    public void setStateSuccess(){
        state = true;
    }
    public void setStateFail(){
        state = false;
    }
    public boolean getState(){
        return state;
    }
}
