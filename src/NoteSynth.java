import javax.sound.midi.*;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ASUS on 6/7/2017.
 */
public class NoteSynth extends SwingWorker<Void, View> {
    private static final int SAMPLE_RATE = 22050;
    private static final int MIN_FREQ = 250;
    private static final int MAX_FREQ = 15000;
    private static double MAX_AMPLITUDE = 32760;
    private static AudioFormat format = null;
    private static SourceDataLine line = null;
    public static MidiChannel drumChannel = null;
    public static Receiver receiver = null;
    private static final int VOLUME_CONTROLLER = 127;
    private static Synthesizer synthesizer = null;
    private int tempo = 60; //default value = 60 BPM



    public NoteSynth()
    {
        createOutput();
        try {
            synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            drumChannel = synthesizer.getChannels()[9];
            drumChannel.programChange(0, 15);
            receiver = synthesizer.getReceiver();

            MidiChannel[] channels = synthesizer.getChannels( );
            int channelVol = drumChannel.getController(VOLUME_CONTROLLER);
            drumChannel.controlChange(VOLUME_CONTROLLER, 127);


        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground() throws Exception {
        return null;
    }

    private void createOutput() {
        format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, SAMPLE_RATE, 16, 2, 4, SAMPLE_RATE, false);
        System.out.println("Audio format: " + format);

        try {
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            if(!AudioSystem.isLineSupported(info)) {
                System.out.println("Line does not support: " + format );
                System.exit(0);
            }
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
        } catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            System.exit(0);
        }
    }

    public static void main(String[] args) {



        try {
            synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            drumChannel = synthesizer.getChannels()[9];
            synthesizer.getChannels( )[0].programChange(0, 15);
            receiver = synthesizer.getReceiver();

            MidiChannel[] channels = synthesizer.getChannels( );
            int channelVol = channels[4].getController(VOLUME_CONTROLLER);
            channels[9].controlChange(VOLUME_CONTROLLER, 127);

            int channel = 9;

            for (int j = 0; j < 10; j++) {
//                    playNote(36, 1, 0.3f, channel);
//                    playNote(38, 1, 0.3f, channel);
//                    playNote(38, 1, 0f, channel);
//                    playNote(38, 1, 0.1f, channel);
//                    playNote(38, 1, 0.1f, channel);
//                    playNote(57, 1, 0.01f, channel);
//                    playNote(38, 1, 0.2f, channel);
//                    playNote(53, 1, 0.1f, channel);
                }


            } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public Map<String, Instrument> listInstruments()
    {
        Instrument[] instrument = synthesizer.getAvailableInstruments();
        System.out.println("No. of Instruments: " + instrument.length);
        HashMap<String, Instrument> map = new HashMap<>();

        for (int i=0; i < instrument.length; i++) {
            map.putIfAbsent(instrument[i].getName( ), instrument[i]);

//            Patch p = instrument[i].getPatch();
//            System.out.print("(" + instrument[i].getName( ) +
//                    " <" + p.getBank( ) + "," + p.getProgram( ) + ">) ");
//            if (i%3 ==0)
//                System.out.println( );
        }
        return map;
        //System.out.println( );
    } // end of listInstruments( )


    public void playNote(int note, int duration, float pause, int channel)
    {
        ShortMessage msg = new ShortMessage( );
        ShortMessage volMsg = new ShortMessage( );
            drumChannel.noteOn(note, 70); // 70 is the volume level
            try {

                volMsg.setMessage(ShortMessage.CONTROL_CHANGE, channel, VOLUME_CONTROLLER, 127);
               // receiver.send(volMsg, -1);

                msg.setMessage(ShortMessage.NOTE_ON, channel, note, 70); // 70 is the volume level
                receiver.send(msg, -1); // -1 means play immediately

                Thread.sleep((int)(pause * 1000)); // secs --> ms
            }
            catch (InterruptedException e) {} catch (InvalidMidiDataException e) {
                e.printStackTrace();
            }

        drumChannel.noteOff(note);
    }

    public void play() {
        int maxSize = (int)Math.round((SAMPLE_RATE * format.getFrameSize()) / MIN_FREQ);
        byte[] samples = new byte[maxSize];
        line.start();
        double volume;
        for (int step = 1; step < 10; step++) {
            for (int freq = MIN_FREQ; freq < MAX_FREQ; freq += step) {
                volume = 1.0 - (step/10.0);
                //sendNote(freq, volume, samples);
             }
        }
        line.drain();
        line.stop();
        line.close();


    }

    private void sendNote(int freq, double volLevel, byte[] samples) {
        if ((volLevel < 0.0) || (volLevel > 1.0)) {
            System.out.println("Volume is incorrect");
            volLevel = 0.9;
        }
        double amplitude = volLevel * MAX_AMPLITUDE;

        int numSampleInWave = (int) Math.round(((double) SAMPLE_RATE) / freq );
        int idx = 0;
        for (int i = 0; i < numSampleInWave; i++) {
            double sine = Math.sin(((double) i/numSampleInWave) * 2.0 * Math.PI);
            int sample = (int)(sine * amplitude);

            samples[idx + 0] = (byte) (sample & 0xFF);
            samples[idx + 1] = (byte) ((sample >> 8) & 0xFF);
            samples[idx + 2] = (byte) (sample & 0xFF);
            samples[idx + 3] = (byte) ((sample >> 8) & 0xFF);
            idx += 4;
        }

        int offset = 0;

        while (offset < idx)
            offset += line.write(samples, offset, idx - offset);
    }


    public void setTempo(int tempo) {
        this.tempo = tempo;
    }
}
