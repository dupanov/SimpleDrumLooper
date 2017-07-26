import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ASUS on 6/14/2017.
 */
public class RythmTickHandler extends Thread implements ActionListener {
    List<Long> clicksList;
    double averagedTempo = 0;

    public RythmTickHandler()
    {

    }

    public void run() {
        clicksList = new ArrayList<>();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        clicksList.add(System.currentTimeMillis());
    }

    public void setAvgTempo() {
        if (clicksList.size() >= 2) {
            double sum = 0;
            for (int i = 1; i < clicksList.size(); i++) {
                sum += clicksList.get(i) - clicksList.get(i - 1);
            }
            averagedTempo = sum / ((clicksList.size() - 1.0));
        } else averagedTempo = 0;
    }

    public double getAveragedTempo()
    {
        return averagedTempo;
    }


    public void stopListening() {
        setAvgTempo();
    }


}
