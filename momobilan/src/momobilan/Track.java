/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package momobilan;

import momobilan.events.TrackListener;
import java.util.concurrent.ConcurrentLinkedQueue;
import momobilan.events.CrashEvent;
import momobilan.events.ListenerCollection;
import momobilan.events.TrackEvent;

/**
 *
 * @author Andhika
 */
public class Track {
    Vehicle[][] matrix;    
    int laneCount = 5;
    int trackLength = 20;
    
    protected ListenerCollection listeners;
    
    /**
     * Konstruktor utama
     */
    public Track() {
        matrix = new Vehicle[trackLength][laneCount];
    }
    
    /**
     * Konstruktor dengan jumlah lajur dan panjang trek
     * 
     * @param lanes jumlah lajur
     * @param trackLength panjang trek
     */
    public Track(int laneCount, int trackLength) {
        this.laneCount = laneCount;
        this.trackLength = trackLength;

        matrix = new Vehicle[trackLength][laneCount];
    }
    
    /**
     * Mendaftarkan perpindahan benda pada trek
     * 
     * I.S. Pada currentLane dan currentDistance terdefinisi sebuah Vehicle T.
     * F.S. Jika pada newLane dan newDistance terdapat Vehicle lain,
     *        panggil V.hasCrashed().
     *      Jika newLane dan newDistance melebihi boundary atas-bawah track,
     *        panggil V.hasTrespassed().
     *      Else, V dipindahkan ke newLane dan newDistance,
     *        panggil V.hasMoved().
     *      Sesuai dengan event yang terjadi, panggil juga listener track.
     * 
     * @param currentLane lajur mula-mula benda
     * @param currentDistance jarak mula-mula benda
     * @param newLane lajur baru benda
     * @param newDistance jarak baru benda
     */
    public synchronized void registerMovement(int currentLane, int currentDistance,
            int newLane, int newDistance) {
        Vehicle V = matrix[currentDistance][currentLane];
        
        TrackEvent event = new TrackEvent(this, V, currentLane, currentDistance,
                newLane, newDistance);
        
        if (newDistance < 0 || newDistance >= trackLength) {
            V.die();
            V.hasTrespassed();
            listeners.fireEvent("trespass", event);
        }
        else {
            Vehicle W = matrix[newDistance][newLane];
            CrashEvent crashEvent =
                    new CrashEvent(this, V, W, newLane, newDistance);

            if (newLane < 0 || newLane >= laneCount || W != null) {
                V.die();
                // Menabrak tembok atau mobil lain
                listeners.fireEvent("crash", event);
                V.hasCrashed();
                W.hasCrashed();
            }
            else { // W == null
                matrix[newDistance][newLane] = V;
                matrix[currentDistance][currentLane] = null;
                listeners.fireEvent("move", event);
                V.hasMoved();
            }
        }
    }
    
    /**
     * Melahirkan sebuah mobil ke dalam dunia trek.
     * 
     * @param vehicle mobil yang akan dilahirkan
     * @param lane lajur mobil tersebut
     * @param distance jarak mobil tersebut
     */
    public void spawn(Vehicle vehicle, int lane, int distance) {
        matrix[distance][lane] = vehicle;
        
        vehicle.attach(this, lane, distance); // this starts the thread.
        listeners.fireEvent("spawn", new TrackEvent(this, vehicle, lane, distance));
    }
    
    /**
     * Mengembalikan isi matriks trek
     * 
     * @return matriks trek - [distance][lane].
     */
    public Vehicle[][] getMatrix() {
        return matrix;
    }
    
    /**
     * Mengembalikan mobil yang berada pada lane dan distance tertentu,
     * atau null jika tidak ada
     * 
     * @param lane
     * @param distance
     * @return 
     */
    public Vehicle getVehicle(int lane, int distance) {
        return matrix[distance][lane];
    }
}
