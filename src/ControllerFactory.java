public class ControllerFactory {
    public ControllerFactory()
    {

    }

    public Controller getNewController(int instrument, NoteSynth midiController)
    {
        return new Controller(instrument, midiController);
    }
}
