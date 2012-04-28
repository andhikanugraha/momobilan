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
public class CrashEvent extends TrackEvent {
    Vehicle perpetrator;
    Vehicle victim;
    
    public CrashEvent(Track track, Vehicle perpetrator, Vehicle victim, int lane, int distance) {
        super(track, perpetrator, lane, distance);

        this.perpetrator = perpetrator;
        this.victim = victim;
    }
}
