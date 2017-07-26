import sun.nio.ch.ThreadPool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by ASUS on 6/9/2017.
 */
public class View extends JFrame implements ActionListener {
    private int maxGap = 20;
    JComboBox horGapComboBox;
    JComboBox verGapComboBox;
    private final GridBagConstraints constraints;
    JButton applyButton;
    JMenuBar menu = new JMenuBar();
    JMenu fileMenu = new JMenu("File");
    private static HashMap<String, Integer> drumInstruments = new HashMap<>();

    private GridLayout gridLayout = new GridLayout(0,2);

    private RythmTickHandler rythmTickHandler;
    private Controller controller;
    private ArrayList<Controller> controllerList;
    private ThreadPool controllerThreadPool;
    private ExecutorService executorService;
    NoteSynth midiController = null;


    public View(String name)
    {
        super(name);
        fillDrumInstruments();
        executorService = Executors.newCachedThreadPool();
        controllerList = new ArrayList<>();
        midiController = new NoteSynth();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        constraints = new GridBagConstraints();
        getContentPane().setLayout(new BorderLayout());
        setResizable(true);

        rythmTickHandler = new RythmTickHandler();
        rythmTickHandler.start();
        addComponentsToPane();
        pack();
        setVisible(true);
    }

    private void fillDrumInstruments() {
        drumInstruments.put("Acoustic Bass Drum",35);
        drumInstruments.put("Bass Drum 1", 36);
        drumInstruments.put("Side Stick", 37);
        drumInstruments.put("Acoustic Snare", 38);
        drumInstruments.put("Hand Clap", 39);
        drumInstruments.put("Electric Snare", 40);
        drumInstruments.put("Low Floor Tom", 41);
        drumInstruments.put("Closed Hi Hat", 42);
        drumInstruments.put("High Floor Tom", 43);
        drumInstruments.put("Pedal Hi-Hat", 44);
        drumInstruments.put("Low Tom", 45);
        drumInstruments.put("Open Hi-Hat", 46);
        drumInstruments.put("Low-Mid Tom", 47);
        drumInstruments.put("Hi-Mid Tom", 48);
        drumInstruments.put("Crash Cymbal 1", 49);
        drumInstruments.put("High Tom", 50);
        drumInstruments.put("Ride Cymbal 1", 51);
        drumInstruments.put("Chinese Cymbal", 52);
        drumInstruments.put("Ride Bell", 53);
        drumInstruments.put("Tambourine", 54);
        drumInstruments.put("Splash Cymbal", 55);
        drumInstruments.put("Cowbell", 56);
        drumInstruments.put("Crash Cymbal 2", 57);
        drumInstruments.put("Vibraslap", 58);
        drumInstruments.put("Ride Cymbal 2", 59);
        drumInstruments.put("Hi Bongo", 60);
        drumInstruments.put("Low Bongo", 61);
        drumInstruments.put("Mute Hi Conga", 62);
        drumInstruments.put("Open Hi Conga", 63);
        drumInstruments.put("Low Conga", 64);
        drumInstruments.put("High Timbale", 65);
        drumInstruments.put("Low Timbale", 66);
        drumInstruments.put("High Agogo", 67);
        drumInstruments.put("Low Agogo", 68);
        drumInstruments.put("Cabasa", 69);
        drumInstruments.put("Maracas", 70);
        drumInstruments.put("Short Whistle", 71);
        drumInstruments.put("Long Whistle", 72);
        drumInstruments.put("Short Guiro", 73);
        drumInstruments.put("Long Guiro", 74);
        drumInstruments.put("Claves", 75);
        drumInstruments.put("Hi Wood Block", 76);
        drumInstruments.put("Low Wood Block", 77);
        drumInstruments.put("Mute Cuica", 78);
        drumInstruments.put("Open Cuica", 79);
        drumInstruments.put("Mute Triangle", 80);
        drumInstruments.put("Open Triangle", 81);

    }

    public void addComponentsToPane() {

        final JPanel compsToExperiment = new JPanel();
        compsToExperiment.setLayout(gridLayout);
        JPanel controls = new JPanel();
        controls.setLayout(new GridLayout(3,3));

        applyButton = new JButton("Show me the rhythm");

        //Set up components preferred size
        JTextArea tempoBPM = new JTextArea();
        tempoBPM.setText("60");
        JButton enterTempo = new JButton("Enter Tempo");
        enterTempo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Controller controller : controllerList) {
                    controller.setTempo(Integer.parseInt(tempoBPM.getText()));
                }
            }
        });

        JButton start = new JButton("Start");
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controllerList.size() != 0)
                {
                    for (Controller controller : controllerList) {
                        if (!controller.running)
                            executorService.execute(controller);
                        else
                            controller.resume();
                    }
                }

                //controllerThread.start();
            }
        });

        JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controllerList.size() != 0)
                {
                    for (Controller controller : controllerList) {
                        controller.stop();
                    }
                }
            }
        });

       // Dimension buttonSize = enterTempo.getPreferredSize();
       // compsToExperiment.setPreferredSize(new Dimension((int)(buttonSize.getWidth() * 2.5)+maxGap,
       //         (int)(buttonSize.getHeight() * 3.5)+maxGap * 2));

        //Add buttons to experiment with Grid Layout

        Map<Integer, JButton> buttonSet = new HashMap<>();

        for (Map.Entry<String, Integer> instrument : drumInstruments.entrySet()) {
            JButton button = new JButton(instrument.getKey());
            buttonSet.put(instrument.getValue(), button);
            compsToExperiment.add(button);
            button.addActionListener(this);
        }

        controls.add(tempoBPM);
        controls.add(start);
        controls.add(applyButton);
        controls.add(enterTempo);
        controls.add(stopButton);
        gridLayout.layoutContainer(compsToExperiment);

        //Process the Apply button press
        applyButton.addActionListener(rythmTickHandler);

        add(compsToExperiment, BorderLayout.NORTH);
        add(new JSeparator(), BorderLayout.CENTER);
        add(controls, BorderLayout.SOUTH);
    }


    public static void main(String[] args) {
        try {
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new View("Sample");
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        JButton b = (JButton)e.getSource();
        Controller controller = new Controller(drumInstruments.get(b.getText()), midiController);
        controllerList.add(controller);
        b.getText();
        if (!controller.running){
            executorService.execute(controller);
        }
        if (controller.paused)
            controller.resume();

        controller.pressingTime = System.currentTimeMillis();
    }

}
