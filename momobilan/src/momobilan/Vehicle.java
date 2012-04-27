/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package momobilan;

import java.util.HashMap;
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
    
    private Track parentTrack;
    private volatile boolean isActive = false;
    
    /**
     * Kecepatan mobil ditentukan oleh dua properti:
     * - velocity: perpindahan setiap interval
     * - interval: jangka waktu antara perpindahan
     */
    private int velocity;
    private int interval = 1000;
    
    private float acceleration = 0;
    
    private ConcurrentLinkedQueue<TrackListener> listeners;
    
    private HashMap<String,String> attributes;

    /**
     * Menghubungkan mobil dengan dunia trek
     * 
     * @param track
     * @param lane
     * @param distance 
     */
    public void attach(Track track, int lane, int distance) {
        parentTrack = track;
        currentLane = lane;
        currentDistance = distance;
        
        start();
    }
    
    /**
     * Pergerakan default
     * 
     * Tanpa intervensi dari luar, mobil akan bergerak sesuai dengan
     * properti velocity dan interval.
     */
    protected void defaultMotion() throws InterruptedException {
        // Lakukan sebuah pergerakan
        sleep(interval);

        newDistance = currentDistance + velocity;
        velocity += acceleration * velocity;
        
        updateTrack();
    }
    
    /**
     * Berpindah posisi
     * 
     * @param deltaLane
     * @param deltaDistance 
     */
    public void move(int deltaLane, int deltaDistance) {
        newLane = currentLane + deltaLane;
        newDistance = currentDistance + deltaDistance;
        
        updateTrack();
    }
    
    /**
     * Berpindah lajur.
     * Note: Pemanggilan event hasil pemindahan lajur
     *       hanya akan dipanggil dalam iterasi run().
     * @param deltaLane 
     */
    public void changeLane(int deltaLane) {
        newLane = currentLane + deltaLane;
        
        updateTrack();
    }
    
    /**
     * Mengubah kecepatan mobil berdasarkan perpindahan per selang waktu
     * 
     * @param velocity 
     */
    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }
    
    /**
     * Mengubah kecepatan mobil berdasarkan selang waktu per perpindahan
     * 
     * @param interval 
     */
    public void setInterval(int interval) {
        this.interval = interval;
    }
    
    /**
     * Mengubah kecepatan mobil berdasarkan perpindahan dan selang waktu
     * 
     * @param velocity
     * @param interval 
     */
    public void setVelocity(int velocity, int interval) {
        this.velocity = velocity;
        this.interval = interval;
    }
    
    /**
     * Memperbarui kondisi track di mana mobil berada
     * 
     * I.S. parentTrack terdefinisi
     * F.S. Terdaftar perpindahan pada parentTrack, serta event dipanggil.
     */
    protected void updateTrack() {
        parentTrack.registerMovement((int) currentLane, (int) currentDistance, (int) newLane, (int) newDistance);

        currentLane = newLane;
        currentDistance = newDistance;
    }
    
    /**
     * Konkurensi
     * 
     * Secara default, mobil akan bergerak (bermutasi) sesuai defaultMotion()
     */
    @Override
    public void run() {
        try {
            while (isActive) {
                // do stuff
                defaultMotion();
            }
        }
        catch (InterruptedException e) {
            // Interrupted. May be because of various reasons.
        }
    }
    
    /**
     * Dipanggil setelah terjadi perpindahan yang aman.
     */
    public void hasMoved() {
        // Tidak terjadi tabrakan.
        
        // panggil event onCrash di sini
        for (TrackListener listener : listeners) {
            listener.onMove(new TrackEvent(parentTrack, this, currentLane, currentDistance, newLane, newDistance));
        }
    }
    
    /**
     * Menghilangkan keberadaan dan pergerakan mobil.
     */
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
