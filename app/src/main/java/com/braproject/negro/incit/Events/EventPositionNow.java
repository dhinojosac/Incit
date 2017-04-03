package com.braproject.negro.incit.Events;

import android.location.Location;

/**
 * Created by negro_note on 11-11-2015.
 */
public class EventPositionNow {
    private Location mPosicion;

    public EventPositionNow(Location mPosicion) {
        this.mPosicion = mPosicion;
    }

    public Location getPosicion(){
        return mPosicion;
    }


}
