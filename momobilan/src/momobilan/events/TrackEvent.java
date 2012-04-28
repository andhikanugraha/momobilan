/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package momobilan.events;

import momobilan.Track;
import momobilan.Vehicle;

/**
 *
 * @author Andhika
 */
public class TrackEvent {
    private Track track;
    private Vehicle vehicle;
    private int oldLane;
    private int oldDistance;
    private int newLane;
    private int newDistance;

    public TrackEvent(Track track, Vehicle vehicle) {
        this.track = track;
        this.vehicle = vehicle;
    }
    
    public TrackEvent(Track track, Vehicle vehicle, int oldLane, int oldDistance, int newLane, int newDistance) {
        this.track = track;
        this.vehicle = vehicle;
        this.oldLane = oldLane;
        this.oldDistance = oldDistance;
        this.newLane = newLane;
        this.newDistance = newDistance;
    }
    
    public TrackEvent(Track track, Vehicle vehicle, int lane, int distance) {
        this.track = track;
        this.vehicle = vehicle;
        oldLane = newLane = lane;
        newLane = newDistance = distance;
    }
    
    public Track getTrack() {
        return track;
    }
    
    public Vehicle getVehicle() {
        return vehicle;
    }
    
    public int getOldLane() {
        return oldLane;
    }
    
    public int getOldDistance() {
        return oldDistance;
    }
    
    public int getNewLane() {
        return newLane;
    }
    
    public int getNewDistance() {
        return newDistance;
    }
}
