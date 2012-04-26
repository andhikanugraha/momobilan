/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package momobilan;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author Andhika
 */
public class Vehicle extends Thread {
    public int currentLane;
    public int currentDistance;
    public int newLane;
    public int newDistance;
    
    protected Track parentTrack;
    protected int interval = 500;
    
    private boolean isActive = false;
    
    private ConcurrentLinkedQueue<TrackListener> listeners;

    public void attach(Track track, int lane, int distance) {
        parentTrack = track;
        currentLane = lane;
        currentDistance = distance;
        
        start();
    }
    
    protected void move() {
        // Lakukan sebuah pergerakan
        // I.S. Terdefinisi currentLane, currentDistance
        // F.S. Terdefinisi newLane, newDistance.
    }
    
    @Override
    public void run() {
        try {
            while (isActive) {
                // do stuff
                Thread.sleep(interval);
                
                move();
                
                parentTrack.registerMovement(currentLane, currentDistance, newLane, newDistance);
                
                currentLane = newLane;
                currentDistance = newDistance;
            }
        }
        catch (InterruptedException e) {
            // Interrupted. May be because of various reasons.
        }
    }
    
    public void hasMoved() {
        // Tidak terjadi tabrakan.
        
        // panggil event onCrash di sini
        for (TrackListener listener : listeners) {
            listener.onMove(new TrackEvent(parentTrack, this, currentLane, currentDistance, newLane, newDistance));
        }
    }
    
    public void die() {
        isActive = false;
        
        for (TrackListener listener : listeners) {
            listener.onDie(new TrackEvent(parentTrack, this, currentLane, currentDistance, newLane, newDistance));
        }
    }
    
    public void hasCrashed() {
        // Terjadi tabrakan.
        die();
        
        // panggil event onCrash di sini
        for (TrackListener listener : listeners) {
            listener.onCrash(new TrackEvent(parentTrack, this, currentLane, currentDistance, newLane, newDistance));
        }
    }
    
    public void hasTrespassed() {
        // Menghilang saja
        die();
        
        // panggil event onDisappear di sini
        for (TrackListener listener : listeners) {
            listener.onTrespass(new TrackEvent(parentTrack, this, currentLane, currentDistance, newLane, newDistance));
        }
    }
}
