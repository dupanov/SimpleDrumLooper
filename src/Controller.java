import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;


/**
 * Created by ASUS on 7/12/2017.
 */
public class Controller implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);
    public volatile boolean running = false;
    public volatile boolean paused = false;
    public ArrayList<InstrumentTiming> instrumentTimingArrayList = new ArrayList<>();
    public long pressingTime;
    public NoteSynth midiController;
    private volatile int tempo = 60;
    private long period;
    private int instrument = 35; //defaukt 35 - Acoustic drum bass


    public Controller(int instrument, NoteSynth midiController)
    {
        this.midiController = midiController;
        this.instrument = instrument;
    }

    @Override
    public void run() {
        long beforeTime, timeDiff, sleepTime;


        running = true;
        while (running) {
            try {
                if (paused) {
                    synchronized(this) {
                        while (paused && running)
                            wait( );
                    }
                }
            } // of try block
            catch (InterruptedException e){}

            midiController.playNote(instrument, 10, 0, 9);
            beforeTime = System.currentTimeMillis();
            timeDiff = System.currentTimeMillis() - beforeTime;
            period = (long) (60000/ tempo);
            sleepTime = period - timeDiff; // time left in this loop
            if (sleepTime <= 0) // update/render took longer than period
                sleepTime = 5; // sleep a bit anyway
            try {
                LOGGER.debug("SleepTime" + String.valueOf(sleepTime));
                Thread.sleep(sleepTime); // in ms
            } catch (InterruptedException ex) {
                running = false;
            }
        }
        System.exit(0);
    }

    public synchronized void stop()
    {
        paused = true;
    }

    public synchronized void resume()
    {
        paused = false;
        notify( );
    }


    public synchronized void setTempo(int tempo) {
        this.tempo = tempo;
        LOGGER.info(String.valueOf(this.tempo));
    }

    private class InstrumentTiming {

    }
    }
