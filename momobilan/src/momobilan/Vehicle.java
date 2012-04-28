/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package momobilan;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Vehicle
 * 
 * Representasi sebuah mobil yang berjalan pada track.
 * Diimplementasikan sebagai thread untuk eksekusi konkuren.
 * 
 * @author Andhika Nugraha andhika.nugraha@gmail.com
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
    
    private HashMap<String,Object> attributes;

    /**
     * Menghubungkan mobil dengan dunia trek.
     * 
     * I.S. Track terdefinisi, lane dan distance valid (tidak out-of-bounds)
     * F.S. properti parentTrack, currentLane, currentDistance terisi.
     *      Eksekusi Thread dijalankan.
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
     * Pergerakan default.
     * 
     * Tanpa intervensi dari luar, mobil akan bergerak sesuai dengan
     * properti velocity dan interval.
     */
    protected void defaultMotion() throws InterruptedException {
        // Lakukan sebuah pergerakan
        sleep(interval);

        move(0, velocity);
                
        velocity += acceleration * velocity;
    }
    
    /**
     * Berpindah posisi.
     * 
     * @param deltaLane
     * @param deltaDistance 
     */
    public void move(int deltaLane, int deltaDistance) {
        if (isActive && deltaLane != 0 && deltaDistance != 0) {
            newLane = currentLane + deltaLane;
            newDistance = currentDistance + deltaDistance;

            updateTrack();
        }
    }
    
    /**
     * Berpindah lajur.
     * 
     * @param deltaLane 
     */
    public void changeLane(int deltaLane) {
        move(deltaLane, 0);
    }
    
    /**
     * Mengembalikan nilai perpindahan mobil per interval.
     * 
     * @return velocity
     */
    public int getVelocity() {
        return velocity;
    }
    
    /**
     * Mengembalikan nilai interval.
     * 
     * @return interval
     */
    public int getInterval() {
        return interval;
    }
    
    /**
     * Mengembalikan nilai percepatan.
     * 
     * @return 
     */
    public float getAcceleration() {
        return acceleration;
    }
    
    /**
     * Mengubah kecepatan mobil berdasarkan perpindahan per selang waktu.
     * 
     * @param velocity 
     */
    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }
    
    /**
     * Mengubah kecepatan mobil berdasarkan selang waktu per perpindahan.
     * 
     * @param interval 
     */
    public void setInterval(int interval) {
        this.interval = interval;
    }
    
    /**
     * Mengubah kecepatan mobil berdasarkan perpindahan dan selang waktu.
     * 
     * @param velocity
     * @param interval 
     */
    public void setVelocity(int velocity, int interval) {
        this.velocity = velocity;
        this.interval = interval;
    }
    
    /**
     * Mengubah percepatan mobil.
     * 
     * @param acceleration percepatan mobil yang baru.
     */
    public void setAcceleration(float acceleration) {
        this.acceleration = acceleration;
    }
    
    /**
     * Memperbarui status track di mana mobil berada.
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
                // Lakukan pergerakan default.
                // Pergerakan lainnya semestinya dilakukan dengan method lain
                // dan dari thread lain.
                defaultMotion();
            }
        }
        catch (InterruptedException e) {
            // Interrupted. Tidak perlu dihiraukan.
        }
    }
    
    /**
     * Dipanggil setelah terjadi perpindahan yang aman.
     */
    public void hasMoved() {
        // Tidak terjadi tabrakan.
        
        // panggil event onCrash di sini
        for (TrackListener listener : listeners) {
            listener.onMove(new TrackEvent(parentTrack, this, currentLane,
                    currentDistance, newLane, newDistance));
        }
    }
    
    /**
     * Menghilangkan keberadaan dan pergerakan mobil.
     */
    public void die() {
        isActive = false;
        
        for (TrackListener listener : listeners) {
            listener.onDie(new TrackEvent(parentTrack, this, currentLane,
                    currentDistance, newLane, newDistance));
        }
    }
    
    /**
     * Dipanggil apabila terjadi tabrakan.
     * 
     * Tabrakan adalah kejadian di mana mobil berpindah ke lokasi:
     * a. dinding
     * b. sebuah mobil lainnya
     */
    public void hasCrashed() {
        // Terjadi tabrakan.
        die();
        
        // panggil event onCrash di sini
        for (TrackListener listener : listeners) {
            listener.onCrash(new TrackEvent(parentTrack, this, currentLane,
                    currentDistance, newLane, newDistance));
        }
    }
    
    /**
     * Dipanggil apabila mobil melewati batas trek.
     * 
     * Batas trek di sini berarti batas atas/bawah, atau panjang lintasan trek.
     */
    public void hasTrespassed() {
        // Menghilang saja
        die();
        
        // panggil event onTrespass di sini
        for (TrackListener listener : listeners) {
            listener.onTrespass(new TrackEvent(parentTrack, this, currentLane, currentDistance, newLane, newDistance));
        }
    }
    
    /**
     * Membaca nilai sebuah atribut.
     * 
     * Sebuah atribut dapat mewakili sifat auksilier mobil,
     * seperti warna, bentuk, dll. Dengan kata lain, atribut adalah
     * sifat mobil yang tidak menentukan pergerakannya.
     * 
     * @param key kunci atribut yang dicari.
     * @return nilai atribut tersebut
     */
    public Object getAttribute(String key) {
        return attributes.get(key);
    }
    
    /**
     * Mengubah nilai sebuah atribut.
     * 
     * Sebuah atribut dapat mewakili sifat auksilier mobil,
     * seperti warna, bentuk, dll. Dengan kata lain, atribut adalah
     * sifat mobil yang tidak menentukan pergerakannya.
     * 
     * @param key kunci atribut yang ingin diubah
     * @param value nilai atribut yang baru
     */
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }
}
