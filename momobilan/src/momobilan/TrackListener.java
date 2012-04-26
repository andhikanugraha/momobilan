/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package momobilan;

/**
 *
 * @author Andhika
 */
public interface TrackListener {
    public void onCrash(TrackEvent event);
    
    public void onTrespass(TrackEvent event);
    
    public void onDie(TrackEvent event);
    
    public void onMove(TrackEvent event);
}
