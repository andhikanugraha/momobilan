/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package momobilan.events;

import java.util.EventListener;

/**
 *
 * @author Andhika
 */
public class TrackListener implements EventListener {
    public void crash(TrackEvent event) {}
    
    public void trespass(TrackEvent event) {}
    
    public void die(TrackEvent event) {}
    
    public void move(TrackEvent event) {}
    
    public void spawn(TrackEvent spawn) {}
}
